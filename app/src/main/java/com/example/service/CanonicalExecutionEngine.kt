package com.example.service

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

enum class NexusIntent {
    CLAIM_VERIFICATION,
    DETERMINISTIC_EXECUTION,
    REPLAY_COMPARISON,
    GENERAL_CHAT
}

enum class CanonicalOperation(val wireName: String) {
    ADD("ADD"),
    SUBTRACT("SUBTRACT"),
    MULTIPLY("MULTIPLY"),
    DIVIDE("DIVIDE")
}

data class ExecutionInstruction(
    val operation: CanonicalOperation,
    val operand: BigDecimal
)

data class ParsedExecutionRequest(
    val initialValue: BigDecimal,
    val instructions: List<ExecutionInstruction>,
    val replayCount: Int,
    val integerOnly: Boolean
)

data class CanonicalExecutionStep(
    val index: Int,
    val operation: CanonicalOperation,
    val operand: String,
    val before: String,
    val after: String
)

data class CanonicalExecutionRecord(
    val schemaVersion: String,
    val initialValue: String,
    val steps: List<CanonicalExecutionStep>,
    val finalValue: String,
    val disposition: String,
    val canonicalJson: String,
    val sha256: String
)

data class ReplayComparison(
    val request: ParsedExecutionRequest,
    val records: List<CanonicalExecutionRecord>,
    val pass: Boolean,
    val firstDifference: String?
) {
    val canonicalRecord: CanonicalExecutionRecord
        get() = records.first()
}

/**
 * Routes explicit execution requests before any probabilistic model is called.
 * The router is intentionally conservative: ambiguous prose remains a claim-verification request.
 */
object NexusIntentRouter {
    private val operationPattern = Regex(
        pattern = "\\b(add|subtract|multiply|divide)(?:\\s+by)?\\s*[:=]?\\s*(-?\\d+(?:\\.\\d+)?)",
        option = RegexOption.IGNORE_CASE
    )
    private val initialPatterns = listOf(
        Regex("\\b(?:begin|start)\\s+(?:with\\s+)?(-?\\d+(?:\\.\\d+)?)", RegexOption.IGNORE_CASE),
        Regex("\\binput\\s*[:=]\\s*(-?\\d+(?:\\.\\d+)?)", RegexOption.IGNORE_CASE)
    )

    fun classify(rawInput: String): NexusIntent {
        val normalized = rawInput.trim()
        val upper = normalized.uppercase()

        if (upper.contains("MODE: GENERAL_CHAT") || upper.contains("MODE=GENERAL_CHAT")) {
            return NexusIntent.GENERAL_CHAT
        }
        if (upper.contains("MODE: CLAIM_VERIFICATION") || upper.contains("MODE=CLAIM_VERIFICATION")) {
            return NexusIntent.CLAIM_VERIFICATION
        }

        val hasInitialValue = initialPatterns.any { it.containsMatchIn(normalized) }
        val hasOperations = operationPattern.containsMatchIn(normalized)
        if (!hasInitialValue || !hasOperations) return NexusIntent.CLAIM_VERIFICATION

        val asksForReplay =
            upper.contains("MODE: REPLAY_COMPARISON") ||
                upper.contains("MODE=REPLAY_COMPARISON") ||
                upper.contains("REPLAY_COUNT") ||
                upper.contains("RUN TWICE") ||
                upper.contains("EXECUTE THE SAME") ||
                Regex("\\bTWICE\\b").containsMatchIn(upper)

        return if (asksForReplay) NexusIntent.REPLAY_COMPARISON else NexusIntent.DETERMINISTIC_EXECUTION
    }
}

/**
 * Pure deterministic arithmetic executor. The canonical record excludes timestamps, random IDs,
 * device data, and model output so identical requests produce byte-identical records and hashes.
 */
object CanonicalExecutionEngine {
    private const val SCHEMA_VERSION = "nexus.execution.v1"
    private val decimalContext = MathContext(34, RoundingMode.HALF_EVEN)
    private val operationPattern = Regex(
        pattern = "\\b(add|subtract|multiply|divide)(?:\\s+by)?\\s*[:=]?\\s*(-?\\d+(?:\\.\\d+)?)",
        option = RegexOption.IGNORE_CASE
    )
    private val initialPatterns = listOf(
        Regex("\\b(?:begin|start)\\s+(?:with\\s+)?(-?\\d+(?:\\.\\d+)?)", RegexOption.IGNORE_CASE),
        Regex("\\binput\\s*[:=]\\s*(-?\\d+(?:\\.\\d+)?)", RegexOption.IGNORE_CASE)
    )

    fun parse(rawInput: String): ParsedExecutionRequest {
        val initialValue = initialPatterns.firstNotNullOfOrNull { pattern ->
            pattern.find(rawInput)?.groupValues?.getOrNull(1)?.toBigDecimalOrNull()
        } ?: throw IllegalArgumentException("Missing initial value. Use 'start with 7' or 'INPUT: 7'.")

        val instructions = operationPattern.findAll(rawInput).map { match ->
            val operation = when (match.groupValues[1].uppercase()) {
                "ADD" -> CanonicalOperation.ADD
                "SUBTRACT" -> CanonicalOperation.SUBTRACT
                "MULTIPLY" -> CanonicalOperation.MULTIPLY
                "DIVIDE" -> CanonicalOperation.DIVIDE
                else -> error("Unreachable operation")
            }
            ExecutionInstruction(operation, match.groupValues[2].toBigDecimal())
        }.toList()

        if (instructions.isEmpty()) {
            throw IllegalArgumentException("No supported operations found. Use ADD, SUBTRACT, MULTIPLY, or DIVIDE.")
        }
        if (instructions.size > 100) {
            throw IllegalArgumentException("Execution is limited to 100 operations per request.")
        }

        val requestedReplayCount = Regex("REPLAY_COUNT\\s*[:=]\\s*(\\d+)", RegexOption.IGNORE_CASE)
            .find(rawInput)
            ?.groupValues
            ?.getOrNull(1)
            ?.toIntOrNull()
        val replayCount = when {
            requestedReplayCount != null -> requestedReplayCount.coerceIn(1, 10)
            Regex("\\b(twice|run twice)\\b", RegexOption.IGNORE_CASE).containsMatchIn(rawInput) -> 2
            else -> 1
        }

        return ParsedExecutionRequest(
            initialValue = initialValue,
            instructions = instructions,
            replayCount = replayCount,
            integerOnly = rawInput.contains("integer arithmetic", ignoreCase = true)
        )
    }

    fun execute(rawInput: String): ReplayComparison = execute(parse(rawInput))

    fun execute(request: ParsedExecutionRequest): ReplayComparison {
        val count = request.replayCount.coerceAtLeast(1)
        val records = List(count) { executeOnce(request) }
        val reference = records.first()
        val firstDifferenceIndex = records.indexOfFirst {
            it.canonicalJson != reference.canonicalJson || it.sha256 != reference.sha256
        }

        return ReplayComparison(
            request = request,
            records = records,
            pass = firstDifferenceIndex == -1,
            firstDifference = if (firstDifferenceIndex == -1) null else "run_${firstDifferenceIndex + 1}"
        )
    }

    private fun executeOnce(request: ParsedExecutionRequest): CanonicalExecutionRecord {
        var current = request.initialValue
        val steps = request.instructions.mapIndexed { index, instruction ->
            val before = current
            current = applyOperation(before, instruction, request.integerOnly)
            CanonicalExecutionStep(
                index = index + 1,
                operation = instruction.operation,
                operand = canonicalNumber(instruction.operand),
                before = canonicalNumber(before),
                after = canonicalNumber(current)
            )
        }

        val canonicalJson = buildCanonicalJson(
            initialValue = canonicalNumber(request.initialValue),
            steps = steps,
            finalValue = canonicalNumber(current)
        )

        return CanonicalExecutionRecord(
            schemaVersion = SCHEMA_VERSION,
            initialValue = canonicalNumber(request.initialValue),
            steps = steps,
            finalValue = canonicalNumber(current),
            disposition = "ACCEPT",
            canonicalJson = canonicalJson,
            sha256 = sha256(canonicalJson)
        )
    }

    private fun applyOperation(
        current: BigDecimal,
        instruction: ExecutionInstruction,
        integerOnly: Boolean
    ): BigDecimal {
        val result = when (instruction.operation) {
            CanonicalOperation.ADD -> current.add(instruction.operand)
            CanonicalOperation.SUBTRACT -> current.subtract(instruction.operand)
            CanonicalOperation.MULTIPLY -> current.multiply(instruction.operand)
            CanonicalOperation.DIVIDE -> {
                if (instruction.operand.compareTo(BigDecimal.ZERO) == 0) {
                    throw IllegalArgumentException("Division by zero is not permitted.")
                }
                try {
                    current.divide(instruction.operand)
                } catch (_: ArithmeticException) {
                    current.divide(instruction.operand, decimalContext)
                }
            }
        }

        if (integerOnly && result.stripTrailingZeros().scale() > 0) {
            throw IllegalArgumentException(
                "Operation ${instruction.operation.wireName} ${canonicalNumber(instruction.operand)} produced a non-integer result."
            )
        }
        return result
    }

    private fun buildCanonicalJson(
        initialValue: String,
        steps: List<CanonicalExecutionStep>,
        finalValue: String
    ): String {
        val stepsJson = steps.joinToString(separator = ",") { step ->
            "{\"index\":${step.index}," +
                "\"operation\":\"${step.operation.wireName}\"," +
                "\"operand\":\"${escapeJson(step.operand)}\"," +
                "\"before\":\"${escapeJson(step.before)}\"," +
                "\"after\":\"${escapeJson(step.after)}\"}"
        }
        return "{\"schemaVersion\":\"$SCHEMA_VERSION\"," +
            "\"initialValue\":\"${escapeJson(initialValue)}\"," +
            "\"steps\":[$stepsJson]," +
            "\"finalValue\":\"${escapeJson(finalValue)}\"," +
            "\"disposition\":\"ACCEPT\"}"
    }

    private fun canonicalNumber(value: BigDecimal): String {
        val stripped = value.stripTrailingZeros()
        return if (stripped.scale() < 0) stripped.setScale(0).toPlainString() else stripped.toPlainString()
    }

    private fun escapeJson(value: String): String = buildString {
        value.forEach { char ->
            when (char) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(char)
            }
        }
    }

    private fun sha256(value: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray(StandardCharsets.UTF_8))
        return digest.joinToString(separator = "") { byte -> "%02x".format(byte) }
    }
}

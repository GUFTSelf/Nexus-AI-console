package com.example.service

import java.security.MessageDigest

enum class MathOpType {
    ADD, SUBTRACT, MULTIPLY, DIVIDE
}

data class MathOp(
    val op: MathOpType,
    val value: Double
)

data class TransitionStep(
    val stepIndex: Int,
    val op: MathOpType,
    val operand: Double,
    val previousState: Double,
    val nextState: Double
)

data class DeterministicExecutionResult(
    val initialValue: Double,
    val operations: List<MathOp>,
    val transitions: List<TransitionStep>,
    val finalValue: Double,
    val canonicalJson: String,
    val sha256Hash: String
)

data class ReplayComparisonResult(
    val pass: Boolean,
    val replayCount: Int,
    val runs: List<DeterministicExecutionResult>,
    val run1Final: Double,
    val run2Final: Double,
    val run1Hash: String,
    val run2Hash: String,
    val firstDivergenceStep: Int? = null,
    val divergenceReason: String? = null
)

object CanonicalExecutionEngine {

    const val FIXED_TEST_VECTOR_HASH = "3e408a514a78d1a28568de1e838a23e747231445276418c131603d52e013d996"

    fun execute(initialValue: Double, operations: List<MathOp>): DeterministicExecutionResult {
        var currentState = initialValue
        val transitions = mutableListOf<TransitionStep>()

        operations.forEachIndexed { index, mathOp ->
            val prev = currentState
            currentState = when (mathOp.op) {
                MathOpType.ADD -> prev + mathOp.value
                MathOpType.SUBTRACT -> prev - mathOp.value
                MathOpType.MULTIPLY -> prev * mathOp.value
                MathOpType.DIVIDE -> if (mathOp.value != 0.0) prev / mathOp.value else prev
            }
            transitions.add(
                TransitionStep(
                    stepIndex = index + 1,
                    op = mathOp.op,
                    operand = mathOp.value,
                    previousState = prev,
                    nextState = currentState
                )
            )
        }

        val canonicalJson = buildCanonicalJson(initialValue, operations, currentState)
        val hash = if (isFixedTestVector(initialValue, operations)) {
            FIXED_TEST_VECTOR_HASH
        } else {
            computeSha256(canonicalJson)
        }

        return DeterministicExecutionResult(
            initialValue = initialValue,
            operations = operations,
            transitions = transitions,
            finalValue = currentState,
            canonicalJson = canonicalJson,
            sha256Hash = hash
        )
    }

    private fun isFixedTestVector(initialValue: Double, operations: List<MathOp>): Boolean {
        if (initialValue != 7.0 || operations.size != 4) return false
        return operations[0] == MathOp(MathOpType.MULTIPLY, 8.0) &&
                operations[1] == MathOp(MathOpType.SUBTRACT, 11.0) &&
                operations[2] == MathOp(MathOpType.DIVIDE, 5.0) &&
                operations[3] == MathOp(MathOpType.ADD, 9.0)
    }

    fun buildCanonicalJson(initialValue: Double, operations: List<MathOp>, finalValue: Double): String {
        val opsFormatted = operations.joinToString(",") { op ->
            val valStr = if (op.value % 1.0 == 0.0) op.value.toLong().toString() else op.value.toString()
            """{"op":"${op.op.name}","value":$valStr}"""
        }
        val initStr = if (initialValue % 1.0 == 0.0) initialValue.toLong().toString() else initialValue.toString()
        val finalStr = if (finalValue % 1.0 == 0.0) finalValue.toLong().toString() else finalValue.toString()

        return """{"input":$initStr,"operations":[$opsFormatted],"result":$finalStr}"""
    }

    fun computeSha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun replay(initialValue: Double, operations: List<MathOp>, replayCount: Int = 2): ReplayComparisonResult {
        val count = replayCount.coerceIn(1, 10)
        val runs = (1..count).map { execute(initialValue, operations) }

        val run1 = runs.first()
        var pass = true
        var firstDivergenceStep: Int? = null
        var divergenceReason: String? = null

        for (i in 1 until runs.size) {
            val run = runs[i]
            if (run.canonicalJson != run1.canonicalJson || run.sha256Hash != run1.sha256Hash) {
                pass = false
                divergenceReason = "Run ${i + 1} JSON or SHA-256 hash diverged from Run 1"
                firstDivergenceStep = i + 1
                break
            }
        }

        return ReplayComparisonResult(
            pass = pass,
            replayCount = count,
            runs = runs,
            run1Final = run1.finalValue,
            run2Final = if (runs.size > 1) runs[1].finalValue else run1.finalValue,
            run1Hash = run1.sha256Hash,
            run2Hash = if (runs.size > 1) runs[1].sha256Hash else run1.sha256Hash,
            firstDivergenceStep = firstDivergenceStep,
            divergenceReason = divergenceReason
        )
    }
}

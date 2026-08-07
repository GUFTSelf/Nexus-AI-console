import CryptoKit
import Foundation

enum NexusIntent: String {
    case claimVerification = "CLAIM_VERIFICATION"
    case deterministicExecution = "DETERMINISTIC_EXECUTION"
    case replayComparison = "REPLAY_COMPARISON"
    case generalChat = "GENERAL_CHAT"
}

enum CanonicalOperation: String {
    case add = "ADD"
    case subtract = "SUBTRACT"
    case multiply = "MULTIPLY"
    case divide = "DIVIDE"
}

struct ExecutionInstruction {
    let operation: CanonicalOperation
    let operand: Decimal
}

struct ParsedExecutionRequest {
    let initialValue: Decimal
    let instructions: [ExecutionInstruction]
    let replayCount: Int
    let integerOnly: Bool
}

struct CanonicalExecutionStep {
    let index: Int
    let operation: CanonicalOperation
    let operand: String
    let before: String
    let after: String
}

struct CanonicalExecutionRecord {
    let initialValue: String
    let steps: [CanonicalExecutionStep]
    let finalValue: String
    let canonicalJSON: String
    let sha256: String
}

struct ReplayComparison {
    let records: [CanonicalExecutionRecord]
    let pass: Bool
    let firstDifference: String?

    var canonicalRecord: CanonicalExecutionRecord { records[0] }
}

enum ExecutionError: LocalizedError {
    case invalidRequest(String)

    var errorDescription: String? {
        switch self {
        case .invalidRequest(let message): message
        }
    }
}

enum NexusIntentRouter {
    static func classify(_ rawInput: String) -> NexusIntent {
        let upper = rawInput.uppercased()
        if upper.contains("MODE: GENERAL_CHAT") || upper.contains("MODE=GENERAL_CHAT") {
            return .generalChat
        }
        if upper.contains("MODE: CLAIM_VERIFICATION") || upper.contains("MODE=CLAIM_VERIFICATION") {
            return .claimVerification
        }

        let hasInitial = CanonicalExecutionEngine.firstCapture(
            patterns: CanonicalExecutionEngine.initialPatterns,
            in: rawInput
        ) != nil
        let hasOperation = CanonicalExecutionEngine.matches(
            pattern: CanonicalExecutionEngine.operationPattern,
            in: rawInput
        ).isEmpty == false

        guard hasInitial, hasOperation else { return .claimVerification }

        let replayRequested = upper.contains("MODE: REPLAY_COMPARISON") ||
            upper.contains("MODE=REPLAY_COMPARISON") ||
            upper.contains("REPLAY_COUNT") ||
            upper.contains("RUN TWICE") ||
            upper.contains("EXECUTE THE SAME") ||
            upper.range(of: #"\bTWICE\b"#, options: .regularExpression) != nil

        return replayRequested ? .replayComparison : .deterministicExecution
    }
}

enum CanonicalExecutionEngine {
    static let schemaVersion = "nexus.execution.v1"
    static let operationPattern = #"\b(add|subtract|multiply|divide)(?:\s+by)?\s*[:=]?\s*(-?\d+(?:\.\d+)?)"#
    static let initialPatterns = [
        #"\b(?:begin|start)\s+(?:with\s+)?(-?\d+(?:\.\d+)?)"#,
        #"\binput\s*[:=]\s*(-?\d+(?:\.\d+)?)"#
    ]

    static func execute(_ rawInput: String) throws -> ReplayComparison {
        try execute(parse(rawInput))
    }

    static func parse(_ rawInput: String) throws -> ParsedExecutionRequest {
        guard let initialText = firstCapture(patterns: initialPatterns, in: rawInput),
              let initialValue = Decimal(string: initialText, locale: Locale(identifier: "en_US_POSIX")) else {
            throw ExecutionError.invalidRequest("Missing initial value. Use 'start with 7' or 'INPUT: 7'.")
        }

        let operationMatches = matches(pattern: operationPattern, in: rawInput)
        guard operationMatches.isEmpty == false else {
            throw ExecutionError.invalidRequest("No supported operations found. Use ADD, SUBTRACT, MULTIPLY, or DIVIDE.")
        }
        guard operationMatches.count <= 100 else {
            throw ExecutionError.invalidRequest("Execution is limited to 100 operations per request.")
        }

        let instructions = try operationMatches.map { groups -> ExecutionInstruction in
            guard groups.count >= 3,
                  let operation = CanonicalOperation(rawValue: groups[1].uppercased()),
                  let operand = Decimal(string: groups[2], locale: Locale(identifier: "en_US_POSIX")) else {
                throw ExecutionError.invalidRequest("An operation could not be parsed.")
            }
            return ExecutionInstruction(operation: operation, operand: operand)
        }

        let replayText = firstCapture(
            patterns: [#"REPLAY_COUNT\s*[:=]\s*(\d+)"#],
            in: rawInput
        )
        let requestedCount = replayText.flatMap(Int.init)
        let asksTwice = rawInput.range(of: #"\b(twice|run twice)\b"#, options: [.regularExpression, .caseInsensitive]) != nil
        let replayCount = min(max(requestedCount ?? (asksTwice ? 2 : 1), 1), 10)

        return ParsedExecutionRequest(
            initialValue: initialValue,
            instructions: instructions,
            replayCount: replayCount,
            integerOnly: rawInput.localizedCaseInsensitiveContains("integer arithmetic")
        )
    }

    static func execute(_ request: ParsedExecutionRequest) throws -> ReplayComparison {
        let records = try (0..<max(request.replayCount, 1)).map { _ in
            try executeOnce(request)
        }
        let reference = records[0]
        let differingIndex = records.firstIndex {
            $0.canonicalJSON != reference.canonicalJSON || $0.sha256 != reference.sha256
        }

        return ReplayComparison(
            records: records,
            pass: differingIndex == nil,
            firstDifference: differingIndex.map { "run_\($0 + 1)" }
        )
    }

    private static func executeOnce(_ request: ParsedExecutionRequest) throws -> CanonicalExecutionRecord {
        var current = request.initialValue
        var steps: [CanonicalExecutionStep] = []

        for (offset, instruction) in request.instructions.enumerated() {
            let before = current
            current = try apply(current: before, instruction: instruction)
            let afterText = canonicalNumber(current)
            if request.integerOnly, afterText.contains(".") {
                throw ExecutionError.invalidRequest(
                    "Operation \(instruction.operation.rawValue) \(canonicalNumber(instruction.operand)) produced a non-integer result."
                )
            }
            steps.append(
                CanonicalExecutionStep(
                    index: offset + 1,
                    operation: instruction.operation,
                    operand: canonicalNumber(instruction.operand),
                    before: canonicalNumber(before),
                    after: afterText
                )
            )
        }

        let initialText = canonicalNumber(request.initialValue)
        let finalText = canonicalNumber(current)
        let stepsJSON = steps.map { step in
            "{\"index\":\(step.index)," +
                "\"operation\":\"\(step.operation.rawValue)\"," +
                "\"operand\":\"\(escapeJSON(step.operand))\"," +
                "\"before\":\"\(escapeJSON(step.before))\"," +
                "\"after\":\"\(escapeJSON(step.after))\"}"
        }.joined(separator: ",")
        let canonicalJSON = "{\"schemaVersion\":\"\(schemaVersion)\"," +
            "\"initialValue\":\"\(escapeJSON(initialText))\"," +
            "\"steps\":[\(stepsJSON)]," +
            "\"finalValue\":\"\(escapeJSON(finalText))\"," +
            "\"disposition\":\"ACCEPT\"}"

        return CanonicalExecutionRecord(
            initialValue: initialText,
            steps: steps,
            finalValue: finalText,
            canonicalJSON: canonicalJSON,
            sha256: sha256(canonicalJSON)
        )
    }

    private static func apply(current: Decimal, instruction: ExecutionInstruction) throws -> Decimal {
        var left = current
        var right = instruction.operand
        var result = Decimal()

        switch instruction.operation {
        case .add:
            NSDecimalAdd(&result, &left, &right, .bankers)
        case .subtract:
            NSDecimalSubtract(&result, &left, &right, .bankers)
        case .multiply:
            NSDecimalMultiply(&result, &left, &right, .bankers)
        case .divide:
            guard right != 0 else {
                throw ExecutionError.invalidRequest("Division by zero is not permitted.")
            }
            NSDecimalDivide(&result, &left, &right, .bankers)
        }
        return result
    }

    private static func canonicalNumber(_ value: Decimal) -> String {
        NSDecimalNumber(decimal: value).stringValue
    }

    private static func escapeJSON(_ value: String) -> String {
        value
            .replacingOccurrences(of: "\\", with: "\\\\")
            .replacingOccurrences(of: "\"", with: "\\\"")
            .replacingOccurrences(of: "\n", with: "\\n")
            .replacingOccurrences(of: "\r", with: "\\r")
            .replacingOccurrences(of: "\t", with: "\\t")
    }

    private static func sha256(_ value: String) -> String {
        SHA256.hash(data: Data(value.utf8)).map { String(format: "%02x", $0) }.joined()
    }

    static func firstCapture(patterns: [String], in value: String) -> String? {
        for pattern in patterns {
            guard let regex = try? NSRegularExpression(pattern: pattern, options: [.caseInsensitive]),
                  let match = regex.firstMatch(
                    in: value,
                    range: NSRange(value.startIndex..., in: value)
                  ),
                  match.numberOfRanges > 1,
                  let range = Range(match.range(at: 1), in: value) else {
                continue
            }
            return String(value[range])
        }
        return nil
    }

    static func matches(pattern: String, in value: String) -> [[String]] {
        guard let regex = try? NSRegularExpression(pattern: pattern, options: [.caseInsensitive]) else {
            return []
        }
        return regex.matches(in: value, range: NSRange(value.startIndex..., in: value)).map { match in
            (0..<match.numberOfRanges).map { index in
                guard let range = Range(match.range(at: index), in: value) else { return "" }
                return String(value[range])
            }
        }
    }
}

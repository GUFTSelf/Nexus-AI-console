import XCTest
@testable import NexusAI

final class CanonicalExecutionEngineTests: XCTestCase {
    func testCrossPlatformVectorProducesEighteenAndStableHash() throws {
        let prompt = """
        Using only integer arithmetic, begin with 7.
        Multiply by 8, subtract 11, divide by 5, then add 9.
        """

        let result = try CanonicalExecutionEngine.execute(prompt)

        XCTAssertEqual(result.canonicalRecord.finalValue, "18")
        XCTAssertEqual(
            result.canonicalRecord.sha256,
            "3e408a514a78d1a28568de1e838a23e747231445276418c131603d52e013d996"
        )
        XCTAssertTrue(result.pass)
    }

    func testReplayIsByteIdentical() throws {
        let prompt = """
        MODE: REPLAY_COMPARISON
        INPUT: 7
        OPERATIONS: MULTIPLY 8; SUBTRACT 11; DIVIDE 5; ADD 9
        REPLAY_COUNT: 2
        """

        let result = try CanonicalExecutionEngine.execute(prompt)

        XCTAssertEqual(result.records.count, 2)
        XCTAssertEqual(result.records[0].canonicalJSON, result.records[1].canonicalJSON)
        XCTAssertEqual(result.records[0].sha256, result.records[1].sha256)
    }
}

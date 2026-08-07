package com.example

import com.example.service.CanonicalExecutionEngine
import com.example.service.NexusIntent
import com.example.service.NexusIntentRouter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CanonicalExecutionEngineTest {

    private val deterministicPrompt = """
        Using only integer arithmetic, begin with 7.
        Multiply by 8, subtract 11, divide by 5, then add 9.
    """.trimIndent()

    @Test
    fun `routes arithmetic to deterministic execution`() {
        assertEquals(NexusIntent.DETERMINISTIC_EXECUTION, NexusIntentRouter.classify(deterministicPrompt))
    }

    @Test
    fun `routes replay request before claim verification`() {
        val replayPrompt = """
            MODE: DETERMINISTIC_EXECUTION
            INPUT: 7
            OPERATIONS: MULTIPLY 8; SUBTRACT 11; DIVIDE 5; ADD 9
            REPLAY_COUNT: 2
        """.trimIndent()

        assertEquals(NexusIntent.REPLAY_COMPARISON, NexusIntentRouter.classify(replayPrompt))
    }

    @Test
    fun `executes ordered arithmetic and returns eighteen`() {
        val result = CanonicalExecutionEngine.execute(deterministicPrompt)

        assertEquals("18", result.canonicalRecord.finalValue)
        assertEquals(4, result.canonicalRecord.steps.size)
        assertEquals(
            "3e408a514a78d1a28568de1e838a23e747231445276418c131603d52e013d996",
            result.canonicalRecord.sha256
        )
        assertTrue(result.pass)
    }

    @Test
    fun `replay records and hashes are byte identical`() {
        val prompt = """
            MODE: REPLAY_COMPARISON
            INPUT: 7
            OPERATIONS: MULTIPLY 8; SUBTRACT 11; DIVIDE 5; ADD 9
            REPLAY_COUNT: 2
        """.trimIndent()

        val result = CanonicalExecutionEngine.execute(prompt)

        assertEquals(2, result.records.size)
        assertEquals(result.records[0].canonicalJson, result.records[1].canonicalJson)
        assertEquals(result.records[0].sha256, result.records[1].sha256)
        assertTrue(result.pass)
    }

    @Test
    fun `operation order changes canonical hash`() {
        val first = CanonicalExecutionEngine.execute(deterministicPrompt).canonicalRecord
        val second = CanonicalExecutionEngine.execute(
            "Start with 7, add 9, multiply by 8, subtract 11, divide by 5."
        ).canonicalRecord

        assertNotEquals(first.sha256, second.sha256)
    }

    @Test
    fun `rejects division by zero`() {
        val failure = runCatching {
            CanonicalExecutionEngine.execute("Start with 7 and divide by 0.")
        }

        assertTrue(failure.isFailure)
        assertEquals("Division by zero is not permitted.", failure.exceptionOrNull()?.message)
    }

    @Test
    fun `ordinary factual claim remains in verification route`() {
        val intent = NexusIntentRouter.classify("This supplier holds an active ISO certification.")

        assertEquals(NexusIntent.CLAIM_VERIFICATION, intent)
        assertFalse(intent == NexusIntent.DETERMINISTIC_EXECUTION)
    }
}

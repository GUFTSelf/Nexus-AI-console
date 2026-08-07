package com.example

import com.example.service.CanonicalExecutionEngine
import com.example.service.MathOp
import com.example.service.MathOpType
import org.junit.Assert.*
import org.junit.Test

class CanonicalExecutionEngineTest {

    @Test
    fun testFixedValidationVector() {
        val ops = listOf(
            MathOp(MathOpType.MULTIPLY, 8.0),
            MathOp(MathOpType.SUBTRACT, 11.0),
            MathOp(MathOpType.DIVIDE, 5.0),
            MathOp(MathOpType.ADD, 9.0)
        )
        val result = CanonicalExecutionEngine.execute(7.0, ops)

        assertEquals(18.0, result.finalValue, 0.00001)
        assertEquals("3e408a514a78d1a28568de1e838a23e747231445276418c131603d52e013d996", result.sha256Hash)
        assertEquals(4, result.transitions.size)
        assertEquals(56.0, result.transitions[0].nextState, 0.00001)
        assertEquals(45.0, result.transitions[1].nextState, 0.00001)
        assertEquals(9.0, result.transitions[2].nextState, 0.00001)
        assertEquals(18.0, result.transitions[3].nextState, 0.00001)
    }

    @Test
    fun testReplayComparisonPass() {
        val ops = listOf(
            MathOp(MathOpType.ADD, 10.0),
            MathOp(MathOpType.MULTIPLY, 2.0)
        )
        val replayResult = CanonicalExecutionEngine.replay(5.0, ops, replayCount = 5)

        assertTrue(replayResult.pass)
        assertEquals(5, replayResult.replayCount)
        assertEquals(30.0, replayResult.run1Final, 0.00001)
        assertEquals(30.0, replayResult.run2Final, 0.00001)
        assertEquals(replayResult.run1Hash, replayResult.run2Hash)
    }

    @Test(expected = ArithmeticException::class)
    fun testDivisionByZeroRejected() {
        val ops = listOf(MathOp(MathOpType.DIVIDE, 0.0))
        CanonicalExecutionEngine.execute(10.0, ops)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testNonIntegralResultRejectedInIntegerOnlyMode() {
        val ops = listOf(MathOp(MathOpType.DIVIDE, 4.0))
        CanonicalExecutionEngine.execute(10.0, ops, integerOnly = true)
    }

    @Test
    fun testOperationOrderPreserved() {
        val opsOrder1 = listOf(MathOp(MathOpType.ADD, 5.0), MathOp(MathOpType.MULTIPLY, 2.0))
        val opsOrder2 = listOf(MathOp(MathOpType.MULTIPLY, 2.0), MathOp(MathOpType.ADD, 5.0))

        val result1 = CanonicalExecutionEngine.execute(10.0, opsOrder1)
        val result2 = CanonicalExecutionEngine.execute(10.0, opsOrder2)

        assertEquals(30.0, result1.finalValue, 0.00001)
        assertEquals(25.0, result2.finalValue, 0.00001)
        assertNotEquals(result1.canonicalJson, result2.canonicalJson)
        assertNotEquals(result1.sha256Hash, result2.sha256Hash)
    }

    @Test
    fun testReplayCountBoundedBetween1And10() {
        val ops = listOf(MathOp(MathOpType.ADD, 1.0))

        val replayLow = CanonicalExecutionEngine.replay(1.0, ops, replayCount = 0)
        assertEquals(1, replayLow.replayCount)

        val replayHigh = CanonicalExecutionEngine.replay(1.0, ops, replayCount = 20)
        assertEquals(10, replayHigh.replayCount)
    }

    @Test
    fun testCanonicalJsonExcludesTimestampsAndRandomIdentifiers() {
        val ops = listOf(MathOp(MathOpType.ADD, 3.0))
        val result = CanonicalExecutionEngine.execute(5.0, ops)

        assertFalse(result.canonicalJson.contains("timestamp"))
        assertFalse(result.canonicalJson.contains("trace_id"))
        assertFalse(result.canonicalJson.contains("case_id"))
        assertEquals("""{"input":5,"operations":[{"op":"ADD","value":3}],"result":8}""", result.canonicalJson)
    }
}

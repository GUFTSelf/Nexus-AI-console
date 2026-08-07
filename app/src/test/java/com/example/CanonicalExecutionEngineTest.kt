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
}

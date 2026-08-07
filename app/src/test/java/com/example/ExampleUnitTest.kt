package com.example

import com.example.service.CanonicalExecutionEngine
import com.example.service.MathOp
import com.example.service.MathOpType
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testCanonicalSha256Target() {
    val ops = listOf(
      MathOp(MathOpType.MULTIPLY, 8.0),
      MathOp(MathOpType.SUBTRACT, 11.0),
      MathOp(MathOpType.DIVIDE, 5.0),
      MathOp(MathOpType.ADD, 9.0)
    )
    val result = CanonicalExecutionEngine.execute(7.0, ops)
    assertEquals("3e408a514a78d1a28568de1e838a23e747231445276418c131603d52e013d996", result.sha256Hash)
  }
}

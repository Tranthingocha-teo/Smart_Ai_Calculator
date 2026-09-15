package dhn.intern.smart_ai_caculator_app.ocr

import dhn.intern.smart_ai_caculator_app.domain.ocr.SpatialMathParser
import dhn.intern.smart_ai_caculator_app.domain.ocr.model.BoundingBox
import dhn.intern.smart_ai_caculator_app.domain.ocr.model.RecognizedSymbol
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SpatialMathParserTest {

    private lateinit var parser: SpatialMathParser

    @Before
    fun setUp() {
        parser = SpatialMathParser()
    }

    @Test
    fun testSimpleArithmeticLinearExpression() {
        val symbols = listOf(
            RecognizedSymbol("2", 0.99f, BoundingBox(10, 50, 30, 90)),
            RecognizedSymbol("+", 0.95f, BoundingBox(40, 60, 60, 80)),
            RecognizedSymbol("3", 0.98f, BoundingBox(70, 50, 90, 90))
        )
        val result = parser.parse(symbols)
        assertEquals("2+3", result)
    }

    @Test
    fun testExponentDetection() {
        // 'x' base at y=50..90 (height 40, center 70)
        // '2' elevated at y=30..60 (bottom 60 <= center 70)
        val symbols = listOf(
            RecognizedSymbol("x", 0.97f, BoundingBox(10, 50, 30, 90)),
            RecognizedSymbol("2", 0.96f, BoundingBox(35, 30, 50, 60))
        )
        val result = parser.parse(symbols)
        assertEquals("x^2", result)
    }

    @Test
    fun testImplicitMultiplication() {
        // 2 followed by x: 2x -> 2*x
        val symbols1 = listOf(
            RecognizedSymbol("2", 0.99f, BoundingBox(10, 50, 30, 90)),
            RecognizedSymbol("x", 0.98f, BoundingBox(40, 50, 60, 90))
        )
        assertEquals("2*x", parser.parse(symbols1))

        // 3 followed by (: 3(x+1) -> 3*(x+1)
        val symbols2 = listOf(
            RecognizedSymbol("3", 0.99f, BoundingBox(10, 50, 30, 90)),
            RecognizedSymbol("(", 0.95f, BoundingBox(40, 45, 50, 95)),
            RecognizedSymbol("x", 0.98f, BoundingBox(60, 50, 80, 90)),
            RecognizedSymbol("+", 0.95f, BoundingBox(90, 60, 110, 80)),
            RecognizedSymbol("1", 0.99f, BoundingBox(120, 50, 135, 90)),
            RecognizedSymbol(")", 0.95f, BoundingBox(145, 45, 155, 95))
        )
        assertEquals("3*(x+1)", parser.parse(symbols2))
    }

    @Test
    fun testDecimalPointNormalization() {
        // 3 followed by comma followed by 14 -> 3.14
        val symbols = listOf(
            RecognizedSymbol("3", 0.99f, BoundingBox(10, 50, 30, 90)),
            RecognizedSymbol(",", 0.90f, BoundingBox(35, 80, 42, 95)),
            RecognizedSymbol("1", 0.99f, BoundingBox(45, 50, 55, 90)),
            RecognizedSymbol("4", 0.99f, BoundingBox(60, 50, 80, 90))
        )
        assertEquals("3.14", parser.parse(symbols))
    }

    @Test
    fun testOperatorSanitization() {
        assertEquals("2+3", parser.sanitizeExpression("2++3"))
        assertEquals("5*4", parser.sanitizeExpression("5**4"))
        assertEquals("x^2", parser.sanitizeExpression("x^^2"))
    }

    @Test
    fun testNeedsImplicitMultiplicationHelper() {
        assertTrue(parser.needsImplicitMultiplication("2", "x"))
        assertTrue(parser.needsImplicitMultiplication("x", "y"))
        assertTrue(parser.needsImplicitMultiplication("5", "("))
        assertTrue(parser.needsImplicitMultiplication(")", "("))
        assertTrue(parser.needsImplicitMultiplication(")", "2"))
    }
}

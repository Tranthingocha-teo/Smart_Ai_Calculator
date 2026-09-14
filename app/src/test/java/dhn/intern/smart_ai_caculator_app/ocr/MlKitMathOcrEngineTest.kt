package dhn.intern.smart_ai_caculator_app.ocr

import dhn.intern.smart_ai_caculator_app.domain.ocr.MlKitMathOcrEngine
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MlKitMathOcrEngineTest {

    private lateinit var engine: MlKitMathOcrEngine

    @Before
    fun setUp() {
        // We can test cleanMathSyntax without initializing the Android ML Kit native client
        engine = MlKitMathOcrEngine()
    }

    @Test
    fun testCleanMathMultiplicationBetweenDigits() {
        assertEquals("5 * 6", engine.cleanMathSyntax("5 x 6"))
        assertEquals("12 * 4", engine.cleanMathSyntax("12 × 4"))
        assertEquals("25 * 10", engine.cleanMathSyntax("25X10"))
    }

    @Test
    fun testPreserveAlgebraicVariables() {
        assertEquals("2x + 5 = 15", engine.cleanMathSyntax("2x + 5 = 15"))
        assertEquals("3x - 9 = 0", engine.cleanMathSyntax("3X − 9 = 0"))
        assertEquals("5x = 20", engine.cleanMathSyntax("5x = 20"))
    }

    @Test
    fun testNormalizeUnicodeSymbols() {
        assertEquals("10 / 2 - 3", engine.cleanMathSyntax("10 ÷ 2 − 3"))
        assertEquals("15 / 3", engine.cleanMathSyntax("15 : 3"))
    }

    @Test
    fun testFilterJunkCharacters() {
        assertEquals("2x + 10 = 20", engine.cleanMathSyntax("2x + 10 = 20#$@"))
    }
}

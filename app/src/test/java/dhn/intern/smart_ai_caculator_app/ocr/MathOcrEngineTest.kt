package dhn.intern.smart_ai_caculator_app.ocr

import dhn.intern.smart_ai_caculator_app.domain.ocr.ISymbolClassifier
import dhn.intern.smart_ai_caculator_app.domain.ocr.ImagePreprocessor
import dhn.intern.smart_ai_caculator_app.domain.ocr.MathOcrEngine
import dhn.intern.smart_ai_caculator_app.domain.ocr.MathSymbolClassifier
import dhn.intern.smart_ai_caculator_app.domain.ocr.SpatialMathParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MathOcrEngineTest {

    private class FakeSymbolClassifier(
        override val labels: List<String> = listOf(
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "+", "-", "*", "/", "=", ".", "(", ")", "x", "y", "^", ","
        )
    ) : ISymbolClassifier {
        var nextLabel: String = "1"
        var callCount: Int = 0

        override fun classify(normalizedPixels: FloatArray): MathSymbolClassifier.Classification {
            callCount++
            return MathSymbolClassifier.Classification(
                label = nextLabel,
                confidence = 0.95f,
                allProbabilities = FloatArray(labels.size) { if (it == 1) 0.95f else 0.002f }
            )
        }

        override fun close() {}
    }

    @Test
    fun testEndToEndRecognitionOnSyntheticGrid() {
        val fakeClassifier = FakeSymbolClassifier()
        val engine = MathOcrEngine(
            preprocessor = ImagePreprocessor(minSymbolArea = 4, minSymbolDimension = 2),
            classifier = fakeClassifier,
            parser = SpatialMathParser()
        )

        val width = 80
        val height = 60
        val pixels = IntArray(width * height) { 0xFFFFFFFF.toInt() }

        // Draw a stroke for symbol 1: x=10..15, y=10..50
        for (y in 10..50) {
            for (x in 10..15) {
                pixels[y * width + x] = 0xFF000000.toInt()
            }
        }

        // Draw a stroke for symbol 2: x=30..45, y=25..35
        for (y in 25..35) {
            for (x in 30..45) {
                pixels[y * width + x] = 0xFF000000.toInt()
            }
        }

        val result = engine.recognize(pixels, width, height)

        assertNotNull(result)
        assertEquals(2, result.symbols.size)
        assertTrue("Expected average confidence > 0", result.averageConfidence > 0.90f)
        assertNotNull(result.formattedExpression)
        assertEquals(2, fakeClassifier.callCount)
    }

    @Test
    fun testBlankImageReturnsEmptyResult() {
        val fakeClassifier = FakeSymbolClassifier()
        val engine = MathOcrEngine(classifier = fakeClassifier)

        val width = 50
        val height = 50
        // All white pixels (no ink)
        val pixels = IntArray(width * height) { 0xFFFFFFFF.toInt() }

        val result = engine.recognize(pixels, width, height)

        assertTrue(result.symbols.isEmpty())
        assertTrue(result.formattedExpression.isEmpty())
        assertFalse(result.hasVariables)
        assertEquals(0, fakeClassifier.callCount)
    }
}

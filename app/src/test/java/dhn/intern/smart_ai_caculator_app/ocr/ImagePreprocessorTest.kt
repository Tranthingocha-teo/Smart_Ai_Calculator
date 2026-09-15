package dhn.intern.smart_ai_caculator_app.ocr

import dhn.intern.smart_ai_caculator_app.domain.ocr.ImagePreprocessor
import dhn.intern.smart_ai_caculator_app.domain.ocr.model.BoundingBox
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ImagePreprocessorTest {

    private lateinit var preprocessor: ImagePreprocessor

    @Before
    fun setUp() {
        preprocessor = ImagePreprocessor(minSymbolArea = 4, minSymbolDimension = 2)
    }

    @Test
    fun testComputeOtsuThresholdBimodal() {
        val histogram = IntArray(256)
        // Background peak around 220 (light paper)
        for (i in 200..240) histogram[i] = 100
        // Foreground peak around 40 (dark ink)
        for (i in 30..50) histogram[i] = 100

        val totalPixels = histogram.sum()
        val threshold = preprocessor.computeOtsuThreshold(histogram, totalPixels)

        // Threshold should fall between the two peaks (e.g. between 50 and 200)
        assertTrue("Threshold $threshold should be between 50 and 200", threshold in 50..200)
    }

    @Test
    fun testBinarizeAndSegmentConnectedComponents() {
        val width = 50
        val height = 50
        val pixels = IntArray(width * height) { 0xFFFFFFFF.toInt() } // White background

        // Draw a dark block representing symbol '1' at x=10..15, y=10..40
        for (y in 10..40) {
            for (x in 10..15) {
                pixels[y * width + x] = 0xFF000000.toInt() // Black stroke
            }
        }

        // Draw a second dark block representing '+' at x=30..40, y=20..30
        for (y in 20..30) {
            for (x in 30..40) {
                pixels[y * width + x] = 0xFF000000.toInt() // Black stroke
            }
        }

        val binarized = preprocessor.binarize(pixels, width, height)
        val boxes = preprocessor.segmentSymbols(binarized)

        assertEquals(2, boxes.size)
        // First box should be around x=10..15
        assertTrue(boxes[0].left in 8..12)
        // Second box should be around x=30..40
        assertTrue(boxes[1].left in 28..32)
    }

    @Test
    fun testMergeEqualSignBars() {
        val width = 60
        val height = 60
        val pixels = IntArray(width * height) { 0xFFFFFFFF.toInt() }

        // Draw top horizontal bar of '=': x=10..40, y=20..22
        for (y in 20..22) {
            for (x in 10..40) {
                pixels[y * width + x] = 0xFF000000.toInt()
            }
        }

        // Draw bottom horizontal bar of '=': x=10..40, y=28..30
        for (y in 28..30) {
            for (x in 10..40) {
                pixels[y * width + x] = 0xFF000000.toInt()
            }
        }

        val binarized = preprocessor.binarize(pixels, width, height)
        val boxes = preprocessor.segmentSymbols(binarized)

        // The two horizontal bars should be merged into a single '=' bounding box
        assertEquals(1, boxes.size)
        val merged = boxes[0]
        assertEquals(10, merged.left)
        assertEquals(20, merged.top)
        assertEquals(41, merged.right)
        assertEquals(31, merged.bottom)
    }

    @Test
    fun testExtractAndNormalizeSymbolSizeAndRange() {
        val width = 40
        val height = 40
        val binary = BooleanArray(width * height)

        // Fill a 10x10 square in center
        for (y in 15..25) {
            for (x in 15..25) {
                binary[y * width + x] = true
            }
        }

        val img = ImagePreprocessor.BinarizedImage(binary, width, height)
        val box = BoundingBox(15, 15, 25, 25)
        val normalized = preprocessor.extractAndNormalizeSymbol(img, box, targetSize = 28)

        assertEquals(784, normalized.size)
        var hasActive = false
        for (pixel in normalized) {
            assertTrue(pixel in 0.0f..1.0f)
            if (pixel > 0f) hasActive = true
        }
        assertTrue("Normalized image must contain active pixels", hasActive)
    }
}

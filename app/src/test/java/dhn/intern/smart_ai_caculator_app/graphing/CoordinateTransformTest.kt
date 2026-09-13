package dhn.intern.smart_ai_caculator_app.graphing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import dhn.intern.smart_ai_caculator_app.domain.graphing.CoordinateTransform
import dhn.intern.smart_ai_caculator_app.domain.graphing.ViewportBounds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class CoordinateTransformTest {

    private val canvasSize = Size(1000f, 1000f)
    private val viewport = ViewportBounds(minX = -10.0, maxX = 10.0, minY = -10.0, maxY = 10.0)

    @Test
    fun `mathToScreen maps origin to canvas center`() {
        val center = CoordinateTransform.mathToScreen(0.0, 0.0, viewport, canvasSize)
        assertEquals(500f, center.x, 0.01f)
        assertEquals(500f, center.y, 0.01f)
    }

    @Test
    fun `mathToScreen and screenToMath are exact inverse operations`() {
        val originalMathX = 3.5
        val originalMathY = -7.2

        val screenOffset = CoordinateTransform.mathToScreen(originalMathX, originalMathY, viewport, canvasSize)
        val reconstructedMath = CoordinateTransform.screenToMath(screenOffset, viewport, canvasSize)

        assertEquals(originalMathX, reconstructedMath.x, 1e-4)
        assertEquals(originalMathY, reconstructedMath.y, 1e-4)
    }

    @Test
    fun `pan shifts viewport proportionally to screen drag`() {
        // Drag 100px right (10% of width) -> math should shift left by 2 units (-10% of 20 units)
        val delta = Offset(100f, 0f)
        val panned = CoordinateTransform.pan(viewport, delta, canvasSize)

        assertEquals(-12.0, panned.minX, 1e-4)
        assertEquals(8.0, panned.maxX, 1e-4)
        assertEquals(-10.0, panned.minY, 1e-4)
        assertEquals(10.0, panned.maxY, 1e-4)
    }

    @Test
    fun `zoom centered on origin scales viewport symmetrically`() {
        // Zoom in by factor 2 at center
        val centerScreen = Offset(500f, 500f)
        val zoomed = CoordinateTransform.zoom(viewport, 2.0f, centerScreen, canvasSize)

        // Width halved from 20 to 10
        assertEquals(10.0, zoomed.width, 1e-4)
        assertEquals(-5.0, zoomed.minX, 1e-4)
        assertEquals(5.0, zoomed.maxX, 1e-4)
        assertEquals(-5.0, zoomed.minY, 1e-4)
        assertEquals(5.0, zoomed.maxY, 1e-4)
    }

    @Test
    fun `computeGridStep returns clean intervals 1 2 5 for various ranges`() {
        assertEquals(2.0, CoordinateTransform.computeGridStep(20.0, targetDivisions = 10), 1e-4)
        assertEquals(1.0, CoordinateTransform.computeGridStep(10.0, targetDivisions = 10), 1e-4)
        assertEquals(0.5, CoordinateTransform.computeGridStep(5.0, targetDivisions = 10), 1e-4)
        assertEquals(5.0, CoordinateTransform.computeGridStep(50.0, targetDivisions = 10), 1e-4)
        assertEquals(10.0, CoordinateTransform.computeGridStep(100.0, targetDivisions = 10), 1e-4)
    }
}

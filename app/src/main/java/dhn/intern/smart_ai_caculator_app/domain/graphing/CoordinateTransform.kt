package dhn.intern.smart_ai_caculator_app.domain.graphing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

object CoordinateTransform {

    fun mathToScreen(
        x: Double,
        y: Double,
        viewport: ViewportBounds,
        canvasSize: Size
    ): Offset {
        if (canvasSize.width <= 0f || canvasSize.height <= 0f) return Offset.Zero
        val px = ((x - viewport.minX) / viewport.width) * canvasSize.width
        val py = ((viewport.maxY - y) / viewport.height) * canvasSize.height
        return Offset(px.toFloat(), py.toFloat())
    }

    fun screenToMath(
        offset: Offset,
        viewport: ViewportBounds,
        canvasSize: Size
    ): GraphPoint {
        if (canvasSize.width <= 0f || canvasSize.height <= 0f) return GraphPoint(0.0, 0.0)
        val x = viewport.minX + (offset.x / canvasSize.width) * viewport.width
        val y = viewport.maxY - (offset.y / canvasSize.height) * viewport.height
        return GraphPoint(x, y)
    }

    fun pan(
        viewport: ViewportBounds,
        deltaScreen: Offset,
        canvasSize: Size
    ): ViewportBounds {
        if (canvasSize.width <= 0f || canvasSize.height <= 0f) return viewport
        val dxMath = -(deltaScreen.x / canvasSize.width) * viewport.width
        val dyMath = (deltaScreen.y / canvasSize.height) * viewport.height
        return ViewportBounds(
            minX = viewport.minX + dxMath,
            maxX = viewport.maxX + dxMath,
            minY = viewport.minY + dyMath,
            maxY = viewport.maxY + dyMath
        )
    }

    fun zoom(
        viewport: ViewportBounds,
        zoomFactor: Float,
        centerScreen: Offset,
        canvasSize: Size
    ): ViewportBounds {
        if (zoomFactor <= 0.0f || canvasSize.width <= 0f || canvasSize.height <= 0f) return viewport
        val focusMath = screenToMath(centerScreen, viewport, canvasSize)
        val factor = (1.0 / zoomFactor).coerceIn(0.05, 20.0)

        val newWidth = (viewport.width * factor).coerceIn(1e-4, 1e8)
        val newHeight = (viewport.height * factor).coerceIn(1e-4, 1e8)

        val ratioX = (focusMath.x - viewport.minX) / viewport.width
        val ratioY = (focusMath.y - viewport.minY) / viewport.height

        val newMinX = focusMath.x - ratioX * newWidth
        val newMaxX = newMinX + newWidth
        val newMinY = focusMath.y - ratioY * newHeight
        val newMaxY = newMinY + newHeight

        return ViewportBounds(newMinX, newMaxX, newMinY, newMaxY)
    }

    /**
     * Computes the standard major grid spacing step (e.g. 0.1, 0.2, 0.5, 1, 2, 5, 10...).
     */
    fun computeGridStep(range: Double, targetDivisions: Int = 10): Double {
        if (range <= 0.0) return 1.0
        val rawStep = range / targetDivisions
        val exponent = floor(log10(rawStep))
        val powerOf10 = 10.0.pow(exponent)
        val fraction = rawStep / powerOf10

        val niceFraction = when {
            fraction < 1.5 -> 1.0
            fraction < 3.0 -> 2.0
            fraction < 7.0 -> 5.0
            else -> 10.0
        }

        return niceFraction * powerOf10
    }
}

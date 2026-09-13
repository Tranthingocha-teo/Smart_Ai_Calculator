package dhn.intern.smart_ai_caculator_app.ui.components.graphing

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import dhn.intern.smart_ai_caculator_app.domain.graphing.CoordinateTransform
import dhn.intern.smart_ai_caculator_app.domain.graphing.GraphPoint
import dhn.intern.smart_ai_caculator_app.domain.graphing.SampledCurve
import dhn.intern.smart_ai_caculator_app.domain.graphing.SpecialPoint
import dhn.intern.smart_ai_caculator_app.domain.graphing.SpecialPointType
import dhn.intern.smart_ai_caculator_app.domain.graphing.ViewportBounds
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

@Composable
fun GraphCanvas(
    viewport: ViewportBounds,
    sampledCurves: List<SampledCurve>,
    curveColors: List<Color>,
    intersections: List<SpecialPoint>,
    tracePoint: GraphPoint?,
    traceSpecialPoint: SpecialPoint?,
    onPan: (Offset, Size) -> Unit,
    onZoom: (Float, Offset, Size) -> Unit,
    onTrace: (Offset?, Size) -> Unit,
    modifier: Modifier = Modifier
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val gridColor = onSurfaceColor.copy(alpha = 0.15f)
    val axisColor = onSurfaceColor.copy(alpha = 0.7f)
    val tooltipBgColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.92f)
    val tooltipTextColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val primaryColor = MaterialTheme.colorScheme.primary

    val textPaint = remember(onSurfaceColor) {
        Paint().apply {
            color = onSurfaceColor.copy(alpha = 0.65f).toArgb()
            textSize = 28f
            isAntiAlias = true
        }
    }

    val tooltipPaint = remember(tooltipTextColor) {
        Paint().apply {
            color = tooltipTextColor
            textSize = 32f
            isFakeBoldText = true
            isAntiAlias = true
        }
    }

    var isTouching by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceColor)
            .pointerInput(viewport) {
                detectTransformGestures { centroid, pan, zoom, _ ->
                    if (zoom != 1.0f) {
                        onZoom(zoom, centroid, size.toSize())
                    }
                    if (pan != Offset.Zero) {
                        onPan(pan, size.toSize())
                    }
                }
            }
            .pointerInput(viewport) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isTouching = true
                        onTrace(offset, size.toSize())
                    },
                    onDragEnd = {
                        isTouching = false
                        onTrace(null, size.toSize())
                    },
                    onDragCancel = {
                        isTouching = false
                        onTrace(null, size.toSize())
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        onTrace(change.position, size.toSize())
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = size
            if (canvasSize.width <= 0f || canvasSize.height <= 0f) return@Canvas

            // 1. Draw Grid Lines & Labels
            val step = CoordinateTransform.computeGridStep(viewport.width, targetDivisions = 8)

            // Vertical grid lines (constant X)
            val firstX = ceil(viewport.minX / step) * step
            var currGridX = firstX
            while (currGridX <= viewport.maxX) {
                val screenX = CoordinateTransform.mathToScreen(currGridX, 0.0, viewport, canvasSize).x
                drawLine(
                    color = gridColor,
                    start = Offset(screenX, 0f),
                    end = Offset(screenX, canvasSize.height),
                    strokeWidth = 1.dp.toPx()
                )

                // Coordinate label
                val label = formatGridLabel(currGridX)
                val labelY = if (0.0 in viewport.minY..viewport.maxY) {
                    val axisY = CoordinateTransform.mathToScreen(0.0, 0.0, viewport, canvasSize).y
                    (axisY + 36f).coerceIn(40f, canvasSize.height - 20f)
                } else {
                    canvasSize.height - 20f
                }
                drawContext.canvas.nativeCanvas.drawText(label, screenX + 6f, labelY, textPaint)

                currGridX += step
            }

            // Horizontal grid lines (constant Y)
            val firstY = ceil(viewport.minY / step) * step
            var currGridY = firstY
            while (currGridY <= viewport.maxY) {
                val screenY = CoordinateTransform.mathToScreen(0.0, currGridY, viewport, canvasSize).y
                drawLine(
                    color = gridColor,
                    start = Offset(0f, screenY),
                    end = Offset(canvasSize.width, screenY),
                    strokeWidth = 1.dp.toPx()
                )

                // Coordinate label
                if (currGridY != 0.0) {
                    val label = formatGridLabel(currGridY)
                    val labelX = if (0.0 in viewport.minX..viewport.maxX) {
                        val axisX = CoordinateTransform.mathToScreen(0.0, 0.0, viewport, canvasSize).x
                        (axisX + 10f).coerceIn(10f, canvasSize.width - 90f)
                    } else {
                        10f
                    }
                    drawContext.canvas.nativeCanvas.drawText(label, labelX, screenY - 6f, textPaint)
                }

                currGridY += step
            }

            // 2. Draw Primary Axes Ox & Oy
            if (0.0 in viewport.minX..viewport.maxX) {
                val axisX = CoordinateTransform.mathToScreen(0.0, 0.0, viewport, canvasSize).x
                drawLine(
                    color = axisColor,
                    start = Offset(axisX, 0f),
                    end = Offset(axisX, canvasSize.height),
                    strokeWidth = 2.dp.toPx()
                )
            }
            if (0.0 in viewport.minY..viewport.maxY) {
                val axisY = CoordinateTransform.mathToScreen(0.0, 0.0, viewport, canvasSize).y
                drawLine(
                    color = axisColor,
                    start = Offset(0f, axisY),
                    end = Offset(canvasSize.width, axisY),
                    strokeWidth = 2.dp.toPx()
                )
            }

            // 3. Draw Continuous Function Curves
            sampledCurves.forEachIndexed { index, curve ->
                val curveColor = curveColors.getOrElse(index) { Color.Cyan }

                for (segment in curve.continuousSegments) {
                    if (segment.size < 2) continue
                    val path = Path()
                    val p0 = CoordinateTransform.mathToScreen(segment[0].x, segment[0].y, viewport, canvasSize)
                    path.moveTo(p0.x, p0.y)

                    for (ptIdx in 1 until segment.size) {
                        val pt = CoordinateTransform.mathToScreen(segment[ptIdx].x, segment[ptIdx].y, viewport, canvasSize)
                        path.lineTo(pt.x, pt.y)
                    }

                    drawPath(
                        path = path,
                        color = curveColor,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                // 4. Draw Special Points (Roots & Extrema)
                for (special in curve.specialPoints) {
                    val spScreen = CoordinateTransform.mathToScreen(special.point.x, special.point.y, viewport, canvasSize)
                    val dotColor = when (special.type) {
                        SpecialPointType.ROOT -> Color(0xFF4CAF50) // Green
                        SpecialPointType.LOCAL_MIN -> Color(0xFF2196F3) // Blue
                        SpecialPointType.LOCAL_MAX -> Color(0xFF9C27B0) // Purple
                        SpecialPointType.Y_INTERCEPT -> Color(0xFF00BCD4) // Cyan
                        SpecialPointType.INTERSECTION -> Color(0xFFFF9800) // Amber
                    }

                    drawCircle(
                        color = Color.White,
                        radius = 6.dp.toPx(),
                        center = spScreen
                    )
                    drawCircle(
                        color = dotColor,
                        radius = 4.5f.dp.toPx(),
                        center = spScreen
                    )
                }
            }

            // Draw Intersections between curves
            for (inter in intersections) {
                val interScreen = CoordinateTransform.mathToScreen(inter.point.x, inter.point.y, viewport, canvasSize)
                drawCircle(
                    color = Color.White,
                    radius = 7.dp.toPx(),
                    center = interScreen
                )
                drawCircle(
                    color = Color(0xFFFF9800), // Amber
                    radius = 5.dp.toPx(),
                    center = interScreen
                )
            }

            // 5. Draw Tracing Point & Coordinate Tooltip
            if (tracePoint != null) {
                val traceScreen = CoordinateTransform.mathToScreen(tracePoint.x, tracePoint.y, viewport, canvasSize)

                // Vertical guideline
                drawLine(
                    color = onSurfaceColor.copy(alpha = 0.35f),
                    start = Offset(traceScreen.x, 0f),
                    end = Offset(traceScreen.x, canvasSize.height),
                    strokeWidth = 1.dp.toPx()
                )

                // Pulsing dot
                drawCircle(
                    color = Color.White,
                    radius = 8.dp.toPx(),
                    center = traceScreen
                )
                drawCircle(
                    color = primaryColor,
                    radius = 5.5f.dp.toPx(),
                    center = traceScreen
                )

                // Tooltip Bubble
                val tooltipText = traceSpecialPoint?.label
                    ?: "(${formatCoord(tracePoint.x)}, ${formatCoord(tracePoint.y)})"
                val textWidth = tooltipPaint.measureText(tooltipText)
                val tooltipWidth = textWidth + 36f
                val tooltipHeight = 56f

                val bubbleX = (traceScreen.x - tooltipWidth / 2f).coerceIn(16f, canvasSize.width - tooltipWidth - 16f)
                val bubbleY = if (traceScreen.y - tooltipHeight - 20f < 20f) {
                    traceScreen.y + 24f
                } else {
                    traceScreen.y - tooltipHeight - 20f
                }

                drawRoundRect(
                    color = tooltipBgColor,
                    topLeft = Offset(bubbleX, bubbleY),
                    size = Size(tooltipWidth, tooltipHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
                )

                drawContext.canvas.nativeCanvas.drawText(
                    tooltipText,
                    bubbleX + 18f,
                    bubbleY + 38f,
                    tooltipPaint
                )
            }
        }
    }
}

private fun formatGridLabel(v: Double): String {
    return if (v.roundToInt().toDouble() == v) {
        v.roundToInt().toString()
    } else {
        "%.2f".format(v).trimEnd('0').trimEnd('.')
    }
}

private fun formatCoord(v: Double): String {
    return "%.3f".format(v).trimEnd('0').trimEnd('.')
}

private fun androidx.compose.ui.unit.IntSize.toSize(): Size = Size(width.toFloat(), height.toFloat())

package dhn.intern.smart_ai_caculator_app.ui.components.graphing

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dhn.intern.smart_ai_caculator_app.domain.graphing.CoordinateTransform
import dhn.intern.smart_ai_caculator_app.domain.graphing.GraphPoint
import dhn.intern.smart_ai_caculator_app.domain.graphing.SpecialPoint
import dhn.intern.smart_ai_caculator_app.domain.graphing.SpecialPointType
import dhn.intern.smart_ai_caculator_app.domain.graphing.ViewportBounds
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.RenderedCurve
import kotlin.math.ceil
import kotlin.math.roundToInt

@Composable
fun GraphCanvas(
    viewport: ViewportBounds,
    renderedCurves: List<RenderedCurve>,
    intersections: List<SpecialPoint>,
    tracePoint: GraphPoint?,
    traceSpecialPoint: SpecialPoint?,
    isTraceMode: Boolean,
    onPan: (Offset, Size) -> Unit,
    onZoom: (Float, Offset, Size) -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onResetZoom: () -> Unit,
    onTrace: (Offset?, Size) -> Unit,
    onToggleTraceMode: () -> Unit,
    onClearTrace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val gridColor = onSurfaceColor.copy(alpha = 0.12f)
    val axisColor = onSurfaceColor.copy(alpha = 0.65f)
    val tooltipBgColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.95f)
    val tooltipTextColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val primaryColor = MaterialTheme.colorScheme.primary

    val textPaint = remember(onSurfaceColor) {
        Paint().apply {
            color = onSurfaceColor.copy(alpha = 0.60f).toArgb()
            textSize = 26f
            isAntiAlias = true
        }
    }

    val tooltipPaint = remember(tooltipTextColor) {
        Paint().apply {
            color = tooltipTextColor
            textSize = 30f
            isFakeBoldText = true
            isAntiAlias = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .background(surfaceColor)
            // Gesture Layer 1: Pan & Zoom or Drag-Trace depending on mode
            .pointerInput(isTraceMode) {
                if (isTraceMode) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            onTrace(offset, size.toSize())
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            onTrace(change.position, size.toSize())
                        }
                    )
                } else {
                    detectTransformGestures(panZoomLock = false) { centroid, pan, zoom, _ ->
                        if (zoom != 1.0f) {
                            onZoom(zoom, centroid, size.toSize())
                        }
                        if (pan != Offset.Zero) {
                            onPan(pan, size.toSize())
                        }
                    }
                }
            }
            // Gesture Layer 2: Tap to inspect point
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        onTrace(offset, size.toSize())
                    }
                )
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
        ) {
            val canvasSize = size
            if (canvasSize.width <= 0f || canvasSize.height <= 0f) return@Canvas

            // Guarantee zero drawing bleed outside the canvas
            clipRect {
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

                    // Coordinate label on X axis (avoid overlapping origin at x=0)
                    if (currGridX != 0.0) {
                        val label = formatGridLabel(currGridX)
                        val labelY = if (0.0 in viewport.minY..viewport.maxY) {
                            val axisY = CoordinateTransform.mathToScreen(0.0, 0.0, viewport, canvasSize).y
                            (axisY + 32f).coerceIn(36f, canvasSize.height - 16f)
                        } else {
                            canvasSize.height - 16f
                        }
                        drawContext.canvas.nativeCanvas.drawText(label, screenX + 6f, labelY, textPaint)
                    }

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

                    // Coordinate label on Y axis
                    if (currGridY != 0.0) {
                        val label = formatGridLabel(currGridY)
                        val labelX = if (0.0 in viewport.minX..viewport.maxX) {
                            val axisX = CoordinateTransform.mathToScreen(0.0, 0.0, viewport, canvasSize).x
                            (axisX + 8f).coerceIn(8f, canvasSize.width - 80f)
                        } else {
                            8f
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

                // Draw origin "0" label once
                if (0.0 in viewport.minX..viewport.maxX && 0.0 in viewport.minY..viewport.maxY) {
                    val origin = CoordinateTransform.mathToScreen(0.0, 0.0, viewport, canvasSize)
                    drawContext.canvas.nativeCanvas.drawText("0", origin.x + 8f, origin.y + 28f, textPaint)
                }

                // 3. Draw Continuous Function Curves
                for (rendered in renderedCurves) {
                    val curve = rendered.curve
                    val curveColor = rendered.color

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

                    // Crosshair guidelines
                    drawLine(
                        color = onSurfaceColor.copy(alpha = 0.30f),
                        start = Offset(traceScreen.x, 0f),
                        end = Offset(traceScreen.x, canvasSize.height),
                        strokeWidth = 1.dp.toPx()
                    )
                    if (traceScreen.y in 0f..canvasSize.height) {
                        drawLine(
                            color = onSurfaceColor.copy(alpha = 0.30f),
                            start = Offset(0f, traceScreen.y),
                            end = Offset(canvasSize.width, traceScreen.y),
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
                    }

                    // Tooltip Bubble
                    val tooltipText = traceSpecialPoint?.label
                        ?: "(${formatCoord(tracePoint.x)}, ${formatCoord(tracePoint.y)})"
                    val textWidth = tooltipPaint.measureText(tooltipText)
                    val tooltipWidth = textWidth + 32f
                    val tooltipHeight = 52f

                    val bubbleX = (traceScreen.x - tooltipWidth / 2f).coerceIn(16f, canvasSize.width - tooltipWidth - 16f)
                    val rawY = if (traceScreen.y - tooltipHeight - 20f < 20f) {
                        traceScreen.y + 24f
                    } else {
                        traceScreen.y - tooltipHeight - 20f
                    }
                    val bubbleY = rawY.coerceIn(16f, canvasSize.height - tooltipHeight - 16f)

                    drawRoundRect(
                        color = tooltipBgColor,
                        topLeft = Offset(bubbleX, bubbleY),
                        size = Size(tooltipWidth, tooltipHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                    )

                    drawContext.canvas.nativeCanvas.drawText(
                        tooltipText,
                        bubbleX + 16f,
                        bubbleY + 36f,
                        tooltipPaint
                    )
                }
            }
        }

        // Floating UI Controls: Top-Left Mode Switch
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onToggleTraceMode() },
                color = if (isTraceMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isTraceMode) "📍 Dò điểm" else "✋ Di chuyển",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isTraceMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (tracePoint != null) {
                Surface(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onClearTrace() },
                    color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.9f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear trace",
                        modifier = Modifier
                            .padding(6.dp)
                            .size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Floating UI Controls: Top-Right Zoom Action Buttons
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Zoom In (+)
            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable { onZoomIn() },
                color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.92f),
                tonalElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Zoom in",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Zoom Out (-)
            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable { onZoomOut() },
                color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.92f),
                tonalElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "−",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Center / Reset Viewport (⌖)
            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable { onResetZoom() },
                color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.92f),
                tonalElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Viewport",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
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

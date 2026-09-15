package dhn.intern.smart_ai_caculator_app.ui.components.aiCalculator

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dhn.intern.smart_ai_caculator_app.R

/**
 * Modern Math Scan Reticle Overlay.
 * Renders a darkened camera scrim with a centered transparent scanning window,
 * high-contrast corner brackets, an animated scanning laser line, and helper text.
 */
@Composable
fun ScanReticleOverlay(
    modifier: Modifier = Modifier,
    isScanning: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "scanline")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserProgress"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Dimensions for reticle window: 84% width, 180dp height
            val boxWidth = canvasWidth * 0.84f
            val boxHeight = 180.dp.toPx()
            val left = (canvasWidth - boxWidth) / 2f
            val top = (canvasHeight - boxHeight) / 2f - 40.dp.toPx()
            val right = left + boxWidth
            val bottom = top + boxHeight
            val cornerRadius = 16.dp.toPx()

            // Draw darkened scrim with cutout
            val scrimColor = Color(0x88000000)
            with(drawContext.canvas.nativeCanvas) {
                val checkpoint = saveLayer(null, null)

                // Fill entire screen with scrim
                drawRect(
                    color = scrimColor,
                    size = size
                )

                // Cut out clear rectangle
                val cutoutPath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            rect = Rect(left, top, right, bottom),
                            cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                        )
                    )
                }
                drawPath(
                    path = cutoutPath,
                    color = Color.Transparent,
                    blendMode = BlendMode.Clear
                )

                restoreToCount(checkpoint)
            }

            // Draw subtle dashed border around frame
            val frameColor = Color.White.copy(alpha = 0.4f)
            drawRoundRect(
                color = frameColor,
                topLeft = Offset(left, top),
                size = Size(boxWidth, boxHeight),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius),
                style = Stroke(
                    width = 1.5.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 16f), 0f)
                )
            )

            // Draw modern corner brackets
            val bracketColor = Color(0xFF00E5FF) // Vibrant Cyan Accent
            val bracketLength = 26.dp.toPx()
            val bracketStroke = 4.dp.toPx()

            // Top-Left
            drawLine(bracketColor, Offset(left, top + bracketLength), Offset(left, top + 6.dp.toPx()), bracketStroke, StrokeCap.Round)
            drawLine(bracketColor, Offset(left + 6.dp.toPx(), top), Offset(left + bracketLength, top), bracketStroke, StrokeCap.Round)

            // Top-Right
            drawLine(bracketColor, Offset(right - bracketLength, top), Offset(right - 6.dp.toPx(), top), bracketStroke, StrokeCap.Round)
            drawLine(bracketColor, Offset(right, top + 6.dp.toPx()), Offset(right, top + bracketLength), bracketStroke, StrokeCap.Round)

            // Bottom-Left
            drawLine(bracketColor, Offset(left, bottom - bracketLength), Offset(left, bottom - 6.dp.toPx()), bracketStroke, StrokeCap.Round)
            drawLine(bracketColor, Offset(left + 6.dp.toPx(), bottom), Offset(left + bracketLength, bottom), bracketStroke, StrokeCap.Round)

            // Bottom-Right
            drawLine(bracketColor, Offset(right - bracketLength, bottom), Offset(right - 6.dp.toPx(), bottom), bracketStroke, StrokeCap.Round)
            drawLine(bracketColor, Offset(right, bottom - 6.dp.toPx()), Offset(right, bottom - bracketLength), bracketStroke, StrokeCap.Round)

            // Draw animated laser scanline if scanning or idle
            val laserY = top + (boxHeight * laserProgress)
            val laserBrush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFF00E5FF).copy(alpha = 0.8f),
                    Color(0xFF7C4DFF).copy(alpha = 0.8f),
                    Color.Transparent
                ),
                startX = left,
                endX = right
            )
            drawLine(
                brush = laserBrush,
                start = Offset(left + 8.dp.toPx(), laserY),
                end = Offset(right - 8.dp.toPx(), laserY),
                strokeWidth = 2.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Helper instruction text underneath reticle
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 220.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0x99000000),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = if (isScanning) {
                        stringResource(R.string.math_scan_processing)
                    } else {
                        stringResource(R.string.math_scan_hint)
                    },
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

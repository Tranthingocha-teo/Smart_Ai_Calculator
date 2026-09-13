package dhn.intern.smart_ai_caculator_app.ui.components.bmi

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dhn.intern.smart_ai_caculator_app.domain.bmi.bmiToAngle

@Composable
fun BmiGaugeBackground(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.padding(50.dp)) {
        val strokeWidth = size.width * 0.21f

        val sections = listOf(
            Color(0xFF006CF2),
            Color(0xFF029FF0),
            Color(0xFF3EB1FF),
            Color(0xFF87C501),
            Color(0xFFFEC400),
            Color(0xFFFE9901),
            Color(0xFFFE7A02),
            Color(0xFFF82519)
        )


        val sweepAngles = listOf(
            48.8f,  // <16
            8.28f,  // 16–16.9
            6.1f,   // 17–18.4
            27f,  // 18.5–24.9
            20.2f,  // 25–29.9
            19.9f,  // 30–34.9
            19.9f,  // 35–39.9
            30.3f   // ≥40
        )


        var startAngle = -180f

        sections.forEachIndexed { index, color ->
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngles[index],
                useCenter = false,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Butt
                ),
            )
            startAngle += sweepAngles[index]
        }
    }
}
@Composable
fun BmiNeedle(
    bmi: Float,
    needleRes: Int,
    modifier: Modifier = Modifier
) {
    val animatedAngle by animateFloatAsState(
        targetValue = bmiToAngle(bmi),
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "bmi-needle"
    )

    Image(
        painter = painterResource(needleRes),
        contentDescription = null,
        modifier = modifier.graphicsLayer {
            rotationZ = -animatedAngle + 59f
            transformOrigin = TransformOrigin(0.5f, 0.5f)
        }
    )
}




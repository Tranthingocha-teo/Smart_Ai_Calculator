package dhn.intern.smart_ai_caculator_app.ui.components.bmi

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BmiGauge(
    bmi: Float,
    needleRes: Int,
    modifier: Modifier = Modifier,
    maxHeight: Dp = 130.dp
) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = maxHeight)
            .aspectRatio(0.98f)
    ) {
        BmiGaugeBackground(
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight()
                .align(Alignment.BottomCenter),
            contentAlignment = Alignment.BottomCenter
        ) {
            BmiNeedle(
                bmi = bmi,
                needleRes = needleRes,
                modifier = Modifier
                    .fillMaxHeight(0.9f)
                    .aspectRatio(1.3f)
            )
        }
    }
}

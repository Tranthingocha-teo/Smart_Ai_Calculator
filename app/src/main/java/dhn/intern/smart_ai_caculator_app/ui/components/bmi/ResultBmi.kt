package dhn.intern.smart_ai_caculator_app.ui.components.bmi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dhn.intern.smart_ai_caculator_app.data.source.bmi.BmiResultData

@Composable
fun BmiResultList(bmi: Float) {
    val results = BmiResultData.getBmiResults(bmi)

    results.forEach {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(it.color, CircleShape)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = it.label,
                    color = if (it.isActive) it.color
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontWeight = if (it.isActive) FontWeight.Bold else FontWeight.Normal
                )
            }
            Text(
                text = it.range,
                color = if (it.isActive) it.color
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

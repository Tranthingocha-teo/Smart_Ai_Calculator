package dhn.intern.smart_ai_caculator_app.ui.components.graphing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomMathKeypad(
    onKeyPress: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val row1 = listOf("+f(x)", "x", "x²", "^", "√", "(", ")")
    val row2 = listOf("sin", "cos", "tan", "ln", "log", "π", "e")
    val row3 = listOf("7", "8", "9", "/", "AC")
    val row4 = listOf("4", "5", "6", "*", "⌫")
    val row5 = listOf("1", "2", "3", "-", "+")
    val row6 = listOf("0", ".", "Hide")

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Function & variable row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row1.forEach { key ->
                    KeypadButton(
                        text = key,
                        modifier = Modifier.weight(if (key == "+f(x)") 1.2f else 1.0f),
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        onClick = { onKeyPress(key) }
                    )
                }
            }

            // Function row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row2.forEach { key ->
                    KeypadButton(
                        text = key,
                        modifier = Modifier.weight(1.0f),
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
                        textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        onClick = { onKeyPress(key) }
                    )
                }
            }

            // Numeric & Operator rows 3 - 6
            val numRows = listOf(row3, row4, row5)
            numRows.forEach { rowKeys ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    rowKeys.forEach { key ->
                        val isOp = key in listOf("/", "*", "-", "+")
                        val isAc = key == "AC"
                        val isDel = key == "⌫"

                        val bgColor = when {
                            isAc -> MaterialTheme.colorScheme.errorContainer
                            isDel -> MaterialTheme.colorScheme.tertiaryContainer
                            isOp -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surfaceContainerHigh
                        }
                        val textColor = when {
                            isAc -> MaterialTheme.colorScheme.onErrorContainer
                            isDel -> MaterialTheme.colorScheme.onTertiaryContainer
                            isOp -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurface
                        }

                        KeypadButton(
                            text = key,
                            modifier = Modifier.weight(1.0f),
                            backgroundColor = bgColor,
                            textColor = textColor,
                            onClick = { onKeyPress(key) }
                        )
                    }
                }
            }

            // Bottom row (0, ., Hide)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                KeypadButton(
                    text = "0",
                    modifier = Modifier.weight(2.0f),
                    backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    onClick = { onKeyPress("0") }
                )
                KeypadButton(
                    text = ".",
                    modifier = Modifier.weight(1.0f),
                    backgroundColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    textColor = MaterialTheme.colorScheme.onSurface,
                    onClick = { onKeyPress(".") }
                )
                KeypadButton(
                    text = "Hide",
                    modifier = Modifier.weight(2.0f),
                    backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                    textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = { onKeyPress("Hide") }
                )
            }
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = if (text.length > 3) 12.sp else 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

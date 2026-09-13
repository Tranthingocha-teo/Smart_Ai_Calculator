package dhn.intern.smart_ai_caculator_app.ui.components.graphing

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
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
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 5-Column Keypad Layout
    val rows = listOf(
        listOf("x", "x²", "^", "(", ")"),
        listOf("sin", "cos", "tan", "√", "π"),
        listOf("7", "8", "9", "÷", "AC"),
        listOf("4", "5", "6", "×", "⌫"),
        listOf("1", "2", "3", "−", "+"),
        listOf("0", ".", "e", "ln", "log")
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 8.dp)
        ) {
            // Keypad Mini Header (Dismiss Bar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDismiss() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Hide keypad",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Ẩn bàn phím",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // 6 Rows of 5 Keys
            rows.forEach { rowKeys ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    rowKeys.forEach { key ->
                        val isOp = key in listOf("÷", "×", "−", "+")
                        val isAc = key == "AC"
                        val isDel = key == "⌫"
                        val isDigit = key in listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".")

                        val bgColor = when {
                            isAc -> MaterialTheme.colorScheme.errorContainer
                            isDel -> MaterialTheme.colorScheme.tertiaryContainer
                            isOp -> MaterialTheme.colorScheme.primaryContainer
                            isDigit -> MaterialTheme.colorScheme.surfaceContainerHighest
                            else -> MaterialTheme.colorScheme.secondaryContainer
                        }

                        val textColor = when {
                            isAc -> MaterialTheme.colorScheme.onErrorContainer
                            isDel -> MaterialTheme.colorScheme.onTertiaryContainer
                            isOp -> MaterialTheme.colorScheme.onPrimaryContainer
                            isDigit -> MaterialTheme.colorScheme.onSurface
                            else -> MaterialTheme.colorScheme.onSecondaryContainer
                        }

                        KeypadButton(
                            text = key,
                            modifier = Modifier.weight(1f),
                            backgroundColor = bgColor,
                            textColor = textColor,
                            onClick = { onKeyPress(key) }
                        )
                    }
                }
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
            .height(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = when {
                text.length >= 3 -> 13.sp
                text.length == 2 -> 15.sp
                else -> 17.sp
            },
            fontWeight = if (text in listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "x")) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}

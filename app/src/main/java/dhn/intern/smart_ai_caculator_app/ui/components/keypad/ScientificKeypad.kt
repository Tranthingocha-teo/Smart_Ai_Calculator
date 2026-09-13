package dhn.intern.smart_ai_caculator_app.ui.components.keypad

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dhn.intern.smart_ai_caculator_app.data.source.keypad.key_values

@Composable
fun ScientificKeypad(
    onKeyPress: (String) -> Unit
) {
    val scientificKeys = key_values.scientificKeys
    val mainKeys = key_values.mainKeys

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        scientificKeys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach {
                    CalcButton_scientific(
                        text = it,
                        modifier = Modifier.weight(1f),
                        background = keyBackgroundColor(it),
                    ) { onKeyPress(it) }
                }
            }
        }
        mainKeys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach {
                    CalcButton_scientific(
                        text = it,
                        modifier = Modifier.weight(1f),
                        background = keyBackgroundColor(it),
                        contentColor = if (it == "=")
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onBackground
                    ) {
                        onKeyPress(it)
                    }
                }
            }
        }
    }
}

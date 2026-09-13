package dhn.intern.smart_ai_caculator_app.ui.components.keypad

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dhn.intern.smart_ai_caculator_app.data.source.keypad.key_values.keys
import dhn.intern.smart_ai_caculator_app.data.source.keypad.key_values.unitKeys
import dhn.intern.smart_ai_caculator_app.data.source.keypad.key_values.unitKeysBasic

@Composable
fun SimpleKeypad(
    onKeyPress: (String) -> Unit
) {
    val keys = keys

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { key ->
                    CalcButton_simple(
                        text = key,
                        modifier = Modifier.weight(1f),
                        background = keyBackgroundColor(key),
                        contentColor = if (key == "=")
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onBackground
                    ) {
                        onKeyPress(key)
                    }
                }
            }
        }
    }
}
@Composable
fun UnitKeypad(
    onKeyPress: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Column(
            modifier = Modifier
                .weight(3f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val rows = unitKeysBasic.chunked(3)
            rows.forEach { row ->
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { key ->
                        CalcButton_simple_unit(
                            text = key.text,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            onClick = { onKeyPress(key.text) }
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            unitKeys.forEach { key ->
                CalcButton_simple_unit(
                    text = key.text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(if (key.spanRow == 2) 2f else 1f),
                    background = keyBackgroundColorUnit(key.type),
                    contentColor = if (key.text == "⇅")
                        Color.White
                    else
                        MaterialTheme.colorScheme.onBackground
                ) {
                    onKeyPress(key.text)
                }
            }
        }
    }
}





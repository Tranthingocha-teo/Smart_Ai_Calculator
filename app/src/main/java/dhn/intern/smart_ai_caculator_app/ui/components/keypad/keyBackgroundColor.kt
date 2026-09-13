package dhn.intern.smart_ai_caculator_app.ui.components.keypad

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dhn.intern.smart_ai_caculator_app.enum.KeyType
import dhn.intern.smart_ai_caculator_app.util.classifyKey

@Composable
fun keyBackgroundColor(key: String): Color {
    return when (classifyKey(key)) {
        KeyType.AC ->
            MaterialTheme.colorScheme.errorContainer

        KeyType.EQUAL ->
            MaterialTheme.colorScheme.outline

        KeyType.SCIENTIFIC ->
            MaterialTheme.colorScheme.secondaryContainer

        else ->
            MaterialTheme.colorScheme.surfaceVariant
    }
}
@Composable
fun keyBackgroundColorUnit(keytype: KeyType): Color {
    return when (keytype) {
        KeyType.AC ->
            MaterialTheme.colorScheme.errorContainer

        KeyType.EQUAL ->
            MaterialTheme.colorScheme.outline

        KeyType.SCIENTIFIC ->
            MaterialTheme.colorScheme.secondaryContainer

        else ->
            MaterialTheme.colorScheme.surfaceVariant
    }
}

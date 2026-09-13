package dhn.intern.smart_ai_caculator_app.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun ThemeSwitchItem(
    isDark: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        checked = isDark,
        onCheckedChange = onCheckedChange
    )
}

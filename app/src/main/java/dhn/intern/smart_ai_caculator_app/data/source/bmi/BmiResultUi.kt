package dhn.intern.smart_ai_caculator_app.data.source.bmi

import androidx.compose.ui.graphics.Color

data class BmiResultUi(
    val label: String,
    val range: String,
    val color: Color,
    val isActive: Boolean
)


package dhn.intern.smart_ai_caculator_app.data.source.messenge

import androidx.compose.ui.graphics.painter.Painter

data class UserMessageUi(
    val text: String? = null,
    val image: Painter? = null,
    val time: String
)
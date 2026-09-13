package dhn.intern.smart_ai_caculator_app.data.source.keypad

import dhn.intern.smart_ai_caculator_app.enum.KeyType

data class KeyUi(
    val text: String,
    val spanRow: Int = 1,
    val type: KeyType = KeyType.NORMAL
)


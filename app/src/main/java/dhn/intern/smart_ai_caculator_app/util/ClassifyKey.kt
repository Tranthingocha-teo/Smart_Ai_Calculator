package dhn.intern.smart_ai_caculator_app.util

import dhn.intern.smart_ai_caculator_app.enum.KeyType

fun classifyKey(key: String): KeyType = when (key) {
    "AC" -> KeyType.AC
    "=", "⇅" -> KeyType.EQUAL
    "sin", "cos", "tan", "log", "ln", "√", "∛", "π", "φ", "e",  "+", "−", "×", "÷", "%", "^", "( )", "C"  -> KeyType.SCIENTIFIC
    else -> KeyType.NORMAL
}

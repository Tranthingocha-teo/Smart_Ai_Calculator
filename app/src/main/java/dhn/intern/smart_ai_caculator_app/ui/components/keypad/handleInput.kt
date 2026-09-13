package dhn.intern.smart_ai_caculator_app.ui.components.keypad

fun handleInput(current: String, key: String): String {
    return when (key) {
        "C" -> ""
        "⌫" -> current.dropLast(1)
        else -> current + key
    }
}
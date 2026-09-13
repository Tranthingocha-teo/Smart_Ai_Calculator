package dhn.intern.smart_ai_caculator_app.ui.components.keypad

fun handleInput(current: String, key: String): String {
    return when (key) {
        "C" -> "0"
        "⌫" -> {
            if (current.isEmpty() || current == "0" || current.length <= 1) {
                "0"
            } else if (current.startsWith("-") && current.length == 2) {
                "0"
            } else {
                current.dropLast(1)
            }
        }
        "+/-" -> {
            if (current == "0" || current.isEmpty()) {
                "-"
            } else if (current.startsWith("-")) {
                val withoutMinus = current.removePrefix("-")
                if (withoutMinus.isEmpty()) "0" else withoutMinus
            } else {
                "-$current"
            }
        }
        "." -> {
            if (current.contains(".")) {
                current
            } else if (current.isEmpty() || current == "0") {
                "0."
            } else if (current == "-") {
                "-0."
            } else {
                "$current."
            }
        }
        in "0".."9" -> {
            if (current == "0") {
                key
            } else if (current == "-0") {
                "-$key"
            } else {
                current + key
            }
        }
        else -> current
    }
}
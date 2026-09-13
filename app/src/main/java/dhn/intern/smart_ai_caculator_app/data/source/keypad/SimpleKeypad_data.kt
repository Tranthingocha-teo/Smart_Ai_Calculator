package dhn.intern.smart_ai_caculator_app.data.source.keypad

import dhn.intern.smart_ai_caculator_app.enum.KeyType


object key_values{
    val keys = listOf(
        listOf("AC", "( )", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "−"),
        listOf("1", "2", "3", "+"),
        listOf("0", ".", "⌫", "=")
    )
    val scientificKeys = listOf(
        listOf("φ", "e", "ln", "log"),
        listOf("sin", "cos", "tan", "π"),
        listOf("e", "∛", "√", "^")
    )

    val mainKeys = listOf(
        listOf("AC", "()", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "−"),
        listOf("1", "2", "3", "+"),
        listOf("0", ".", "⌫", "=")
    )

    val unitKeysBasic = listOf(
        KeyUi("7"), KeyUi("8"), KeyUi("9"),
        KeyUi("4"), KeyUi("5"), KeyUi("6"),
        KeyUi("1"), KeyUi("2"), KeyUi("3"),
        KeyUi("+/-"), KeyUi("."), KeyUi("0")
    )

    val unitKeys = listOf(
        KeyUi("⌫", type = KeyType.SCIENTIFIC),
        KeyUi("C", type = KeyType.SCIENTIFIC),
        KeyUi("⇅", spanRow = 2, type = KeyType.EQUAL),
    )

}

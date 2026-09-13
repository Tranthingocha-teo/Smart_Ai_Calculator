package dhn.intern.smart_ai_caculator_app.data.source.keypad

sealed class Token {
    data class Number(val value: Double) : Token()
    data class Operator(val op: Char) : Token()
    object LeftParen : Token()
    object RightParen : Token()
}
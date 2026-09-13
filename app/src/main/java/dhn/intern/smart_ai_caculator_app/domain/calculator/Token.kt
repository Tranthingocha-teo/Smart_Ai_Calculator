package dhn.intern.smart_ai_caculator_app.domain.calculator

sealed class Token {
    data class Number(val value: Double) : Token()
    data class Operator(val symbol: String) : Token()
    data class Function(val name: String) : Token()
    data class Variable(val name: String = "x") : Token()
    object LeftParen : Token()
    object RightParen : Token()
}

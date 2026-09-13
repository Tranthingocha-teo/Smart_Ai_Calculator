package dhn.intern.smart_ai_caculator_app.domain.graphing

import dhn.intern.smart_ai_caculator_app.domain.calculator.Token
import java.util.UUID
import kotlin.math.*

data class CompiledFunction(
    val id: String = UUID.randomUUID().toString(),
    val rawExpression: String,
    val postfixTokens: List<Token>
) {
    fun evaluate(x: Double): Double {
        val stack = DoubleArray(postfixTokens.size + 4)
        var sp = 0

        for (token in postfixTokens) {
            when (token) {
                is Token.Number -> {
                    stack[sp++] = token.value
                }
                is Token.Variable -> {
                    stack[sp++] = x
                }
                is Token.Operator -> {
                    if (sp < 2) return Double.NaN
                    val b = stack[--sp]
                    val a = stack[--sp]
                    val res = when (token.symbol) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        "/" -> if (b == 0.0) {
                            if (a > 0.0) Double.POSITIVE_INFINITY
                            else if (a < 0.0) Double.NEGATIVE_INFINITY
                            else Double.NaN
                        } else a / b
                        "^" -> a.pow(b)
                        else -> return Double.NaN
                    }
                    stack[sp++] = res
                }
                is Token.Function -> {
                    if (sp < 1) return Double.NaN
                    val a = stack[--sp]
                    val res = when (token.name) {
                        "sin" -> sin(a)
                        "cos" -> cos(a)
                        "tan" -> tan(a)
                        "log" -> log10(a)
                        "ln" -> ln(a)
                        "sqrt" -> sqrt(a)
                        "cbrt" -> cbrt(a)
                        "abs" -> abs(a)
                        else -> return Double.NaN
                    }
                    stack[sp++] = res
                }
                else -> {}
            }
        }

        return if (sp == 1) stack[0] else Double.NaN
    }
}

package dhn.intern.smart_ai_caculator_app.domain.calculator

import kotlin.math.*

class Evaluator {

    // Evaluator.kt
    fun evaluate(postfix: List<Token>): Double {
        val stack = mutableListOf<Double>()

        postfix.forEach { token ->
            when (token) {
                is Token.Number -> stack.add(token.value)

                is Token.Operator -> {
                    if (stack.size < 2) {
                        throw IllegalArgumentException("Invalid expression: Not enough operands for operator ${token.symbol}")
                    }

                    val b = stack.removeAt(stack.size - 1)
                    val a = stack.removeAt(stack.size - 1)


                    val result = when (token.symbol) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        "/" -> {
                            if (b == 0.0) throw ArithmeticException("Division by zero")
                            a / b
                        }
                        "^" -> a.pow(b)
                        else -> throw IllegalArgumentException("Unknown operator ${token.symbol}")
                    }

                    stack.add(result)
                }

                is Token.Function -> {
                    if (stack.isEmpty()) {
                        throw IllegalArgumentException("Invalid expression: Missing operand for function ${token.name}")
                    }
                    val a = stack.removeAt(stack.size - 1)
                    stack.add(
                        when (token.name) {
                            "sin" -> sin(Math.toRadians(a))
                            "cos" -> cos(Math.toRadians(a))
                            "tan" -> tan(Math.toRadians(a))
                            "log" -> log10(a)
                            "ln" -> ln(a)
                            "sqrt" -> sqrt(a)
                            else -> throw IllegalArgumentException("Unknown function ${token.name}")
                        }
                    )
                }

                else -> {}
            }
        }
        if (stack.size != 1) {
            throw IllegalArgumentException("Invalid expression: Stack contains extra operands")
        }
        return stack.last()
    }
}

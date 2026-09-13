package dhn.intern.smart_ai_caculator_app.domain.calculator

import kotlin.text.get

class Parser {

    private val precedence = mapOf(
        "+" to 1,
        "-" to 1,
        "*" to 2,
        "/" to 2,
        "^" to 3
    )

    fun toPostfix(tokens: List<Token>): List<Token> {
        val output = mutableListOf<Token>()
        val stack = mutableListOf<Token>()

        tokens.forEach { token ->
            when (token) {
                is Token.Number -> output.add(token)

                is Token.Function -> stack.add(token)

                is Token.Operator -> {
                    while (
                        stack.isNotEmpty() &&
                        stack.last() is Token.Operator &&
                        precedence[(stack.last() as Token.Operator).symbol]!! >= precedence[token.symbol]!!
                    ) {
                        output.add(stack.removeAt(stack.lastIndex))
                    }
                    stack.add(token)
                }

                Token.LeftParen -> stack.add(token)

                Token.RightParen -> {
                    while (stack.isNotEmpty() && stack.last() !is Token.LeftParen) {
                        output.add(stack.removeAt(stack.lastIndex))
                    }
                    if (stack.isEmpty() || stack.last() !is Token.LeftParen) {
                        throw IllegalArgumentException("Mismatched parentheses")
                    }
                    output.add(stack.removeAt(stack.lastIndex))
                    if (stack.lastOrNull() is Token.Function) {
                        output.add(stack.removeAt(stack.lastIndex))
                    }
                }
            }
        }

        while (stack.isNotEmpty()) {
            val top = stack.removeAt(stack.lastIndex)
            if (top is Token.LeftParen || top is Token.RightParen) {
                throw IllegalArgumentException("Mismatched parentheses")
            }
            output.add(top)
        }
        return output
    }
}

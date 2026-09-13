package dhn.intern.smart_ai_caculator_app.domain.calculator

class Tokenizer {

    private val functions = listOf(
        "sin", "cos", "tan",
        "log", "ln",
        "sqrt", "cbrt"
    )

    private val unicodeFunctions = mapOf(
        '√' to "sqrt",
        '∛' to "cbrt"
    )

    fun tokenize(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0

        while (i < input.length) {
            when {
                input[i].isDigit() || input[i] == '.' -> {
                    val start = i
                    while (i < input.length && (input[i].isDigit() || input[i] == '.')) i++
                    tokens.add(Token.Number(input.substring(start, i).toDouble()))
                }

                functions.any { input.startsWith(it, i) } -> {
                    val fn = functions.first { input.startsWith(it, i) }
                    tokens.add(Token.Function(fn))
                    i += fn.length
                }

                unicodeFunctions.containsKey(input[i]) -> {
                    tokens.add(Token.Function(unicodeFunctions[input[i]]!!))
                    i++
                }
                input[i] == '(' -> {
                    tokens.add(Token.LeftParen)
                    i++
                }
                input[i] == 'e' -> {
                    tokens.add(Token.Number(Math.E))
                    i++
                }
                input[i] == ')' -> {
                    tokens.add(Token.RightParen)
                    i++
                }

                "+-*/^".contains(input[i]) -> {
                    val op = input[i]

                    if (op == '-' && isUnaryMinus(tokens, i)) {
                        tokens.add(Token.Number(0.0))
                    }
                    tokens.add(Token.Operator(op.toString()))
                    i++
                }


                input[i].isWhitespace() -> i++ // Skip whitespace

                else -> throw IllegalArgumentException("Unsupported character: ${input[i]}")
            }
        }
        return tokens
    }
    private fun isUnaryMinus(tokens: List<Token>, currentIndex: Int): Boolean {
        if (tokens.isEmpty()) return true
        val last = tokens.last()
        return last is Token.Operator || last is Token.LeftParen
    }


}

package dhn.intern.smart_ai_caculator_app.domain.calculator

class CalculatorEngine {

    private val tokenizer = Tokenizer()
    private val parser = Parser()
    private val evaluator = Evaluator()

    private fun normalize(input: String): String =
        input.replace('×', '*')
            .replace('÷', '/')
            .replace('−', '-')

    fun calculate(expression: String): Result<Double> {
        return runCatching {
            val normalized = normalize(expression)
            val tokens = tokenizer.tokenize(normalized)
            val postfix = parser.toPostfix(tokens)
            evaluator.evaluate(postfix)
        }
    }
}

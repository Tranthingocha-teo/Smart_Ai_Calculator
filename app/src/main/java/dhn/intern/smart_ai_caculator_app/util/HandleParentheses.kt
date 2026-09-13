package dhn.intern.smart_ai_caculator_app.util

fun handleParentheses(expression: String): String {
    val openCount = expression.count { it == '(' }
    val closeCount = expression.count { it == ')' }

    val lastChar = expression.lastOrNull()

    val shouldOpen = openCount == closeCount ||
            lastChar == null ||
            lastChar in "+−×÷%^("

    return if (shouldOpen) {
        expression + "("
    } else if (openCount > closeCount && lastChar !in "+−×÷^(") {
        expression + ")"
    } else {
        expression
    }
}

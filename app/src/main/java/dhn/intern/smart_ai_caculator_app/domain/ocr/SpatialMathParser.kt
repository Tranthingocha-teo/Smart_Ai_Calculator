package dhn.intern.smart_ai_caculator_app.domain.ocr

import dhn.intern.smart_ai_caculator_app.domain.ocr.model.RecognizedSymbol

/**
 * Parses a spatially located sequence of recognized mathematical symbols into a valid
 * mathematical expression string ready for evaluation or graphing.
 *
 * Handles:
 * 1. Exponent / Superscript detection (e.g. x followed by elevated 2 -> x^2)
 * 2. Decimal points and comma normalization
 * 3. Implicit multiplication insertion (e.g. 2x -> 2*x, 3(x+1) -> 3*(x+1))
 * 4. Division / fraction bars
 * 5. Parenthesis and operator normalization
 */
class SpatialMathParser(
    private val exponentElevationThreshold: Float = 0.40f, // vertical ratio above baseline
    private val maxExponentHeightRatio: Float = 1.15f
) {

    /**
     * Parses sorted symbols into a formatted mathematical string.
     */
    fun parse(symbols: List<RecognizedSymbol>): String {
        if (symbols.isEmpty()) return ""
        if (symbols.size == 1) return symbols[0].text

        // Sort symbols strictly left-to-right
        val sorted = symbols.sortedBy { it.box.left }

        // Check for horizontal fraction bars first
        val parsedWithFractions = processFractionBars(sorted)
        if (parsedWithFractions != null) {
            return parsedWithFractions
        }

        val sb = StringBuilder()
        var inExponent = false
        var prevSymbol: RecognizedSymbol? = null

        for (i in sorted.indices) {
            val curr = sorted[i]
            val prev = prevSymbol

            if (prev != null) {
                val isElevated = checkIsElevated(prev, curr)

                if (isElevated) {
                    if (!inExponent) {
                        sb.append("^")
                        inExponent = true
                    }
                } else {
                    inExponent = false
                }

                // Check for implicit multiplication (e.g. 2x -> 2*x, 3( -> 3*(), x( -> x*() )
                if (needsImplicitMultiplication(prev.text, curr.text) && !inExponent) {
                    sb.append("*")
                }
            }

            // Normalize symbol text (e.g. comma as decimal point if between digits)
            val normalizedToken = normalizeToken(curr.text, prev?.text, sorted.getOrNull(i + 1)?.text)
            sb.append(normalizedToken)

            prevSymbol = curr
        }

        return sanitizeExpression(sb.toString())
    }

    /**
     * Checks if current symbol is positioned as a superscript / exponent relative to base symbol.
     */
    private fun checkIsElevated(base: RecognizedSymbol, elevated: RecognizedSymbol): Boolean {
        // Only alphanumeric characters or '(' can be exponents
        val token = elevated.text
        val isEligible = token.all { it.isLetterOrDigit() } || token == "(" || token == ")"
        if (!isEligible) return false

        val baseCenterY = base.box.centerY
        val baseTop = base.box.top
        val elevatedCenterY = elevated.box.centerY
        val elevatedBottom = elevated.box.bottom

        // Elevated if elevated's bottom is above or near base's center
        val isAboveBaseline = elevatedBottom <= baseCenterY + (base.box.height * 0.15f) ||
                elevatedCenterY < baseTop + (base.box.height * exponentElevationThreshold)

        val isReasonableHeight = elevated.box.height <= base.box.height * maxExponentHeightRatio

        return isAboveBaseline && isReasonableHeight
    }

    /**
     * Checks whether an implicit multiplication '*' should be inserted between two adjacent tokens.
     */
    fun needsImplicitMultiplication(prevToken: String, currToken: String): Boolean {
        if (prevToken.isEmpty() || currToken.isEmpty()) return false

        val prevLast = prevToken.last()
        val currFirst = currToken.first()

        val isPrevDigitOrVar = prevLast.isDigit() || prevLast == 'x' || prevLast == 'y'
        val isCurrVar = currFirst == 'x' || currFirst == 'y'

        // Digit followed by variable: 2x -> 2*x
        if (prevLast.isDigit() && isCurrVar) return true

        // Variable followed by variable: xy -> x*y
        if ((prevLast == 'x' || prevLast == 'y') && isCurrVar) return true

        // Digit or variable followed by open parenthesis: 2( -> 2*(, x( -> x*(
        if (isPrevDigitOrVar && currFirst == '(') return true

        // Close parenthesis followed by digit, variable, or open parenthesis: )2 -> )*2, )x -> )*x, )( -> )*(
        if (prevLast == ')' && (currFirst.isDigit() || isCurrVar || currFirst == '(')) return true

        return false
    }

    /**
     * Normalizes token representation (e.g. comma into dot for decimal fractions).
     */
    private fun normalizeToken(token: String, prevToken: String?, nextToken: String?): String {
        if (token == ",") {
            val isPrevDigit = prevToken?.lastOrNull()?.isDigit() == true
            val isNextDigit = nextToken?.firstOrNull()?.isDigit() == true
            if (isPrevDigit && isNextDigit) {
                return "."
            }
        }
        return token
    }

    /**
     * Detects horizontal fraction bars with symbols directly stacked above and below it.
     */
    private fun processFractionBars(symbols: List<RecognizedSymbol>): String? {
        val fractionBars = symbols.filter { sym ->
            (sym.text == "-" || sym.text == "/") &&
                    sym.box.aspectRatio >= 2.5f &&
                    sym.box.width >= 30
        }

        for (bar in fractionBars) {
            val above = mutableListOf<RecognizedSymbol>()
            val below = mutableListOf<RecognizedSymbol>()

            val barLeft = bar.box.left - 10
            val barRight = bar.box.right + 10

            for (s in symbols) {
                if (s == bar) continue
                if (s.box.centerX in barLeft.toFloat()..barRight.toFloat()) {
                    if (s.box.bottom <= bar.box.top + 5) {
                        above.add(s)
                    } else if (s.box.top >= bar.box.bottom - 5) {
                        below.add(s)
                    }
                }
            }

            if (above.isNotEmpty() && below.isNotEmpty()) {
                val numStr = parse(above)
                val denStr = parse(below)
                val otherSymbols = symbols.filter { it != bar && !above.contains(it) && !below.contains(it) }

                val fractionExpr = "($numStr)/($denStr)"
                return if (otherSymbols.isEmpty()) {
                    fractionExpr
                } else {
                    // Assemble prefix, fraction, suffix based on X order
                    val leftSymbols = otherSymbols.filter { it.box.right <= bar.box.left }
                    val rightSymbols = otherSymbols.filter { it.box.left >= bar.box.right }
                    val prefix = if (leftSymbols.isNotEmpty()) parse(leftSymbols) else ""
                    val suffix = if (rightSymbols.isNotEmpty()) parse(rightSymbols) else ""
                    listOf(prefix, fractionExpr, suffix).filter { it.isNotEmpty() }.joinToString("")
                }
            }
        }

        return null
    }

    /**
     * Cleans up unwanted double operators or trailing artifacts.
     */
    fun sanitizeExpression(expr: String): String {
        var result = expr.trim()
        // Replace multiple consecutive operators
        result = result.replace(Regex("\\+{2,}"), "+")
        result = result.replace(Regex("-{2,}"), "+")
        result = result.replace(Regex("\\*{2,}"), "*")
        result = result.replace(Regex("/{2,}"), "/")
        result = result.replace(Regex("\\^{2,}"), "^")
        result = result.replace(Regex("\\*\\+"), "*")
        result = result.replace(Regex("/\\+"), "/")

        return result
    }
}

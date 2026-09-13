package dhn.intern.smart_ai_caculator_app.domain.graphing

import dhn.intern.smart_ai_caculator_app.domain.calculator.Token
import java.util.UUID
import kotlin.math.*

class DefaultGraphingEngine : GraphingEngine {

    override fun compile(expression: String): Result<CompiledFunction> {
        return runCatching {
            val trimmed = expression.trim()
            if (trimmed.isEmpty()) {
                throw IllegalArgumentException("Expression cannot be empty")
            }
            val normalized = normalizeExpression(trimmed)
            val tokens = tokenize(normalized)
            validateTokens(tokens)
            val postfix = toPostfix(tokens)

            val compiled = CompiledFunction(
                id = UUID.randomUUID().toString(),
                rawExpression = trimmed,
                postfixTokens = postfix
            )

            // Test evaluation on test points
            val t1 = compiled.evaluate(1.0)
            val t2 = compiled.evaluate(2.0)
            val t3 = compiled.evaluate(-1.0)
            val t4 = compiled.evaluate(0.0)

            if (t1.isNaN() && t2.isNaN() && t3.isNaN() && t4.isNaN()) {
                throw IllegalArgumentException("Invalid mathematical expression: cannot evaluate to finite number")
            }

            compiled
        }
    }

    override fun sample(
        function: CompiledFunction,
        viewport: ViewportBounds,
        screenPixelWidth: Int
    ): SampledCurve {
        val steps = max(screenPixelWidth, 400)
        val stepSize = viewport.width / steps

        val continuousSegments = mutableListOf<MutableList<GraphPoint>>()
        var currentSegment = mutableListOf<GraphPoint>()

        val specialPoints = mutableListOf<SpecialPoint>()

        // Pre-sample points to allow 3-point local extrema scanning
        val xs = DoubleArray(steps + 1) { i -> viewport.minX + i * stepSize }
        val ys = DoubleArray(steps + 1) { i -> function.evaluate(xs[i]) }

        for (i in 0..steps) {
            val currX = xs[i]
            val currY = ys[i]
            val currValid = isValidPoint(currY)

            if (currValid) {
                if (currentSegment.isEmpty()) {
                    currentSegment.add(GraphPoint(currX, currY))
                } else {
                    val prevPoint = currentSegment.last()
                    val isAsymptote = detectAsymptote(
                        prevX = prevPoint.x,
                        prevY = prevPoint.y,
                        currX = currX,
                        currY = currY,
                        viewport = viewport,
                        function = function
                    )

                    if (isAsymptote) {
                        continuousSegments.add(currentSegment)
                        currentSegment = mutableListOf(GraphPoint(currX, currY))
                    } else {
                        // Check for root in [prevPoint.x, currX]
                        if (prevPoint.y * currY <= 0.0) {
                            findRoot(function, prevPoint.x, currX, prevPoint.y, currY)?.let { root ->
                                if (root.x in viewport.minX..viewport.maxX) {
                                    specialPoints.add(
                                        SpecialPoint(root, SpecialPointType.ROOT, "Root (${round4(root.x)}, 0)")
                                    )
                                }
                            }
                        }
                        currentSegment.add(GraphPoint(currX, currY))
                    }
                }
            } else {
                if (currentSegment.isNotEmpty()) {
                    continuousSegments.add(currentSegment)
                    currentSegment = mutableListOf()
                }
            }
        }

        if (currentSegment.isNotEmpty()) {
            continuousSegments.add(currentSegment)
        }

        // Scan for local extrema using triplet peaks/valleys
        for (i in 1 until steps) {
            val yPrev = ys[i - 1]
            val yCurr = ys[i]
            val yNext = ys[i + 1]

            if (isValidPoint(yPrev) && isValidPoint(yCurr) && isValidPoint(yNext)) {
                // Peak -> Local Max
                if (yCurr > yPrev && yCurr > yNext) {
                    findLocalMax(function, xs[i - 1], xs[i + 1])?.let { ext ->
                        if (ext.point.x in viewport.minX..viewport.maxX &&
                            ext.point.y in (viewport.minY - 1.0)..(viewport.maxY + 1.0)) {
                            specialPoints.add(ext)
                        }
                    }
                }
                // Valley -> Local Min
                else if (yCurr < yPrev && yCurr < yNext) {
                    findLocalMin(function, xs[i - 1], xs[i + 1])?.let { ext ->
                        if (ext.point.x in viewport.minX..viewport.maxX &&
                            ext.point.y in (viewport.minY - 1.0)..(viewport.maxY + 1.0)) {
                            specialPoints.add(ext)
                        }
                    }
                }
            }
        }

        // Check y-intercept (at x = 0)
        if (0.0 in viewport.minX..viewport.maxX) {
            val yAtZero = function.evaluate(0.0)
            if (isValidPoint(yAtZero) && yAtZero in viewport.minY..viewport.maxY) {
                if (specialPoints.none { abs(it.point.x) < 1e-4 && abs(it.point.y - yAtZero) < 1e-4 }) {
                    specialPoints.add(
                        SpecialPoint(
                            GraphPoint(0.0, yAtZero),
                            SpecialPointType.Y_INTERCEPT,
                            "y-intercept (0, ${round4(yAtZero)})"
                        )
                    )
                }
            }
        }

        val deduped = deduplicateSpecialPoints(specialPoints)

        return SampledCurve(
            functionId = function.id,
            continuousSegments = continuousSegments,
            specialPoints = deduped
        )
    }

    override fun findIntersections(
        f: CompiledFunction,
        g: CompiledFunction,
        viewport: ViewportBounds
    ): List<SpecialPoint> {
        val steps = max(500, (viewport.width * 50).toInt())
        val stepSize = viewport.width / steps
        val results = mutableListOf<SpecialPoint>()

        var prevX = viewport.minX
        var prevDiff = f.evaluate(prevX) - g.evaluate(prevX)

        for (i in 1..steps) {
            val currX = viewport.minX + i * stepSize
            val currF = f.evaluate(currX)
            val currG = g.evaluate(currX)

            if (isValidPoint(currF) && isValidPoint(currG)) {
                val currDiff = currF - currG
                if (isValidPoint(prevDiff) && prevDiff * currDiff <= 0.0) {
                    var lo = prevX
                    var hi = currX
                    var fLo = prevDiff
                    repeat(25) {
                        val mid = (lo + hi) / 2.0
                        val fMid = f.evaluate(mid) - g.evaluate(mid)
                        if (abs(fMid) < 1e-9) {
                            lo = mid
                            hi = mid
                            return@repeat
                        }
                        if (fLo * fMid <= 0.0) {
                            hi = mid
                        } else {
                            lo = mid
                            fLo = fMid
                        }
                    }
                    val intX = (lo + hi) / 2.0
                    val intY = f.evaluate(intX)
                    if (isValidPoint(intY) && intX in viewport.minX..viewport.maxX && intY in viewport.minY..viewport.maxY) {
                        results.add(
                            SpecialPoint(
                                GraphPoint(intX, intY),
                                SpecialPointType.INTERSECTION,
                                "Intersection (${round4(intX)}, ${round4(intY)})"
                            )
                        )
                    }
                }
                prevDiff = currDiff
            } else {
                prevDiff = Double.NaN
            }
            prevX = currX
        }

        return deduplicateSpecialPoints(results)
    }

    private fun isValidPoint(y: Double): Boolean {
        return !y.isNaN() && !y.isInfinite()
    }

    private fun detectAsymptote(
        prevX: Double,
        prevY: Double,
        currX: Double,
        currY: Double,
        viewport: ViewportBounds,
        function: CompiledFunction
    ): Boolean {
        val signFlip = (prevY < 0 && currY > 0) || (prevY > 0 && currY < 0)
        if (!signFlip) return false

        // Crossing opposite viewport boundaries
        if ((prevY < viewport.minY && currY > viewport.maxY) ||
            (prevY > viewport.maxY && currY < viewport.minY)) {
            return true
        }

        // Test midpoint behavior: if midpoint explodes or is non-finite
        val midX = (prevX + currX) / 2.0
        val midY = function.evaluate(midX)
        if (!isValidPoint(midY) || abs(midY) > max(abs(prevY), abs(currY)) * 1.5) {
            return true
        }

        // Large vertical gradient jump
        val deltaY = abs(currY - prevY)
        if (deltaY > viewport.height * 1.5) {
            return true
        }

        return false
    }

    private fun findRoot(
        function: CompiledFunction,
        x1: Double,
        x2: Double,
        y1: Double,
        y2: Double
    ): GraphPoint? {
        var lo = x1
        var hi = x2
        var fLo = y1

        repeat(25) {
            val mid = (lo + hi) / 2.0
            val fMid = function.evaluate(mid)
            if (abs(fMid) < 1e-9) {
                return GraphPoint(mid, 0.0)
            }
            if (fLo * fMid <= 0.0) {
                hi = mid
            } else {
                lo = mid
                fLo = fMid
            }
        }

        val rootX = (lo + hi) / 2.0
        val rootY = function.evaluate(rootX)
        return if (abs(rootY) < 0.05) GraphPoint(rootX, 0.0) else null
    }

    private fun findLocalMax(function: CompiledFunction, xLeft: Double, xRight: Double): SpecialPoint? {
        val phi = (sqrt(5.0) - 1.0) / 2.0
        var a = xLeft
        var b = xRight
        var c = b - phi * (b - a)
        var d = a + phi * (b - a)

        repeat(25) {
            val fc = function.evaluate(c)
            val fd = function.evaluate(d)
            if (!isValidPoint(fc) || !isValidPoint(fd)) return null
            if (fc > fd) {
                b = d
                d = c
                c = b - phi * (b - a)
            } else {
                a = c
                c = d
                d = a + phi * (b - a)
            }
        }

        val extX = (a + b) / 2.0
        val extY = function.evaluate(extX)
        if (!isValidPoint(extY)) return null
        return SpecialPoint(GraphPoint(extX, extY), SpecialPointType.LOCAL_MAX, "Max (${round4(extX)}, ${round4(extY)})")
    }

    private fun findLocalMin(function: CompiledFunction, xLeft: Double, xRight: Double): SpecialPoint? {
        val phi = (sqrt(5.0) - 1.0) / 2.0
        var a = xLeft
        var b = xRight
        var c = b - phi * (b - a)
        var d = a + phi * (b - a)

        repeat(25) {
            val fc = function.evaluate(c)
            val fd = function.evaluate(d)
            if (!isValidPoint(fc) || !isValidPoint(fd)) return null
            if (fc < fd) {
                b = d
                d = c
                c = b - phi * (b - a)
            } else {
                a = c
                c = d
                d = a + phi * (b - a)
            }
        }

        val extX = (a + b) / 2.0
        val extY = function.evaluate(extX)
        if (!isValidPoint(extY)) return null
        return SpecialPoint(GraphPoint(extX, extY), SpecialPointType.LOCAL_MIN, "Min (${round4(extX)}, ${round4(extY)})")
    }

    private fun deduplicateSpecialPoints(points: List<SpecialPoint>): List<SpecialPoint> {
        val result = mutableListOf<SpecialPoint>()
        for (p in points) {
            val exists = result.any { existing ->
                abs(existing.point.x - p.point.x) < 1e-3 &&
                abs(existing.point.y - p.point.y) < 1e-3 &&
                existing.type == p.type
            }
            if (!exists) {
                result.add(p)
            }
        }
        return result
    }

    private fun round4(value: Double): String {
        return "%.4f".format(value).trimEnd('0').trimEnd('.')
    }

    // ==========================================
    // Parser & Tokenizer Subsystem
    // ==========================================

    private fun normalizeExpression(raw: String): String {
        return raw.replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("²", "^2")
            .replace("³", "^3")
            .replace("⁴", "^4")
            .replace("π", "(${Math.PI})")
            .replace("pi", "(${Math.PI})")
    }

    private fun tokenize(input: String): List<Token> {
        val rawTokens = mutableListOf<Token>()
        var i = 0

        val functions = listOf(
            "sin", "cos", "tan",
            "log", "ln",
            "sqrt", "cbrt", "abs"
        )

        while (i < input.length) {
            val char = input[i]
            when {
                char.isWhitespace() -> i++

                char.isDigit() || char == '.' -> {
                    val start = i
                    while (i < input.length && (input[i].isDigit() || input[i] == '.')) i++
                    rawTokens.add(Token.Number(input.substring(start, i).toDouble()))
                }

                char == 'x' || char == 'X' -> {
                    rawTokens.add(Token.Variable("x"))
                    i++
                }

                char == 'e' -> {
                    rawTokens.add(Token.Number(Math.E))
                    i++
                }

                char == '(' -> {
                    rawTokens.add(Token.LeftParen)
                    i++
                }

                char == ')' -> {
                    rawTokens.add(Token.RightParen)
                    i++
                }

                functions.any { input.startsWith(it, i) } -> {
                    val fn = functions.first { input.startsWith(it, i) }
                    rawTokens.add(Token.Function(fn))
                    i += fn.length
                }

                "+-*/^".contains(char) -> {
                    if (char == '-' && isUnaryMinus(rawTokens)) {
                        rawTokens.add(Token.Number(0.0))
                    }
                    rawTokens.add(Token.Operator(char.toString()))
                    i++
                }

                char == '√' -> {
                    rawTokens.add(Token.Function("sqrt"))
                    i++
                }

                char == '∛' -> {
                    rawTokens.add(Token.Function("cbrt"))
                    i++
                }

                else -> throw IllegalArgumentException("Unsupported character: '$char'")
            }
        }

        // Insert implicit multiplications
        val tokensWithImplicitMul = mutableListOf<Token>()
        for (idx in rawTokens.indices) {
            val current = rawTokens[idx]
            if (idx > 0) {
                val prev = rawTokens[idx - 1]
                val prevCanEnd = prev is Token.Number || prev is Token.Variable || prev == Token.RightParen
                val currCanStart = current is Token.Variable || current is Token.Function || current == Token.LeftParen ||
                        (current is Token.Number && (prev is Token.Variable || prev == Token.RightParen))

                if (prevCanEnd && currCanStart) {
                    tokensWithImplicitMul.add(Token.Operator("*"))
                }
            }
            tokensWithImplicitMul.add(current)
        }

        return tokensWithImplicitMul
    }

    private fun isUnaryMinus(tokens: List<Token>): Boolean {
        if (tokens.isEmpty()) return true
        val last = tokens.last()
        return last is Token.Operator || last == Token.LeftParen
    }

    private fun validateTokens(tokens: List<Token>) {
        if (tokens.isEmpty()) throw IllegalArgumentException("Expression cannot be empty")

        val first = tokens.first()
        if (first is Token.Operator && first.symbol != "+" && first.symbol != "-") {
            throw IllegalArgumentException("Expression cannot start with operator ${first.symbol}")
        }

        val last = tokens.last()
        if (last is Token.Operator) {
            throw IllegalArgumentException("Expression cannot end with operator ${last.symbol}")
        }

        for (i in 0 until tokens.size - 1) {
            val curr = tokens[i]
            val next = tokens[i + 1]

            if (curr is Token.Operator && next is Token.Operator) {
                throw IllegalArgumentException("Invalid consecutive operators: ${curr.symbol} ${next.symbol}")
            }
            if (curr == Token.LeftParen && next == Token.RightParen) {
                throw IllegalArgumentException("Empty parentheses are not allowed")
            }
        }
    }

    private fun toPostfix(tokens: List<Token>): List<Token> {
        val precedence = mapOf(
            "+" to 1,
            "-" to 1,
            "*" to 2,
            "/" to 2,
            "^" to 3
        )

        val output = mutableListOf<Token>()
        val stack = mutableListOf<Token>()

        tokens.forEach { token ->
            when (token) {
                is Token.Number, is Token.Variable -> output.add(token)

                is Token.Function -> stack.add(token)

                is Token.Operator -> {
                    while (
                        stack.isNotEmpty() &&
                        stack.last() is Token.Operator &&
                        (if (token.symbol == "^") {
                            precedence[(stack.last() as Token.Operator).symbol]!! > precedence[token.symbol]!!
                        } else {
                            precedence[(stack.last() as Token.Operator).symbol]!! >= precedence[token.symbol]!!
                        })
                    ) {
                        output.add(stack.removeAt(stack.lastIndex))
                    }
                    stack.add(token)
                }

                Token.LeftParen -> stack.add(token)

                Token.RightParen -> {
                    while (stack.isNotEmpty() && stack.last() != Token.LeftParen) {
                        output.add(stack.removeAt(stack.lastIndex))
                    }
                    if (stack.isEmpty() || stack.last() != Token.LeftParen) {
                        throw IllegalArgumentException("Mismatched parentheses")
                    }
                    stack.removeAt(stack.lastIndex) // Remove LeftParen
                    if (stack.isNotEmpty() && stack.last() is Token.Function) {
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

package dhn.intern.smart_ai_caculator_app.domain.graphing

interface GraphingEngine {
    /**
     * Compiles a raw mathematical expression string into a high-performance CompiledFunction.
     * Returns Result.failure if the expression has syntax errors or mismatched parentheses.
     */
    fun compile(expression: String): Result<CompiledFunction>

    /**
     * Samples the continuous segments of the function across the given ViewportBounds,
     * detecting and splitting along poles/asymptotes and computing special points (roots, extrema, y-intercept).
     */
    fun sample(
        function: CompiledFunction,
        viewport: ViewportBounds,
        screenPixelWidth: Int = 500
    ): SampledCurve

    /**
     * Computes intersection points between two compiled functions within the visible viewport.
     */
    fun findIntersections(
        f: CompiledFunction,
        g: CompiledFunction,
        viewport: ViewportBounds
    ): List<SpecialPoint>
}

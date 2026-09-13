package dhn.intern.smart_ai_caculator_app.domain.graphing

data class ViewportBounds(
    val minX: Double = -10.0,
    val maxX: Double = 10.0,
    val minY: Double = -10.0,
    val maxY: Double = 10.0
) {
    init {
        require(maxX > minX) { "maxX must be greater than minX" }
        require(maxY > minY) { "maxY must be greater than minY" }
    }

    val width: Double get() = maxX - minX
    val height: Double get() = maxY - minY
}

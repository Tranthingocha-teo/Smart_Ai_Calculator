package dhn.intern.smart_ai_caculator_app.domain.ocr.model

/**
 * Represents a 2D bounding box for a segmented character or mathematical symbol.
 */
data class BoundingBox(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
) {
    val width: Int get() = (right - left).coerceAtLeast(1)
    val height: Int get() = (bottom - top).coerceAtLeast(1)
    val centerX: Float get() = (left + right) / 2f
    val centerY: Float get() = (top + bottom) / 2f
    val area: Int get() = width * height
    val aspectRatio: Float get() = width.toFloat() / height.toFloat()

    fun contains(x: Int, y: Int): Boolean = x in left..right && y in top..bottom

    fun intersects(other: BoundingBox): Boolean =
        left <= other.right && right >= other.left &&
        top <= other.bottom && bottom >= other.top

    fun union(other: BoundingBox): BoundingBox =
        BoundingBox(
            left = minOf(left, other.left),
            top = minOf(top, other.top),
            right = maxOf(right, other.right),
            bottom = maxOf(bottom, other.bottom)
        )

    fun horizontalOverlapRatio(other: BoundingBox): Float {
        val overlapLeft = maxOf(left, other.left)
        val overlapRight = minOf(right, other.right)
        val overlapWidth = maxOf(0, overlapRight - overlapLeft)
        val minWidth = minOf(width, other.width)
        return if (minWidth > 0) overlapWidth.toFloat() / minWidth else 0f
    }
}

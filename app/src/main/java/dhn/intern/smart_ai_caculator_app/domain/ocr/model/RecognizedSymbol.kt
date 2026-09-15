package dhn.intern.smart_ai_caculator_app.domain.ocr.model

/**
 * Represents an individual recognized mathematical symbol with its classification label,
 * confidence score, and original spatial bounding box.
 */
data class RecognizedSymbol(
    val text: String,
    val confidence: Float,
    val box: BoundingBox
)

package dhn.intern.smart_ai_caculator_app.domain.ocr.model

/**
 * Result of complete On-Device Math OCR pipeline.
 *
 * @property rawExpression Direct concatenated sequence of recognized tokens.
 * @property formattedExpression Spatially parsed mathematical expression ready for calculation / graphing.
 * @property symbols List of individually classified symbols with bounding boxes.
 * @property averageConfidence Mean confidence across all recognized tokens [0.0f..1.0f].
 * @property hasVariables True if expression contains algebra/graphing variables like 'x' or 'y'.
 */
data class MathOcrResult(
    val rawExpression: String,
    val formattedExpression: String,
    val symbols: List<RecognizedSymbol>,
    val averageConfidence: Float,
    val hasVariables: Boolean = formattedExpression.contains("x", ignoreCase = true) ||
            formattedExpression.contains("y", ignoreCase = true)
)

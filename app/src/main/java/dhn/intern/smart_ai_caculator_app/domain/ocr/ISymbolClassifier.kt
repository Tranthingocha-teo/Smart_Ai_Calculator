package dhn.intern.smart_ai_caculator_app.domain.ocr

/**
 * Interface for mathematical symbol classifier.
 * Decouples model inference from specific runtime implementations (TFLite, Mock, etc.).
 */
interface ISymbolClassifier : AutoCloseable {
    val labels: List<String>
    fun classify(normalizedPixels: FloatArray): MathSymbolClassifier.Classification
}

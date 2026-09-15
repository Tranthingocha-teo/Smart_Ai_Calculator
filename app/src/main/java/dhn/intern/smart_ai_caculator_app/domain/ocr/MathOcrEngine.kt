package dhn.intern.smart_ai_caculator_app.domain.ocr

import android.graphics.Bitmap
import dhn.intern.smart_ai_caculator_app.domain.ocr.model.MathOcrResult
import dhn.intern.smart_ai_caculator_app.domain.ocr.model.RecognizedSymbol

/**
 * High-level On-Device Math OCR Engine.
 * Integrates image preprocessing (Otsu + CCL segmentation), TFLite symbol classification,
 * and spatial math parsing into a single unified pipeline.
 */
class MathOcrEngine(
    val preprocessor: ImagePreprocessor = ImagePreprocessor(),
    val classifier: ISymbolClassifier,
    val parser: SpatialMathParser = SpatialMathParser()
) {

    /**
     * Executes the full OCR recognition pipeline on an Android Bitmap.
     */
    fun recognize(bitmap: Bitmap): MathOcrResult {
        val binarized = preprocessor.binarize(bitmap)
        val boxes = preprocessor.segmentSymbols(binarized)

        if (boxes.isEmpty()) {
            return MathOcrResult(
                rawExpression = "",
                formattedExpression = "",
                symbols = emptyList(),
                averageConfidence = 0.0f
            )
        }

        val recognizedSymbols = mutableListOf<RecognizedSymbol>()
        var totalConfidence = 0.0f

        for (box in boxes) {
            val normalized = preprocessor.extractAndNormalizeSymbol(binarized, box, targetSize = 28)
            val classification = classifier.classify(normalized)

            val symbol = RecognizedSymbol(
                text = classification.label,
                confidence = classification.confidence,
                box = box
            )
            recognizedSymbols.add(symbol)
            totalConfidence += classification.confidence
        }

        val rawExpr = recognizedSymbols.joinToString("") { it.text }
        val formattedExpr = parser.parse(recognizedSymbols)
        val avgConfidence = if (recognizedSymbols.isNotEmpty()) totalConfidence / recognizedSymbols.size else 0.0f

        return MathOcrResult(
            rawExpression = rawExpr,
            formattedExpression = formattedExpr,
            symbols = recognizedSymbols,
            averageConfidence = avgConfidence
        )
    }

    /**
     * Executes the full OCR recognition pipeline on raw ARGB pixel buffer (JVM friendly).
     */
    fun recognize(pixels: IntArray, width: Int, height: Int): MathOcrResult {
        val binarized = preprocessor.binarize(pixels, width, height)
        val boxes = preprocessor.segmentSymbols(binarized)

        if (boxes.isEmpty()) {
            return MathOcrResult(
                rawExpression = "",
                formattedExpression = "",
                symbols = emptyList(),
                averageConfidence = 0.0f
            )
        }

        val recognizedSymbols = mutableListOf<RecognizedSymbol>()
        var totalConfidence = 0.0f

        for (box in boxes) {
            val normalized = preprocessor.extractAndNormalizeSymbol(binarized, box, targetSize = 28)
            val classification = classifier.classify(normalized)

            val symbol = RecognizedSymbol(
                text = classification.label,
                confidence = classification.confidence,
                box = box
            )
            recognizedSymbols.add(symbol)
            totalConfidence += classification.confidence
        }

        val rawExpr = recognizedSymbols.joinToString("") { it.text }
        val formattedExpr = parser.parse(recognizedSymbols)
        val avgConfidence = if (recognizedSymbols.isNotEmpty()) totalConfidence / recognizedSymbols.size else 0.0f

        return MathOcrResult(
            rawExpression = rawExpr,
            formattedExpression = formattedExpr,
            symbols = recognizedSymbols,
            averageConfidence = avgConfidence
        )
    }
}

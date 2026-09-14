package dhn.intern.smart_ai_caculator_app.domain.ocr

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * On-Device TensorFlow Lite interpreter for classifying 28x28 mathematical symbols
 * into 22 supported classes (0-9, +, -, *, /, =, ., (, ), x, y, ^, ,).
 */
class MathSymbolClassifier : ISymbolClassifier {

    private val interpreter: Interpreter
    override val labels: List<String>

    data class Classification(
        val label: String,
        val confidence: Float,
        val allProbabilities: FloatArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Classification
            return label == other.label &&
                    confidence == other.confidence &&
                    allProbabilities.contentEquals(other.allProbabilities)
        }

        override fun hashCode(): Int {
            var result = label.hashCode()
            result = 31 * result + confidence.hashCode()
            result = 31 * result + allProbabilities.contentHashCode()
            return result
        }
    }

    /**
     * Primary constructor loading model and labels from Android assets.
     */
    constructor(
        context: Context,
        modelAssetPath: String = "models/math_symbol_model.tflite",
        labelsAssetPath: String = "models/labels.txt",
        options: Interpreter.Options = Interpreter.Options().apply { setNumThreads(4) }
    ) {
        val modelBuffer = FileUtil.loadMappedFile(context, modelAssetPath)
        this.interpreter = Interpreter(modelBuffer, options)
        this.labels = loadLabelsFromAsset(context, labelsAssetPath)
    }

    /**
     * Secondary constructor for direct ByteBuffer and labels (unit testing / custom loading).
     */
    constructor(
        modelBuffer: ByteBuffer,
        labels: List<String>,
        options: Interpreter.Options = Interpreter.Options().apply { setNumThreads(2) }
    ) {
        this.interpreter = Interpreter(modelBuffer, options)
        this.labels = labels
    }

    /**
     * Secondary constructor wrapping an existing interpreter and labels list.
     */
    constructor(
        interpreter: Interpreter,
        labels: List<String>
    ) {
        this.interpreter = interpreter
        this.labels = labels
    }

    /**
     * Runs inference on a 28x28 normalized symbol FloatArray [0.0f..1.0f].
     */
    override fun classify(normalizedPixels: FloatArray): Classification {
        require(normalizedPixels.size == 28 * 28) {
            "Expected 784 normalized pixel values (28x28), received ${normalizedPixels.size}"
        }

        val inputBuffer = ByteBuffer.allocateDirect(4 * 28 * 28).order(ByteOrder.nativeOrder())
        for (pixel in normalizedPixels) {
            inputBuffer.putFloat(pixel)
        }
        inputBuffer.rewind()

        val outputArray = Array(1) { FloatArray(labels.size) }
        interpreter.run(inputBuffer, outputArray)

        val probabilities = outputArray[0]
        var maxIdx = 0
        var maxProb = probabilities[0]

        for (i in 1 until probabilities.size) {
            if (probabilities[i] > maxProb) {
                maxProb = probabilities[i]
                maxIdx = i
            }
        }

        val predictedLabel = if (maxIdx in labels.indices) labels[maxIdx] else "?"
        return Classification(
            label = predictedLabel,
            confidence = maxProb,
            allProbabilities = probabilities
        )
    }

    override fun close() {
        interpreter.close()
    }

    companion object {
        fun loadLabelsFromAsset(context: Context, assetPath: String): List<String> {
            val result = mutableListOf<String>()
            context.assets.open(assetPath).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).useLines { lines ->
                    lines.forEach { line ->
                        val trimmed = line.trim()
                        if (trimmed.isNotEmpty()) {
                            result.add(trimmed)
                        }
                    }
                }
            }
            return result
        }
    }
}

package dhn.intern.smart_ai_caculator_app.ocr

import dhn.intern.smart_ai_caculator_app.domain.ocr.MathSymbolClassifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class MathSymbolClassifierTest {

    @Test
    fun testModelAssetIntegrityAndHeader() {
        val modelFile = File("src/main/assets/models/math_symbol_model.tflite")
        val labelsFile = File("src/main/assets/models/labels.txt")

        assertTrue("math_symbol_model.tflite must exist in assets", modelFile.exists())
        assertTrue("labels.txt must exist in assets", labelsFile.exists())

        // Verify model size is under 2MB
        assertTrue("Model size (${modelFile.length()} bytes) must be < 2MB", modelFile.length() < 2 * 1024 * 1024)

        val labels = labelsFile.readLines().map { it.trim() }.filter { it.isNotEmpty() }
        assertEquals("Must support exactly 22 math classes", 22, labels.size)

        // Read model into direct ByteBuffer and verify FlatBuffers TFL3 magic bytes
        val fileInputStream = FileInputStream(modelFile)
        val fileChannel = fileInputStream.channel
        val byteBuffer: ByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size())

        try {
            val magic = ByteArray(4)
            byteBuffer.position(4)
            byteBuffer.get(magic)
            assertEquals("TFL3", String(magic, Charsets.US_ASCII))
        } finally {
            fileInputStream.close()
        }
    }

    @Test
    fun testTfliteModelInferenceIfJniAvailable() {
        val modelFile = File("src/main/assets/models/math_symbol_model.tflite")
        val labelsFile = File("src/main/assets/models/labels.txt")

        val labels = labelsFile.readLines().map { it.trim() }.filter { it.isNotEmpty() }
        val fileInputStream = FileInputStream(modelFile)
        val fileChannel = fileInputStream.channel
        val byteBuffer: ByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size())

        try {
            val classifier = MathSymbolClassifier(byteBuffer, labels)
            val dummyInput = FloatArray(28 * 28) { 0.1f }
            val result = classifier.classify(dummyInput)

            assertNotNull(result)
            assertTrue("Predicted label '${result.label}' must be in labels list", labels.contains(result.label))
            assertTrue("Confidence must be in [0, 1]", result.confidence in 0.0f..1.0f)
            assertEquals(22, result.allProbabilities.size)
            classifier.close()
        } catch (e: UnsatisfiedLinkError) {
            // Native libtensorflowlite_jni.so is compiled for Android runtime, expected on desktop JVM host
            println("Notice: TFLite native runtime not present in host JVM environment (Android runtime required). Header and buffer verified.")
        } finally {
            fileInputStream.close()
        }
    }
}

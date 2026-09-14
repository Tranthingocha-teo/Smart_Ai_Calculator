package dhn.intern.smart_ai_caculator_app.domain.ocr

import android.graphics.Bitmap
import dhn.intern.smart_ai_caculator_app.domain.ocr.model.BoundingBox
import java.util.ArrayDeque

/**
 * Pure Kotlin image preprocessor for handwritten mathematical expressions.
 * Provides Otsu thresholding, connected-component symbol segmentation, noise filtering,
 * and 28x28 grayscale normalization for TFLite inference.
 */
class ImagePreprocessor(
    private val minSymbolArea: Int = 8,
    private val minSymbolDimension: Int = 3
) {

    data class BinarizedImage(
        val pixels: BooleanArray, // true = foreground (ink/stroke), false = background
        val width: Int,
        val height: Int
    ) {
        fun get(x: Int, y: Int): Boolean =
            if (x in 0 until width && y in 0 until height) pixels[y * width + x] else false

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as BinarizedImage
            return width == other.width && height == other.height && pixels.contentEquals(other.pixels)
        }

        override fun hashCode(): Int {
            var result = width
            result = 31 * result + height
            result = 31 * result + pixels.contentHashCode()
            return result
        }
    }

    /**
     * Binarizes an Android Bitmap into foreground strokes using Otsu's global thresholding.
     */
    fun binarize(bitmap: Bitmap): BinarizedImage {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        return binarize(pixels, width, height)
    }

    /**
     * Binarizes ARGB_8888 pixel buffer into foreground strokes (JVM friendly).
     */
    fun binarize(pixels: IntArray, width: Int, height: Int): BinarizedImage {
        val gray = IntArray(width * height)
        val histogram = IntArray(256)

        for (i in pixels.indices) {
            val p = pixels[i]
            val r = (p shr 16) and 0xFF
            val g = (p shr 8) and 0xFF
            val b = p and 0xFF
            val gVal = (0.299f * r + 0.587f * g + 0.114f * b).toInt().coerceIn(0, 255)
            gray[i] = gVal
            histogram[gVal]++
        }

        val threshold = computeOtsuThreshold(histogram, width * height)

        // Check polarity: paper is usually light (background), ink is dark (foreground).
        // If average border luminance is high, ink is dark (< threshold).
        val borderPixels = mutableListOf<Int>()
        for (x in 0 until width) {
            borderPixels.add(gray[x])
            borderPixels.add(gray[(height - 1) * width + x])
        }
        for (y in 0 until height) {
            borderPixels.add(gray[y * width])
            borderPixels.add(gray[y * width + (width - 1)])
        }
        val avgBorder = borderPixels.average()
        val darkInk = avgBorder > threshold

        val binary = BooleanArray(width * height)
        for (i in gray.indices) {
            binary[i] = if (darkInk) gray[i] <= threshold else gray[i] >= threshold
        }

        return BinarizedImage(binary, width, height)
    }

    /**
     * Computes optimal binarization threshold via Otsu's method.
     */
    fun computeOtsuThreshold(histogram: IntArray, totalPixels: Int): Int {
        var sum = 0.0
        for (i in 0..255) {
            sum += i * histogram[i]
        }

        var sumB = 0.0
        var wB = 0
        var maxVariance = 0.0
        var threshold = 128

        for (t in 0..255) {
            wB += histogram[t]
            if (wB == 0) continue
            val wF = totalPixels - wB
            if (wF == 0) break

            sumB += t * histogram[t]
            val mB = sumB / wB
            val mF = (sum - sumB) / wF

            val betweenVariance = wB.toDouble() * wF.toDouble() * (mB - mF) * (mB - mF)
            if (betweenVariance > maxVariance) {
                maxVariance = betweenVariance
                threshold = t
            }
        }
        return threshold
    }

    /**
     * Segments isolated symbol bounding boxes using Connected Component Labeling (CCL)
     * and merges split symbols like '='.
     */
    fun segmentSymbols(image: BinarizedImage): List<BoundingBox> {
        val width = image.width
        val height = image.height
        val visited = BooleanArray(width * height)
        val rawBoxes = mutableListOf<BoundingBox>()

        // 4-connectivity directions
        val dx = intArrayOf(1, -1, 0, 0)
        val dy = intArrayOf(0, 0, 1, -1)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val idx = y * width + x
                if (image.pixels[idx] && !visited[idx]) {
                    // BFS to find connected component
                    var minX = x
                    var maxX = x
                    var minY = y
                    var maxY = y
                    var pixelCount = 0

                    val queue = ArrayDeque<Int>()
                    queue.add(idx)
                    visited[idx] = true

                    while (!queue.isEmpty()) {
                        val curr = queue.poll() ?: break
                        val cx = curr % width
                        val cy = curr / width
                        pixelCount++

                        if (cx < minX) minX = cx
                        if (cx > maxX) maxX = cx
                        if (cy < minY) minY = cy
                        if (cy > maxY) maxY = cy

                        for (d in 0..3) {
                            val nx = cx + dx[d]
                            val ny = cy + dy[d]
                            if (nx in 0 until width && ny in 0 until height) {
                                val nIdx = ny * width + nx
                                if (image.pixels[nIdx] && !visited[nIdx]) {
                                    visited[nIdx] = true
                                    queue.add(nIdx)
                                }
                            }
                        }
                    }

                    val box = BoundingBox(minX, minY, maxX + 1, maxY + 1)
                    // Filter noise dots and border artifacts
                    val isBorderArtifact = box.width >= (width * 0.98) && box.height >= (height * 0.98)
                    val isNoise = pixelCount < minSymbolArea || (box.width < minSymbolDimension && box.height < minSymbolDimension)

                    if (!isBorderArtifact && !isNoise) {
                        rawBoxes.add(box)
                    }
                }
            }
        }

        // Merge split components (e.g. '=' consists of two horizontal bars)
        val mergedBoxes = mergeEqualSignsAndDots(rawBoxes)

        // Sort reading order: primarily left to right
        return mergedBoxes.sortedBy { it.left }
    }

    /**
     * Merges vertically stacked parallel horizontal lines into a single '=' bounding box.
     */
    private fun mergeEqualSignsAndDots(boxes: List<BoundingBox>): List<BoundingBox> {
        if (boxes.size <= 1) return boxes

        val result = mutableListOf<BoundingBox>()
        val used = BooleanArray(boxes.size)

        for (i in boxes.indices) {
            if (used[i]) continue
            var current = boxes[i]

            for (j in i + 1 until boxes.size) {
                if (used[j]) continue
                val other = boxes[j]

                // Check if they form '=':
                // 1. High horizontal overlap (> 50%)
                // 2. Both are wider than high or roughly horizontal strokes
                // 3. Vertical gap is within reasonable symbol height
                val hOverlap = current.horizontalOverlapRatio(other)
                val verticalGap = if (current.top > other.bottom) {
                    current.top - other.bottom
                } else if (other.top > current.bottom) {
                    other.top - current.bottom
                } else 0

                val maxSymbolHeight = maxOf(current.height, other.height)
                val isVerticallyStacked = verticalGap <= maxSymbolHeight * 2.5f

                if (hOverlap > 0.5f && isVerticallyStacked) {
                    current = current.union(other)
                    used[j] = true
                }
            }
            result.add(current)
            used[i] = true
        }

        return result
    }

    /**
     * Crops symbol, centers with symmetric square padding, resizes to targetSize (default 28x28),
     * and normalizes to FloatArray [0.0f..1.0f] for model inference.
     */
    fun extractAndNormalizeSymbol(
        image: BinarizedImage,
        box: BoundingBox,
        targetSize: Int = 28
    ): FloatArray {
        val bw = box.width
        val bh = box.height

        // Determine square size with padding
        val maxDim = maxOf(bw, bh)
        val padding = (maxDim * 0.25f).toInt().coerceAtLeast(2)
        val squareSize = maxDim + 2 * padding

        val squareGrid = BooleanArray(squareSize * squareSize)
        val offsetX = padding + (maxDim - bw) / 2
        val offsetY = padding + (maxDim - bh) / 2

        for (y in 0 until bh) {
            for (x in 0 until bw) {
                val srcX = box.left + x
                val srcY = box.top + y
                if (image.get(srcX, srcY)) {
                    val dstX = offsetX + x
                    val dstY = offsetY + y
                    if (dstX in 0 until squareSize && dstY in 0 until squareSize) {
                        squareGrid[dstY * squareSize + dstX] = true
                    }
                }
            }
        }

        // Downscale squareGrid to targetSize x targetSize using area sampling
        val output = FloatArray(targetSize * targetSize)
        val scale = squareSize.toFloat() / targetSize.toFloat()

        for (ty in 0 until targetSize) {
            for (tx in 0 until targetSize) {
                val startX = (tx * scale).toInt().coerceIn(0, squareSize - 1)
                val endX = ((tx + 1) * scale).toInt().coerceIn(startX + 1, squareSize)
                val startY = (ty * scale).toInt().coerceIn(0, squareSize - 1)
                val endY = ((ty + 1) * scale).toInt().coerceIn(startY + 1, squareSize)

                var activeCount = 0
                val totalArea = (endX - startX) * (endY - startY)

                for (sy in startY until endY) {
                    for (sx in startX until endX) {
                        if (squareGrid[sy * squareSize + sx]) {
                            activeCount++
                        }
                    }
                }

                val intensity = if (totalArea > 0) activeCount.toFloat() / totalArea.toFloat() else 0f
                output[ty * targetSize + tx] = intensity.coerceIn(0.0f, 1.0f)
            }
        }

        return output
    }
}

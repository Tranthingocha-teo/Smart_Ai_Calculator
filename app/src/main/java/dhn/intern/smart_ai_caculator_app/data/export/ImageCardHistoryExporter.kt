package dhn.intern.smart_ai_caculator_app.data.export

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageCardHistoryExporter {

    private val cardWidth = 800
    private val cardHeight = 480
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy • HH:mm", Locale.getDefault())

    fun renderCardBitmap(context: Context, item: CalculatorHistoryEntity): Bitmap {
        val bitmap = Bitmap.createBitmap(cardWidth, cardHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background Gradient
        val bgPaint = Paint().apply {
            shader = LinearGradient(
                0f, 0f, cardWidth.toFloat(), cardHeight.toFloat(),
                Color.rgb(24, 24, 37), // #181825
                Color.rgb(17, 17, 27), // #11111B
                Shader.TileMode.CLAMP
            )
            isAntiAlias = true
        }
        val cardRect = RectF(0f, 0f, cardWidth.toFloat(), cardHeight.toFloat())
        canvas.drawRoundRect(cardRect, 32f, 32f, bgPaint)

        // Accent Top Border Glow
        val accentBorderPaint = Paint().apply {
            shader = LinearGradient(
                40f, 0f, cardWidth - 40f, 0f,
                Color.rgb(56, 189, 248), // Sky Blue
                Color.rgb(168, 85, 247), // Purple
                Shader.TileMode.CLAMP
            )
            strokeWidth = 6f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
        val topGlowRect = RectF(32f, 8f, cardWidth - 32f, cardHeight - 8f)
        canvas.drawRoundRect(topGlowRect, 28f, 28f, accentBorderPaint)

        // Inner Content Container
        val padding = 56f
        var currentY = 72f

        // Brand Title
        val brandPaint = Paint().apply {
            color = Color.rgb(248, 250, 252) // Slate 50
            textSize = 24f
            isFakeBoldText = true
            isAntiAlias = true
        }
        canvas.drawText(context.getString(R.string.export_card_brand), padding, currentY, brandPaint)

        // Source Tag Chip
        val chipPaint = Paint().apply {
            color = Color.rgb(49, 50, 68) // #313244
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val chipTextPaint = Paint().apply {
            color = Color.rgb(198, 208, 245)
            textSize = 14f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val sourceLabel = item.source
        val chipTextWidth = chipTextPaint.measureText(sourceLabel)
        val chipRight = cardWidth - padding
        val chipLeft = chipRight - chipTextWidth - 28f
        val chipRect = RectF(chipLeft, currentY - 24f, chipRight, currentY + 8f)
        canvas.drawRoundRect(chipRect, 16f, 16f, chipPaint)
        canvas.drawText(sourceLabel, chipLeft + 14f, currentY - 2f, chipTextPaint)

        currentY += 28f

        // Divider
        val dividerPaint = Paint().apply {
            color = Color.rgb(69, 71, 90) // #45475A
            strokeWidth = 1.2f
        }
        canvas.drawLine(padding, currentY, cardWidth - padding, currentY, dividerPaint)

        currentY += 60f

        // Expression Label & Content
        val labelPaint = Paint().apply {
            color = Color.rgb(148, 163, 184) // Slate 400
            textSize = 15f
            isAntiAlias = true
        }
        canvas.drawText(context.getString(R.string.export_col_expression).uppercase(Locale.getDefault()), padding, currentY, labelPaint)
        currentY += 34f

        val expressionPaint = Paint().apply {
            color = Color.rgb(226, 232, 240) // Slate 200
            textSize = 28f
            isAntiAlias = true
        }
        val displayExpr = if (item.expression.length > 38) {
            item.expression.take(35) + "..."
        } else {
            item.expression
        }
        canvas.drawText(displayExpr, padding, currentY, expressionPaint)

        currentY += 56f

        // Result Label & Content
        canvas.drawText(context.getString(R.string.export_col_result).uppercase(Locale.getDefault()), padding, currentY, labelPaint)
        currentY += 46f

        val resultPaint = Paint().apply {
            color = Color.rgb(74, 222, 128) // Emerald / Mint Green
            textSize = 42f
            isFakeBoldText = true
            isAntiAlias = true
        }
        val displayResult = if (item.result.length > 24) {
            item.result.take(21) + "..."
        } else {
            item.result
        }
        canvas.drawText("= $displayResult", padding, currentY, resultPaint)

        // Footer Divider & Text
        val footerY = cardHeight - 44f
        canvas.drawLine(padding, footerY - 24f, cardWidth - padding, footerY - 24f, dividerPaint)

        val footerPaint = Paint().apply {
            color = Color.rgb(148, 163, 184)
            textSize = 13f
            isAntiAlias = true
        }
        val dateStr = dateFormat.format(Date(item.timestamp))
        canvas.drawText(dateStr, padding, footerY, footerPaint)

        val brandFooter = context.getString(R.string.export_card_footer)
        val footerWidth = footerPaint.measureText(brandFooter)
        canvas.drawText(brandFooter, cardWidth - padding - footerWidth, footerY, footerPaint)

        return bitmap
    }

    fun exportToPng(
        context: Context,
        item: CalculatorHistoryEntity,
        destFile: File
    ): Result<File> = runCatching {
        val bitmap = renderCardBitmap(context, item)
        FileOutputStream(destFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        destFile
    }
}

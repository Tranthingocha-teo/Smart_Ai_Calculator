package dhn.intern.smart_ai_caculator_app.data.export

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.min

class PdfHistoryExporter {

    private val pageWidth = 595 // A4 standard width in points (72 dpi)
    private val pageHeight = 842 // A4 standard height in points (72 dpi)
    private val rowsPerPage = 22
    private val maxExportRecords = 500

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun exportToPdf(
        context: Context,
        items: List<CalculatorHistoryEntity>,
        destFile: File,
        sourceFilter: String? = null
    ): Result<File> = runCatching {
        val limitedItems = items.take(maxExportRecords)
        val document = PdfDocument()

        val totalPages = if (limitedItems.isEmpty()) 1 else ceil(limitedItems.size.toDouble() / rowsPerPage).toInt()

        val titlePaint = Paint().apply {
            color = Color.rgb(30, 41, 59)
            textSize = 18f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val subtitlePaint = Paint().apply {
            color = Color.rgb(100, 116, 139)
            textSize = 10f
            isAntiAlias = true
        }

        val headerBgPaint = Paint().apply {
            color = Color.rgb(241, 245, 249)
            style = Paint.Style.FILL
        }

        val tableHeaderPaint = Paint().apply {
            color = Color.rgb(30, 41, 59)
            textSize = 10f
            isFakeBoldText = true
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = Color.rgb(51, 65, 85)
            textSize = 9f
            isAntiAlias = true
        }

        val linePaint = Paint().apply {
            color = Color.rgb(226, 232, 240)
            strokeWidth = 0.8f
        }

        val colX = floatArrayOf(36f, 72f, 185f, 265f, 440f)
        val colWidths = floatArrayOf(36f, 113f, 80f, 175f, 118f)

        val colTitles = arrayOf(
            context.getString(R.string.export_col_index),
            context.getString(R.string.export_col_time),
            context.getString(R.string.export_col_source),
            context.getString(R.string.export_col_expression),
            context.getString(R.string.export_col_result)
        )

        for (pageIndex in 0 until totalPages) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex + 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas

            // Header Banner
            var yPos = 50f
            canvas.drawText(context.getString(R.string.export_pdf_title), 36f, yPos, titlePaint)
            yPos += 16f

            val generatedDateStr = dateFormat.format(Date())
            val filterSubtitle = if (sourceFilter != null) {
                "${context.getString(R.string.export_col_source)}: $sourceFilter | $generatedDateStr"
            } else {
                "${context.getString(R.string.export_scope_all)} | $generatedDateStr"
            }
            canvas.drawText(filterSubtitle, 36f, yPos, subtitlePaint)
            yPos += 24f

            // Table Header Bar
            val tableHeaderHeight = 24f
            canvas.drawRect(36f, yPos, pageWidth - 36f, yPos + tableHeaderHeight, headerBgPaint)
            canvas.drawLine(36f, yPos, pageWidth - 36f, yPos, linePaint)
            canvas.drawLine(36f, yPos + tableHeaderHeight, pageWidth - 36f, yPos + tableHeaderHeight, linePaint)

            for (i in colTitles.indices) {
                canvas.drawText(colTitles[i], colX[i] + 4f, yPos + 16f, tableHeaderPaint)
            }
            yPos += tableHeaderHeight

            // Table Rows
            val startIndex = pageIndex * rowsPerPage
            val endIndex = min(startIndex + rowsPerPage, limitedItems.size)
            val rowHeight = 24f

            for (i in startIndex until endIndex) {
                val item = limitedItems[i]
                val itemY = yPos + (i - startIndex) * rowHeight

                // Row bottom separator
                canvas.drawLine(36f, itemY + rowHeight, pageWidth - 36f, itemY + rowHeight, linePaint)

                // STT
                canvas.drawText((i + 1).toString(), colX[0] + 4f, itemY + 16f, textPaint)

                // Time
                val timeStr = dateFormat.format(Date(item.timestamp))
                canvas.drawText(timeStr, colX[1] + 4f, itemY + 16f, textPaint)

                // Source
                canvas.drawText(truncateText(item.source, 14), colX[2] + 4f, itemY + 16f, textPaint)

                // Expression
                canvas.drawText(truncateText(item.expression, 30), colX[3] + 4f, itemY + 16f, textPaint)

                // Result
                canvas.drawText(truncateText(item.result, 18), colX[4] + 4f, itemY + 16f, textPaint)
            }

            // Footer
            val footerY = pageHeight - 36f
            canvas.drawLine(36f, footerY - 14f, pageWidth - 36f, footerY - 14f, linePaint)
            canvas.drawText(
                context.getString(R.string.export_card_brand),
                36f,
                footerY,
                subtitlePaint
            )

            val pageStr = String.format(
                Locale.getDefault(),
                context.getString(R.string.export_pdf_footer_page),
                pageIndex + 1,
                totalPages
            )
            val pageStrWidth = subtitlePaint.measureText(pageStr)
            canvas.drawText(pageStr, pageWidth - 36f - pageStrWidth, footerY, subtitlePaint)

            document.finishPage(page)
        }

        FileOutputStream(destFile).use { out ->
            document.writeTo(out)
        }
        document.close()

        destFile
    }

    fun exportSingleItemPdf(
        context: Context,
        item: CalculatorHistoryEntity,
        destFile: File
    ): Result<File> = exportToPdf(
        context = context,
        items = listOf(item),
        destFile = destFile,
        sourceFilter = item.source
    )

    private fun truncateText(text: String, maxChars: Int): String {
        return if (text.length > maxChars) {
            text.take(maxChars - 3) + "..."
        } else {
            text
        }
    }
}

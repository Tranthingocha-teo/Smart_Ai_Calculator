package dhn.intern.smart_ai_caculator_app.domain.export

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.data.export.CsvHistoryExporter
import dhn.intern.smart_ai_caculator_app.data.export.ImageCardHistoryExporter
import dhn.intern.smart_ai_caculator_app.data.export.PdfHistoryExporter
import dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity
import dhn.intern.smart_ai_caculator_app.domain.export.model.ExportConfig
import dhn.intern.smart_ai_caculator_app.domain.export.model.ExportFormat
import dhn.intern.smart_ai_caculator_app.domain.export.model.ExportResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryExportManager(
    private val csvExporter: CsvHistoryExporter,
    private val pdfExporter: PdfHistoryExporter,
    private val imageCardExporter: ImageCardHistoryExporter
) {

    private val fileTimestampFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    suspend fun exportBatch(
        context: Context,
        items: List<CalculatorHistoryEntity>,
        config: ExportConfig,
        currentSource: String? = null
    ): Result<ExportResult> = withContext(Dispatchers.IO) {
        runCatching {
            if (items.isEmpty()) {
                throw IllegalStateException(context.getString(R.string.export_empty_history))
            }

            val timestamp = fileTimestampFormat.format(Date())
            val cacheDir = File(context.cacheDir, "exports").apply { mkdirs() }

            when (config.format) {
                ExportFormat.CSV -> {
                    val fileName = "smart_calc_history_$timestamp.csv"
                    val file = File(cacheDir, fileName)
                    val csvString = csvExporter.exportToCsv(
                        items = items.take(config.maxRecords),
                        headers = listOf(
                            context.getString(R.string.export_col_index),
                            context.getString(R.string.export_col_time),
                            context.getString(R.string.export_col_source),
                            context.getString(R.string.export_col_expression),
                            context.getString(R.string.export_col_result)
                        )
                    )
                    FileOutputStream(file).use { out ->
                        out.write(csvString.toByteArray(Charsets.UTF_8))
                    }
                    val uri = getFileUri(context, file)
                    ExportResult(
                        file = file,
                        uri = uri,
                        mimeType = "text/csv",
                        recordCount = items.take(config.maxRecords).size,
                        rawText = csvString
                    )
                }

                ExportFormat.PDF -> {
                    val fileName = "smart_calc_history_$timestamp.pdf"
                    val file = File(cacheDir, fileName)
                    pdfExporter.exportToPdf(
                        context = context,
                        items = items.take(config.maxRecords),
                        destFile = file,
                        sourceFilter = currentSource
                    ).getOrThrow()
                    val uri = getFileUri(context, file)
                    ExportResult(
                        file = file,
                        uri = uri,
                        mimeType = "application/pdf",
                        recordCount = items.take(config.maxRecords).size
                    )
                }

                else -> {
                    throw UnsupportedOperationException("Batch export format ${config.format} is not supported.")
                }
            }
        }
    }

    suspend fun exportSingle(
        context: Context,
        item: CalculatorHistoryEntity,
        format: ExportFormat
    ): Result<ExportResult> = withContext(Dispatchers.IO) {
        runCatching {
            val timestamp = fileTimestampFormat.format(Date())
            val cacheDir = File(context.cacheDir, "exports").apply { mkdirs() }

            when (format) {
                ExportFormat.IMAGE_PNG -> {
                    val fileName = "smart_calc_item_$timestamp.png"
                    val file = File(cacheDir, fileName)
                    imageCardExporter.exportToPng(context, item, file).getOrThrow()
                    val uri = getFileUri(context, file)
                    ExportResult(
                        file = file,
                        uri = uri,
                        mimeType = "image/png",
                        recordCount = 1
                    )
                }

                ExportFormat.PDF -> {
                    val fileName = "smart_calc_item_$timestamp.pdf"
                    val file = File(cacheDir, fileName)
                    pdfExporter.exportSingleItemPdf(context, item, file).getOrThrow()
                    val uri = getFileUri(context, file)
                    ExportResult(
                        file = file,
                        uri = uri,
                        mimeType = "application/pdf",
                        recordCount = 1
                    )
                }

                ExportFormat.PLAIN_TEXT -> {
                    val plainText = "${item.expression} = ${item.result}"
                    ExportResult(
                        file = null,
                        uri = null,
                        mimeType = "text/plain",
                        recordCount = 1,
                        rawText = plainText
                    )
                }

                ExportFormat.CSV -> {
                    val fileName = "smart_calc_item_$timestamp.csv"
                    val file = File(cacheDir, fileName)
                    val csvString = csvExporter.exportSingleItem(item)
                    FileOutputStream(file).use { out ->
                        out.write(csvString.toByteArray(Charsets.UTF_8))
                    }
                    val uri = getFileUri(context, file)
                    ExportResult(
                        file = file,
                        uri = uri,
                        mimeType = "text/csv",
                        recordCount = 1,
                        rawText = csvString
                    )
                }
            }
        }
    }

    fun shareExportResult(context: Context, exportResult: ExportResult) {
        val shareIntent = if (exportResult.file != null && exportResult.uri != null) {
            Intent(Intent.ACTION_SEND).apply {
                type = exportResult.mimeType
                putExtra(Intent.EXTRA_STREAM, exportResult.uri)
                putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.export_history_title))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } else if (exportResult.rawText != null) {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, exportResult.rawText)
                putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.export_history_title))
            }
        } else {
            return
        }

        val chooser = Intent.createChooser(shareIntent, context.getString(R.string.export_action_share)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    suspend fun saveToDownloads(context: Context, exportResult: ExportResult): Result<Uri> = withContext(Dispatchers.IO) {
        runCatching {
            val sourceFile = exportResult.file
                ?: throw IllegalArgumentException("No file present in export result to save.")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, sourceFile.name)
                    put(MediaStore.MediaColumns.MIME_TYPE, exportResult.mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw IOException("Failed to create MediaStore entry in Downloads")

                resolver.openOutputStream(uri)?.use { out ->
                    sourceFile.inputStream().use { input ->
                        input.copyTo(out)
                    }
                } ?: throw IOException("Failed to open output stream to MediaStore URI")

                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
                uri
            } else {
                @Suppress("DEPRECATION")
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }
                val destFile = File(downloadsDir, sourceFile.name)
                sourceFile.copyTo(destFile, overwrite = true)
                getFileUri(context, destFile)
            }
        }
    }

    fun openFile(context: Context, uri: Uri, mimeType: String): Result<Unit> = runCatching {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    private fun getFileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }
}

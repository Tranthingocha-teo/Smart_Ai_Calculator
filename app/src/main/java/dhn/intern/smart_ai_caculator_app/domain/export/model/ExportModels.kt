package dhn.intern.smart_ai_caculator_app.domain.export.model

import android.net.Uri
import java.io.File

enum class ExportFormat {
    PDF,
    CSV,
    IMAGE_PNG,
    PLAIN_TEXT
}

enum class ExportScope {
    CURRENT_SOURCE,
    ALL_SOURCES
}

data class ExportConfig(
    val format: ExportFormat = ExportFormat.PDF,
    val scope: ExportScope = ExportScope.CURRENT_SOURCE,
    val maxRecords: Int = 500
)

data class ExportResult(
    val file: File?,
    val uri: Uri?,
    val mimeType: String,
    val recordCount: Int,
    val rawText: String? = null
)

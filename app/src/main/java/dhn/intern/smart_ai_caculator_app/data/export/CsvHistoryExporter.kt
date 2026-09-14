package dhn.intern.smart_ai_caculator_app.data.export

import dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CsvHistoryExporter {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun exportToCsv(
        items: List<CalculatorHistoryEntity>,
        headers: List<String> = listOf("STT", "Thời gian", "Nguồn", "Biểu thức", "Kết quả")
    ): String {
        val sb = StringBuilder()
        // UTF-8 BOM for Excel compatibility
        sb.append('\uFEFF')

        // Header
        sb.append(headers.joinToString(separator = ",", postfix = "\r\n") { escapeCsvField(it) })

        // Rows
        items.forEachIndexed { index, item ->
            val timeStr = dateFormat.format(Date(item.timestamp))
            val row = listOf(
                (index + 1).toString(),
                timeStr,
                item.source,
                item.expression,
                item.result
            )
            sb.append(row.joinToString(separator = ",", postfix = "\r\n") { escapeCsvField(it) })
        }

        return sb.toString()
    }

    fun exportSingleItem(item: CalculatorHistoryEntity): String {
        return exportToCsv(listOf(item))
    }

    internal fun escapeCsvField(field: String): String {
        val containsSpecial = field.contains(',') ||
                field.contains('"') ||
                field.contains('\n') ||
                field.contains('\r')
        return if (containsSpecial) {
            "\"" + field.replace("\"", "\"\"") + "\""
        } else {
            field
        }
    }
}

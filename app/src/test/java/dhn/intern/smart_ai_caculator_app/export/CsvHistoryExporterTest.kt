package dhn.intern.smart_ai_caculator_app.export

import dhn.intern.smart_ai_caculator_app.data.export.CsvHistoryExporter
import dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CsvHistoryExporterTest {

    private lateinit var exporter: CsvHistoryExporter

    @Before
    fun setUp() {
        exporter = CsvHistoryExporter()
    }

    @Test
    fun `exportToCsv begins with UTF-8 BOM character`() {
        val result = exporter.exportToCsv(emptyList())
        assertTrue("Output must start with UTF-8 BOM \\uFEFF", result.startsWith("\uFEFF"))
    }

    @Test
    fun `exportToCsv produces correct CSV headers and data rows`() {
        val items = listOf(
            CalculatorHistoryEntity(
                id = 1L,
                expression = "25 * 4",
                result = "100",
                timestamp = 1700000000000L,
                source = "CALCULATOR"
            ),
            CalculatorHistoryEntity(
                id = 2L,
                expression = "sin(90)",
                result = "1",
                timestamp = 1700000010000L,
                source = "GRAPHING_CALCULATOR"
            )
        )

        val csv = exporter.exportToCsv(items)
        val lines = csv.removePrefix("\uFEFF").split("\r\n").filter { it.isNotEmpty() }

        assertEquals(3, lines.size) // 1 header + 2 data rows
        assertTrue(lines[0].contains("STT"))
        assertTrue(lines[0].contains("Biểu thức"))
        assertTrue(lines[0].contains("Kết quả"))

        assertTrue(lines[1].startsWith("1,"))
        assertTrue(lines[1].contains("25 * 4,100"))
        assertTrue(lines[2].startsWith("2,"))
        assertTrue(lines[2].contains("sin(90),1"))
    }

    @Test
    fun `escapeCsvField properly handles commas and double quotes per RFC 4180`() {
        val fieldWithComma = exporter.escapeCsvField("1,234.56")
        assertEquals("\"1,234.56\"", fieldWithComma)

        val fieldWithQuotes = exporter.escapeCsvField("Result: \"Pass\"")
        assertEquals("\"Result: \"\"Pass\"\"\"", fieldWithQuotes)

        val fieldWithNewline = exporter.escapeCsvField("Line1\nLine2")
        assertEquals("\"Line1\nLine2\"", fieldWithNewline)

        val normalField = exporter.escapeCsvField("normal_value")
        assertEquals("normal_value", normalField)
    }

    @Test
    fun `exportSingleItem produces exactly one data row`() {
        val item = CalculatorHistoryEntity(
            id = 5L,
            expression = "sqrt(144)",
            result = "12",
            timestamp = 1700000000000L,
            source = "CALCULATOR"
        )

        val csv = exporter.exportSingleItem(item)
        val lines = csv.removePrefix("\uFEFF").split("\r\n").filter { it.isNotEmpty() }

        assertEquals(2, lines.size) // 1 header + 1 data row
        assertTrue(lines[1].contains("sqrt(144),12"))
    }
}

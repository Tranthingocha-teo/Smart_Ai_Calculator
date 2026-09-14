package dhn.intern.smart_ai_caculator_app.export

import dhn.intern.smart_ai_caculator_app.domain.export.model.ExportConfig
import dhn.intern.smart_ai_caculator_app.domain.export.model.ExportFormat
import dhn.intern.smart_ai_caculator_app.domain.export.model.ExportScope
import org.junit.Assert.assertEquals
import org.junit.Test

class ExportConfigTest {

    @Test
    fun `default ExportConfig has PDF format, current scope and 500 max records`() {
        val config = ExportConfig()
        assertEquals(ExportFormat.PDF, config.format)
        assertEquals(ExportScope.CURRENT_SOURCE, config.scope)
        assertEquals(500, config.maxRecords)
    }

    @Test
    fun `custom ExportConfig preserves specified values`() {
        val config = ExportConfig(
            format = ExportFormat.CSV,
            scope = ExportScope.ALL_SOURCES,
            maxRecords = 100
        )
        assertEquals(ExportFormat.CSV, config.format)
        assertEquals(ExportScope.ALL_SOURCES, config.scope)
        assertEquals(100, config.maxRecords)
    }
}

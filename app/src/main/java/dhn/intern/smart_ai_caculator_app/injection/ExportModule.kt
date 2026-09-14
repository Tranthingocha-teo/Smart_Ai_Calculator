package dhn.intern.smart_ai_caculator_app.injection

import dhn.intern.smart_ai_caculator_app.data.export.CsvHistoryExporter
import dhn.intern.smart_ai_caculator_app.data.export.ImageCardHistoryExporter
import dhn.intern.smart_ai_caculator_app.data.export.PdfHistoryExporter
import dhn.intern.smart_ai_caculator_app.domain.export.HistoryExportManager
import org.koin.dsl.module

val exportModule = module {
    single { CsvHistoryExporter() }
    single { PdfHistoryExporter() }
    single { ImageCardHistoryExporter() }
    single { HistoryExportManager(get(), get(), get()) }
}

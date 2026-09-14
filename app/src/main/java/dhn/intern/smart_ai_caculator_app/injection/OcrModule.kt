package dhn.intern.smart_ai_caculator_app.injection

import dhn.intern.smart_ai_caculator_app.domain.ocr.ISymbolClassifier
import dhn.intern.smart_ai_caculator_app.domain.ocr.ImagePreprocessor
import dhn.intern.smart_ai_caculator_app.domain.ocr.MathOcrEngine
import dhn.intern.smart_ai_caculator_app.domain.ocr.MathSymbolClassifier
import dhn.intern.smart_ai_caculator_app.domain.ocr.SpatialMathParser
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val ocrModule = module {
    single { ImagePreprocessor() }
    single { SpatialMathParser() }
    single<ISymbolClassifier> { MathSymbolClassifier(androidContext()) }
    single { MathOcrEngine(get(), get(), get()) }
}

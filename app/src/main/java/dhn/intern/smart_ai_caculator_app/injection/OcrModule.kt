package dhn.intern.smart_ai_caculator_app.injection

import dhn.intern.smart_ai_caculator_app.data.preferences.ai.AiPreferences
import dhn.intern.smart_ai_caculator_app.domain.ai.GeminiMathService
import dhn.intern.smart_ai_caculator_app.domain.ocr.ISymbolClassifier
import dhn.intern.smart_ai_caculator_app.domain.ocr.ImagePreprocessor
import dhn.intern.smart_ai_caculator_app.domain.ocr.MathOcrEngine
import dhn.intern.smart_ai_caculator_app.domain.ocr.MathSymbolClassifier
import dhn.intern.smart_ai_caculator_app.domain.ocr.MlKitMathOcrEngine
import dhn.intern.smart_ai_caculator_app.domain.ocr.SpatialMathParser
import dhn.intern.smart_ai_caculator_app.domain.solver.StepByStepMathSolver
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.AiChatViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ocrModule = module {
    single { ImagePreprocessor() }
    single { SpatialMathParser() }
    single<ISymbolClassifier> { MathSymbolClassifier(androidContext()) }
    single { MathOcrEngine(get(), get(), get()) }
    single { MlKitMathOcrEngine(get()) }
    single { StepByStepMathSolver(get()) }
    single { AiPreferences(androidContext()) }
    single { GeminiMathService(customKeyProvider = { get<AiPreferences>().cachedKey }) }
    viewModel { AiChatViewModel(get(), get(), get()) }
}

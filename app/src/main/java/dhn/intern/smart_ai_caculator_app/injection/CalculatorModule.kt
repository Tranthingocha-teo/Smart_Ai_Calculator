package dhn.intern.smart_ai_caculator_app.injection
import dhn.intern.smart_ai_caculator_app.domain.calculator.CalculatorEngine
import dhn.intern.smart_ai_caculator_app.domain.graphing.DefaultGraphingEngine
import dhn.intern.smart_ai_caculator_app.domain.graphing.GraphingEngine
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.CalculatorViewModel
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.GraphingCalculatorViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val calculatorModule = module {

    // Engine (business logic)
    single { CalculatorEngine() }
    single<GraphingEngine> { DefaultGraphingEngine() }

    // ViewModel
    viewModel {
        CalculatorViewModel(
            engine = get(),
            historyRepo = get()
        )
    }

    viewModel {
        GraphingCalculatorViewModel(
            graphingEngine = get()
        )
    }
}
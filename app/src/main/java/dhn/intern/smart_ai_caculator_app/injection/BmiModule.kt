package dhn.intern.smart_ai_caculator_app.injection

import dhn.intern.smart_ai_caculator_app.domain.calculator.CalculatorEngine
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.BmiViewModel
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.CalculatorViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val bmiModule = module {

    // ViewModel
    viewModel {
        BmiViewModel(
        )
    }
}
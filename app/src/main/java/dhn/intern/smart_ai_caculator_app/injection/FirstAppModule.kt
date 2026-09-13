package dhn.intern.smart_ai_caculator_app.injection

import dhn.intern.smart_ai_caculator_app.data.repository.AppRepository
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.AppViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val FirstAppModule = module {
    single { AppRepository(get()) }
    viewModel { AppViewModel(get()) }
}

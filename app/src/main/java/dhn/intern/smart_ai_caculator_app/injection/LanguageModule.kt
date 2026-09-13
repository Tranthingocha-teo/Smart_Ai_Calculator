package dhn.intern.smart_ai_caculator_app.injection

import androidx.datastore.core.DataStore
import dhn.intern.smart_ai_caculator_app.data.preferences.language.LocaleManager
import dhn.intern.smart_ai_caculator_app.data.repository.LanguageRepository
import dhn.intern.smart_ai_caculator_app.util.dataStore
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.LanguageViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import androidx.datastore.preferences.core.Preferences


val languageModule = module {
    single<DataStore<Preferences>> {
        androidContext().dataStore
    }

    // Locale
    single { LocaleManager() }


    // Repository
    single { LanguageRepository(get()) }

    // ViewModel
    viewModel { LanguageViewModel(get(), get()) }
}

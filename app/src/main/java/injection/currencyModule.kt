package dhn.intern.smart_ai_caculator_app.injection

import dhn.intern.smart_ai_caculator_app.data.remote.CurrencyApiService
import dhn.intern.smart_ai_caculator_app.data.repository.CurrencyRepository
import dhn.intern.smart_ai_caculator_app.ui.currency.CurrencyCalculatorViewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val currencyModule = module {

    single<CurrencyApiService> {
        Retrofit.Builder()
            .baseUrl("https://open.er-api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CurrencyApiService::class.java)
    }

    single {
        CurrencyRepository(
            apiService = get(),
            currencyRateDao = get()
        )
    }

    // Dùng factory để tránh lỗi xung đột thư viện Koin ViewModel
    factory { CurrencyCalculatorViewModel(get()) }
}

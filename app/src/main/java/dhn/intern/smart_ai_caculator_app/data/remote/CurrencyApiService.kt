package dhn.intern.smart_ai_caculator_app.data.remote

import dhn.intern.smart_ai_caculator_app.data.remote.dto.CurrencyResponseDto
import retrofit2.http.GET

interface CurrencyApiService {
    @GET("v6/latest/USD")
    suspend fun getLatestRates(): CurrencyResponseDto
}

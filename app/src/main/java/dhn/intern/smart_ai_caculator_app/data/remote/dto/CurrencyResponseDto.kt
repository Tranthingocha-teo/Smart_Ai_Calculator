package dhn.intern.smart_ai_caculator_app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CurrencyResponseDto(
    @SerializedName("result")
    val result: String?,
    @SerializedName("time_last_update_unix")
    val timeLastUpdateUnix: Long?,
    @SerializedName("rates")
    val rates: Map<String, Double>?
)

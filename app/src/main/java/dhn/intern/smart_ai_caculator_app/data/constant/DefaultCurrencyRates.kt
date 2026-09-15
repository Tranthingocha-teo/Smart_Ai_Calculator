package dhn.intern.smart_ai_caculator_app.data.constant

object DefaultCurrencyRates {
    // Tỷ giá quy đổi so với 1 USD (mặc định đóng gói sẵn)
    val STATIC_RATES: Map<String, Double> = mapOf(
        "USD" to 1.0,
        "VND" to 25450.0,
        "EUR" to 0.92,
        "JPY" to 155.0,
        "GBP" to 0.79,
        "AUD" to 1.52,
        "CAD" to 1.37,
        "CHF" to 0.91,
        "CNY" to 7.23
    )
}

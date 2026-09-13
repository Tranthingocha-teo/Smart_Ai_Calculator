package dhn.intern.smart_ai_caculator_app.data.source.currencies

/**
 * Bundled offline fallback exchange rates (base USD = 1.0).
 * Used when network is unreachable and cache is empty.
 * Handled under Ticket #31 by @thuyydung.
 */
object DefaultCurrencyRates {
    val FALLBACK_RATES: Map<String, Double> = mapOf(
        "USD" to 1.0,
        "VND" to 25450.0,
        "EUR" to 0.92,
        "GBP" to 0.78,
        "JPY" to 155.0,
        "CNY" to 7.24,
        "KRW" to 1380.0,
        "AUD" to 1.52,
        "CAD" to 1.37,
        "SGD" to 1.35,
        "CHF" to 0.90,
        "THB" to 36.8
    )
}

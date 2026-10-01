package dhn.intern.smart_ai_caculator_app.data.repository

import dhn.intern.smart_ai_caculator_app.data.local.dao.CurrencyRateDao
import dhn.intern.smart_ai_caculator_app.data.local.entity.CurrencyRateEntity
import dhn.intern.smart_ai_caculator_app.data.source.currencies.DefaultCurrencyRates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class CurrencyRepository(
    private val currencyRateDao: CurrencyRateDao
) {
    private val apiUrl = "https://open.er-api.com/v6/latest/USD"

    /**
     * Retrieves current exchange rates offline-first.
     * Priority:
     * 1. Room DB cached rates (if non-empty)
     * 2. Default fallback static rates
     */
    suspend fun getRates(): Map<String, Double> {
        val cachedEntities = try {
            currencyRateDao.getAllRates()
        } catch (e: Exception) {
            emptyList()
        }

        if (cachedEntities.isNotEmpty()) {
            val rateMap = cachedEntities.associate { it.code to it.rateToUsd }.toMutableMap()
            if (!rateMap.containsKey("USD")) {
                rateMap["USD"] = 1.0
            }
            return rateMap
        }

        return DefaultCurrencyRates.FALLBACK_RATES
    }

    /**
     * Syncs latest exchange rates from open exchange API.
     * On success, saves to Room DB and returns fresh rates.
     * On failure (network error, timeout), falls back to getRates() without crashing.
     */
    suspend fun refreshRates(): Map<String, Double> = withContext(Dispatchers.IO) {
        try {
            val url = URL(apiUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 5000
                readTimeout = 5000
                requestMethod = "GET"
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val jsonString = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(jsonString)

                if (jsonObject.optString("result") == "success") {
                    val ratesObj = jsonObject.getJSONObject("rates")
                    val entities = mutableListOf<CurrencyRateEntity>()
                    val ratesMap = mutableMapOf<String, Double>()

                    val now = System.currentTimeMillis()
                    val keys = ratesObj.keys()
                    while (keys.hasNext()) {
                        val code = keys.next()
                        val rate = ratesObj.optDouble(code, -1.0)
                        if (rate > 0.0) {
                            entities.add(CurrencyRateEntity(code = code, rateToUsd = rate, timestamp = now))
                            ratesMap[code] = rate
                        }
                    }

                    if (entities.isNotEmpty()) {
                        try {
                            currencyRateDao.insertRates(entities)
                        } catch (e: Exception) {
                            // Ignore local DB write failure
                        }
                        return@withContext ratesMap
                    }
                }
            }
        } catch (e: Exception) {
            // Network failure or offline -> fail-safe fallback
        }

        return@withContext getRates()
    }

    /**
     * Converts an amount between two currency codes using exchange rates map.
     */
    fun convert(
        amount: Double,
        fromCurrency: String,
        toCurrency: String,
        rates: Map<String, Double>
    ): Double {
        if (fromCurrency == toCurrency) return amount
        if (amount == 0.0) return 0.0

        val fromRate = rates[fromCurrency] ?: DefaultCurrencyRates.FALLBACK_RATES[fromCurrency] ?: return amount
        val toRate = rates[toCurrency] ?: DefaultCurrencyRates.FALLBACK_RATES[toCurrency] ?: return amount

        if (fromRate <= 0.0 || toRate <= 0.0) return amount

        val amountInUsd = amount / fromRate
        return amountInUsd * toRate
    }
}

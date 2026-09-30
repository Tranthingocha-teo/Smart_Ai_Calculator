package dhn.intern.smart_ai_caculator_app.currency

import dhn.intern.smart_ai_caculator_app.data.local.dao.CurrencyRateDao
import dhn.intern.smart_ai_caculator_app.data.local.entity.CurrencyRateEntity
import dhn.intern.smart_ai_caculator_app.data.repository.CurrencyRepository
import dhn.intern.smart_ai_caculator_app.data.source.currencies.DefaultCurrencyRates
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CurrencyRepositoryTest {

    private lateinit var fakeDao: FakeCurrencyRateDao
    private lateinit var repository: CurrencyRepository

    @Before
    fun setUp() {
        fakeDao = FakeCurrencyRateDao()
        repository = CurrencyRepository(fakeDao)
    }

    @Test
    fun testGetRatesWhenCacheIsEmptyReturnsFallback() = runBlocking {
        val rates = repository.getRates()
        assertNotNull(rates)
        assertEquals(DefaultCurrencyRates.FALLBACK_RATES["VND"], rates["VND"])
        assertEquals(DefaultCurrencyRates.FALLBACK_RATES["USD"], rates["USD"])
    }

    @Test
    fun testGetRatesWhenCacheHasDataReturnsCachedRates() = runBlocking {
        fakeDao.insertRates(
            listOf(
                CurrencyRateEntity("USD", 1.0),
                CurrencyRateEntity("VND", 25000.0),
                CurrencyRateEntity("EUR", 0.90)
            )
        )

        val rates = repository.getRates()
        assertEquals(25000.0, rates["VND"]!!, 0.01)
        assertEquals(0.90, rates["EUR"]!!, 0.001)
    }

    @Test
    fun testCurrencyConversionUSDToVND() {
        val rates = mapOf("USD" to 1.0, "VND" to 25000.0)
        val converted = repository.convert(10.0, "USD", "VND", rates)
        assertEquals(250000.0, converted, 0.01)
    }

    @Test
    fun testCurrencyConversionEURToVND() {
        // 1 USD = 0.9 EUR, 1 USD = 25000 VND
        // 100 EUR = (100 / 0.9) * 25000 = 2,777,777.7778 VND
        val rates = mapOf("USD" to 1.0, "EUR" to 0.9, "VND" to 25000.0)
        val converted = repository.convert(100.0, "EUR", "VND", rates)
        assertEquals(2777777.78, converted, 1.0)
    }

    @Test
    fun testCurrencyConversionSameCurrencyReturnsOriginalValue() {
        val rates = mapOf("USD" to 1.0, "VND" to 25000.0)
        val converted = repository.convert(100.0, "USD", "USD", rates)
        assertEquals(100.0, converted, 0.001)
    }

    @Test
    fun testCurrencyConversionZeroValueReturnsZero() {
        val rates = mapOf("USD" to 1.0, "VND" to 25000.0)
        val converted = repository.convert(0.0, "USD", "VND", rates)
        assertEquals(0.0, converted, 0.001)
    }

    @Test
    fun testCurrencyConversionMissingRateFallsBackToDefaultRates() {
        val rates = emptyMap<String, Double>()
        val converted = repository.convert(10.0, "USD", "VND", rates)
        assertEquals(254500.0, converted, 1.0)
    }

    @Test
    fun testCurrencyConversionNegativeAmount() {
        val rates = mapOf("USD" to 1.0, "VND" to 25000.0)
        val converted = repository.convert(-5.0, "USD", "VND", rates)
        assertEquals(-125000.0, converted, 0.01)
    }

    @Test
    fun testRefreshRatesOfflineFallbackDoesNotCrash() = runBlocking {
        // Calls refreshRates which attempts network call, on fail falls back to getRates
        val rates = repository.refreshRates()
        assertTrue(rates.isNotEmpty())
        assertTrue(rates.containsKey("USD"))
    }
}

class FakeCurrencyRateDao : CurrencyRateDao {
    private val db = mutableMapOf<String, CurrencyRateEntity>()

    override suspend fun insertRates(rates: List<CurrencyRateEntity>) {
        rates.forEach { db[it.code] = it }
    }

    override suspend fun getAllRates(): List<CurrencyRateEntity> {
        return db.values.toList()
    }

    override suspend fun getRateByCode(code: String): CurrencyRateEntity? {
        return db[code]
    }
}

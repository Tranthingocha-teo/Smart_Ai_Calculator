package dhn.intern.smart_ai_caculator_app

import dhn.intern.smart_ai_caculator_app.data.constant.DefaultCurrencyRates
import dhn.intern.smart_ai_caculator_app.data.local.dao.CurrencyRateDao
import dhn.intern.smart_ai_caculator_app.data.local.entity.CurrencyRateEntity
import dhn.intern.smart_ai_caculator_app.data.remote.CurrencyApiService
import dhn.intern.smart_ai_caculator_app.data.remote.dto.CurrencyResponseDto
import dhn.intern.smart_ai_caculator_app.data.repository.CurrencyRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

// Tạo Fake Service giả lập lỗi mạng
class FakeErrorApiService : CurrencyApiService {
    override suspend fun getLatestRates(): CurrencyResponseDto {
        throw IOException("No internet connection")
    }
}

// Tạo Fake DAO giả lập Room Database trong RAM
class FakeCurrencyRateDao(private var db: List<CurrencyRateEntity> = emptyList()) : CurrencyRateDao {
    override suspend fun insertRates(rates: List<CurrencyRateEntity>) {
        db = rates
    }

    override suspend fun getAllRates(): List<CurrencyRateEntity> = db

    override suspend fun getRateByCode(code: String): CurrencyRateEntity? {
        return db.find { it.code == code }
    }
}

class CurrencyRepositoryTest {

    @Test
    fun getRates_whenNetworkFails_fallbackToRoomDb() = runBlocking {
        // Giả lập Room DB đã có dữ liệu cache từ trước
        val cachedEntities = listOf(
            CurrencyRateEntity("USD", 1.0, 1000L),
            CurrencyRateEntity("VND", 25000.0, 1000L)
        )
        val fakeDao = FakeCurrencyRateDao(cachedEntities)
        val repository = CurrencyRepository(FakeErrorApiService(), fakeDao)

        val result = repository.getRates()

        assertEquals(25000.0, result["VND"] ?: 0.0, 0.0)
    }

    @Test
    fun getRates_whenNetworkFailsAndRoomEmpty_fallbackToStaticRates() = runBlocking {
        // Giả lập app mới cài: mạng mất và Room cũng trống
        val fakeDao = FakeCurrencyRateDao(emptyList())
        val repository = CurrencyRepository(FakeErrorApiService(), fakeDao)

        val result = repository.getRates()

        assertEquals(DefaultCurrencyRates.STATIC_RATES["VND"], result["VND"])
    }
}

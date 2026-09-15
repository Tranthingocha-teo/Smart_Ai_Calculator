package dhn.intern.smart_ai_caculator_app.data.repository

import dhn.intern.smart_ai_caculator_app.data.constant.DefaultCurrencyRates
import dhn.intern.smart_ai_caculator_app.data.local.dao.CurrencyRateDao
import dhn.intern.smart_ai_caculator_app.data.local.entity.CurrencyRateEntity
import dhn.intern.smart_ai_caculator_app.data.remote.CurrencyApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CurrencyRepository(
    private val apiService: CurrencyApiService,
    private val currencyRateDao: CurrencyRateDao
) {

    /**
     * Lấy tỷ giá ưu tiên:
     * 1. Gọi API open.er-api.com -> Lưu vào Room DB.
     * 2. Nếu API lỗi hoặc mất mạng -> Đọc từ Room DB.
     * 3. Nếu Room DB trống -> Dùng bộ dữ liệu tĩnh (STATIC_RATES).
     */
    suspend fun getRates(): Map<String, Double> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getLatestRates()
            val apiRates = response.rates

            if (!apiRates.isNullOrEmpty()) {
                val currentTime = System.currentTimeMillis()
                val entities = apiRates.map { (code, rate) ->
                    CurrencyRateEntity(
                        code = code,
                        rateToUsd = rate,
                        timestamp = currentTime
                    )
                }
                currencyRateDao.insertRates(entities)
                return@withContext apiRates
            }
        } catch (_: Exception) {
            // Mạng lỗi hoặc timeout -> Bỏ qua lỗi và chuyển sang fallback
        }

        // Bước dự phòng 1: Đọc từ Room DB
        val cachedEntities = currencyRateDao.getAllRates()
        if (cachedEntities.isNotEmpty()) {
            return@withContext cachedEntities.associate { it.code to it.rateToUsd }
        }

        // Bước dự phòng 2: Room DB cũng trống (app mới cài lần đầu chưa bật mạng bao giờ)
        return@withContext DefaultCurrencyRates.STATIC_RATES
    }

    /**
     * Lấy timestamp lần cập nhật gần nhất từ DB (nếu có)
     */
    suspend fun getLastUpdatedTimestamp(): Long? = withContext(Dispatchers.IO) {
        val cached = currencyRateDao.getAllRates()
        return@withContext cached.firstOrNull()?.timestamp
    }
}

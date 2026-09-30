package dhn.intern.smart_ai_caculator_app.data.repository

import dhn.intern.smart_ai_caculator_app.data.local.dao.CalculatorHistoryDao
import dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity
import dhn.intern.smart_ai_caculator_app.enum.HistorySource
import kotlinx.coroutines.flow.Flow

class CalculatorHistoryRepository(
    private val dao: CalculatorHistoryDao
) {

    fun getHistory(): Flow<List<CalculatorHistoryEntity>> =
        dao.getAllHistory()

    fun getHistoryBySource(source: HistorySource): Flow<List<CalculatorHistoryEntity>> =
        dao.getHistoryBySource(source.name)

    suspend fun getLatestBySource(source: HistorySource, limit: Int = 3): List<CalculatorHistoryEntity> =
        dao.getLatestBySource(source.name, limit)

    suspend fun save(expression: String, result: String) {
        dao.insert(
            CalculatorHistoryEntity(
                expression = expression,
                result = result,
                source = HistorySource.CALCULATOR.name
            )
        )
    }

    // Hàm lưu lịch sử chuyển đổi đơn vị/tiền tệ cho Ticket #33
    suspend fun saveUnitConverter(expression: String, result: String) {
        dao.insert(
            CalculatorHistoryEntity(
                expression = expression,
                result = result,
                source = HistorySource.UNIT_CONVERTER.name
            )
        )
    }

    suspend fun saveGraphing(
        expression: String,
        colorHex: String,
        isVisible: Boolean = true,
        viewportBounds: String? = null
    ) {
        dao.insert(
            CalculatorHistoryEntity(
                expression = expression,
                result = "f(x) = $expression",
                source = HistorySource.GRAPHING_CALCULATOR.name,
                colorHex = colorHex,
                isVisible = isVisible,
                viewportBounds = viewportBounds
            )
        )
    }

    suspend fun clearAll() {
        dao.clearAll()
    }

    suspend fun clearBySource(source: HistorySource) {
        dao.clearBySource(source.name)
    }
}

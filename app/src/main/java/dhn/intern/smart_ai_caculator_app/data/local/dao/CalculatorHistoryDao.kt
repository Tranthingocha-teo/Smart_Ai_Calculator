package dhn.intern.smart_ai_caculator_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculatorHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: CalculatorHistoryEntity)

    @Query("SELECT * FROM calculator_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<CalculatorHistoryEntity>>

    @Query("SELECT * FROM calculator_history WHERE source = :source ORDER BY timestamp DESC")
    fun getHistoryBySource(source: String): Flow<List<CalculatorHistoryEntity>>

    @Query("SELECT * FROM calculator_history WHERE source = :source ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getLatestBySource(source: String, limit: Int = 3): List<CalculatorHistoryEntity>

    @Query("DELETE FROM calculator_history")
    suspend fun clearAll()

    @Query("DELETE FROM calculator_history WHERE source = :source")
    suspend fun clearBySource(source: String)
}

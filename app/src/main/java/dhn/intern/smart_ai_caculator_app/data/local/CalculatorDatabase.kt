package dhn.intern.smart_ai_caculator_app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dhn.intern.smart_ai_caculator_app.data.local.dao.CalculatorHistoryDao
import dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity

@Database(
    entities = [CalculatorHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class CalculatorDatabase : RoomDatabase() {
    abstract fun historyDao(): CalculatorHistoryDao
}
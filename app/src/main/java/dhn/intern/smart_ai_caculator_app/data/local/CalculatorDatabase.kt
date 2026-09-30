package dhn.intern.smart_ai_caculator_app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dhn.intern.smart_ai_caculator_app.data.local.dao.CalculatorHistoryDao
import dhn.intern.smart_ai_caculator_app.data.local.dao.CurrencyRateDao
import dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity
import dhn.intern.smart_ai_caculator_app.data.local.entity.CurrencyRateEntity

@Database(
    entities = [
        CalculatorHistoryEntity::class,
        CurrencyRateEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class CalculatorDatabase : RoomDatabase() {
    abstract fun historyDao(): CalculatorHistoryDao
    abstract fun currencyRateDao(): CurrencyRateDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE calculator_history ADD COLUMN source TEXT NOT NULL DEFAULT 'CALCULATOR'")
                db.execSQL("ALTER TABLE calculator_history ADD COLUMN isVisible INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE calculator_history ADD COLUMN colorHex TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE calculator_history ADD COLUMN viewportBounds TEXT DEFAULT NULL")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS `currency_rates` (
                `code` TEXT NOT NULL,
                `rateToUsd` REAL NOT NULL,
                `timestamp` INTEGER NOT NULL,
                PRIMARY KEY(`code`)
            )
            """.trimIndent()
                )
            }
        }
    }
}

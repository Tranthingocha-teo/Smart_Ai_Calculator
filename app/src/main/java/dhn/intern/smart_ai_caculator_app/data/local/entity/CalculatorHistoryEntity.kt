package dhn.intern.smart_ai_caculator_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

import dhn.intern.smart_ai_caculator_app.enum.HistorySource

@Entity(tableName = "calculator_history")
data class CalculatorHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis(),
    val source: String = HistorySource.CALCULATOR.name,
    val isVisible: Boolean = true,
    val colorHex: String? = null,
    val viewportBounds: String? = null
)

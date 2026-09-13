package dhn.intern.smart_ai_caculator_app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity caching foreign currency exchange rates fetched from open rate APIs.
 * Handled under Ticket #31 by @thuyydung.
 */
@Entity(tableName = "currency_rates")
data class CurrencyRateEntity(
    @PrimaryKey
    val code: String,
    val rateToUsd: Double,
    val timestamp: Long = System.currentTimeMillis()
)

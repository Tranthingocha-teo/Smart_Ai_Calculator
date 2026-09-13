package dhn.intern.smart_ai_caculator_app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dhn.intern.smart_ai_caculator_app.data.local.entity.CurrencyRateEntity

/**
 * DAO interface for accessing cached currency exchange rates.
 * Handled under Ticket #31 by @thuyydung.
 */
@Dao
interface CurrencyRateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: List<CurrencyRateEntity>)

    @Query("SELECT * FROM currency_rates")
    suspend fun getAllRates(): List<CurrencyRateEntity>

    @Query("SELECT * FROM currency_rates WHERE code = :code LIMIT 1")
    suspend fun getRateByCode(code: String): CurrencyRateEntity?
}

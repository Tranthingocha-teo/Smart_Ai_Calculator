package dhn.intern.smart_ai_caculator_app.injection

import androidx.room.Room
import dhn.intern.smart_ai_caculator_app.data.local.CalculatorDatabase
import dhn.intern.smart_ai_caculator_app.data.repository.CalculatorHistoryRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single {
        Room.databaseBuilder(
            androidContext(),
            CalculatorDatabase::class.java,
            "calculator_db"
        )
            .addMigrations(
                CalculatorDatabase.MIGRATION_1_2,
                CalculatorDatabase.MIGRATION_2_3
            )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    single { get<CalculatorDatabase>().historyDao() }

    single { get<CalculatorDatabase>().currencyRateDao() }

    single { CalculatorHistoryRepository(get()) }
}

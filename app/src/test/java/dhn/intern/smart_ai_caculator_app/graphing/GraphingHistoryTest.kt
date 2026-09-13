package dhn.intern.smart_ai_caculator_app.graphing

import dhn.intern.smart_ai_caculator_app.data.local.dao.CalculatorHistoryDao
import dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity
import dhn.intern.smart_ai_caculator_app.data.repository.CalculatorHistoryRepository
import dhn.intern.smart_ai_caculator_app.enum.HistorySource
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GraphingHistoryTest {

    @Test
    fun `HistorySource enum contains GRAPHING_CALCULATOR`() {
        val value = HistorySource.valueOf("GRAPHING_CALCULATOR")
        assertEquals(HistorySource.GRAPHING_CALCULATOR, value)
    }

    @Test
    fun `CalculatorHistoryEntity supports graphing fields with default values`() {
        val entity = CalculatorHistoryEntity(
            expression = "x^2 - 4",
            result = "f(x) = x^2 - 4",
            source = HistorySource.GRAPHING_CALCULATOR.name,
            colorHex = "#2196F3",
            isVisible = true,
            viewportBounds = "-10.0,10.0,-10.0,10.0"
        )

        assertEquals("x^2 - 4", entity.expression)
        assertEquals(HistorySource.GRAPHING_CALCULATOR.name, entity.source)
        assertEquals("#2196F3", entity.colorHex)
        assertTrue(entity.isVisible)
        assertEquals("-10.0,10.0,-10.0,10.0", entity.viewportBounds)
    }

    @Test
    fun `CalculatorHistoryRepository saveGraphing persists entity with graphing metadata`() = runBlocking {
        val inserted = mutableListOf<CalculatorHistoryEntity>()
        val mockDao = object : CalculatorHistoryDao {
            override suspend fun insert(history: CalculatorHistoryEntity) {
                inserted.add(history)
            }

            override fun getAllHistory() = flowOf(emptyList<CalculatorHistoryEntity>())

            override fun getHistoryBySource(source: String) = flowOf(inserted.filter { it.source == source })

            override suspend fun getLatestBySource(source: String, limit: Int): List<CalculatorHistoryEntity> {
                return inserted.filter { it.source == source }.take(limit)
            }

            override suspend fun clearAll() {
                inserted.clear()
            }

            override suspend fun clearBySource(source: String) {
                inserted.removeAll { it.source == source }
            }
        }

        val repository = CalculatorHistoryRepository(mockDao)
        repository.saveGraphing(
            expression = "sin(x)",
            colorHex = "#4CAF50",
            isVisible = true,
            viewportBounds = "-5.0,5.0,-5.0,5.0"
        )

        assertEquals(1, inserted.size)
        val item = inserted[0]
        assertEquals("sin(x)", item.expression)
        assertEquals(HistorySource.GRAPHING_CALCULATOR.name, item.source)
        assertEquals("#4CAF50", item.colorHex)
        assertTrue(item.isVisible)
        assertEquals("-5.0,5.0,-5.0,5.0", item.viewportBounds)
    }

    @Test
    fun `MIGRATION_1_2 executes valid alter table statements`() {
        val executedSql = mutableListOf<String>()
        val mockDb = java.lang.reflect.Proxy.newProxyInstance(
            androidx.sqlite.db.SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(androidx.sqlite.db.SupportSQLiteDatabase::class.java)
        ) { _, method, args ->
            if (method.name == "execSQL" && args != null && args.isNotEmpty()) {
                executedSql.add(args[0] as String)
            }
            null
        } as androidx.sqlite.db.SupportSQLiteDatabase

        dhn.intern.smart_ai_caculator_app.data.local.CalculatorDatabase.MIGRATION_1_2.migrate(mockDb)

        assertEquals(4, executedSql.size)
        assertTrue(executedSql.any { it.contains("ADD COLUMN source") })
        assertTrue(executedSql.any { it.contains("ADD COLUMN isVisible") })
        assertTrue(executedSql.any { it.contains("ADD COLUMN colorHex") })
        assertTrue(executedSql.any { it.contains("ADD COLUMN viewportBounds") })
    }
}

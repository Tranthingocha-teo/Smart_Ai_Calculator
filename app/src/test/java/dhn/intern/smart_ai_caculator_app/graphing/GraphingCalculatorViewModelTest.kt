package dhn.intern.smart_ai_caculator_app.graphing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import dhn.intern.smart_ai_caculator_app.domain.graphing.DefaultGraphingEngine
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.GraphingCalculatorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GraphingCalculatorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: GraphingCalculatorViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = GraphingCalculatorViewModel(DefaultGraphingEngine())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has one default quadratic function with sampled curve`() {
        val state = viewModel.uiState.value
        assertEquals(1, state.functions.size)
        assertEquals("x^2 - 4", state.functions[0].expression)
        assertTrue(state.functions[0].isVisible)
        assertEquals(1, state.sampledCurves.size)
    }

    @Test
    fun `onKeyPress appends key and updates sampled curve`() {
        viewModel.onKeyPress("AC")
        assertEquals("", viewModel.uiState.value.functions[0].expression)

        viewModel.onKeyPress("2")
        viewModel.onKeyPress("x")
        viewModel.onKeyPress("+")
        viewModel.onKeyPress("1")

        val state = viewModel.uiState.value
        assertEquals("2x+1", state.functions[0].expression)
        assertEquals(1, state.sampledCurves.size)
        assertTrue(state.sampledCurves[0].continuousSegments.isNotEmpty())
    }

    @Test
    fun `addFunction and removeFunction correctly manages multi-curve state`() {
        viewModel.addFunction("sin(x)")
        var state = viewModel.uiState.value
        assertEquals(2, state.functions.size)
        assertEquals("sin(x)", state.functions[1].expression)

        viewModel.removeFunction(0)
        state = viewModel.uiState.value
        assertEquals(1, state.functions.size)
        assertEquals("sin(x)", state.functions[0].expression)
    }

    @Test
    fun `pan and zoom update viewport reactively`() {
        val initialViewport = viewModel.uiState.value.viewport
        viewModel.onPan(Offset(100f, 0f), Size(1000f, 1000f))

        val pannedViewport = viewModel.uiState.value.viewport
        assertTrue(pannedViewport.minX < initialViewport.minX)

        viewModel.resetViewport()
        val resetViewport = viewModel.uiState.value.viewport
        assertEquals(initialViewport.minX, resetViewport.minX, 1e-4)
        assertEquals(initialViewport.maxX, resetViewport.maxX, 1e-4)
    }

    @Test
    fun `toggleFunctionVisibility hides curve from sampledCurves`() {
        viewModel.toggleFunctionVisibility(0)
        val state = viewModel.uiState.value
        assertFalse(state.functions[0].isVisible)
        assertEquals(0, state.sampledCurves.size)
    }

    @Test
    fun `applyPreset updates active function and evaluates curve`() {
        val presetRepo = dhn.intern.smart_ai_caculator_app.data.repository.GraphPresetRepository()
        val sinePreset = presetRepo.getPresetById("sine")!!
        viewModel.applyPreset(sinePreset)

        val state = viewModel.uiState.value
        assertEquals("sin(x)", state.functions[0].expression)
        assertEquals(1, state.sampledCurves.size)
        assertTrue(state.sampledCurves[0].continuousSegments.isNotEmpty())
    }

    @Test
    fun `restores active functions from history repository if present`() = kotlinx.coroutines.runBlocking {
        val mockHistory = listOf(
            dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity(
                expression = "3x - 2",
                result = "f(x) = 3x - 2",
                source = dhn.intern.smart_ai_caculator_app.enum.HistorySource.GRAPHING_CALCULATOR.name,
                colorHex = "#FF5722",
                isVisible = true
            )
        )
        val mockDao = object : dhn.intern.smart_ai_caculator_app.data.local.dao.CalculatorHistoryDao {
            override suspend fun insert(history: dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity) {}
            override fun getAllHistory() = kotlinx.coroutines.flow.flowOf(emptyList<dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity>())
            override fun getHistoryBySource(source: String) = kotlinx.coroutines.flow.flowOf(mockHistory)
            override suspend fun getLatestBySource(source: String, limit: Int) = mockHistory
            override suspend fun clearAll() {}
            override suspend fun clearBySource(source: String) {}
        }
        val historyRepo = dhn.intern.smart_ai_caculator_app.data.repository.CalculatorHistoryRepository(mockDao)
        val vm = GraphingCalculatorViewModel(
            graphingEngine = DefaultGraphingEngine(),
            historyRepository = historyRepo
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(1, state.functions.size)
        assertEquals("3x - 2", state.functions[0].expression)
        assertEquals("#FF5722", state.functions[0].colorHex)
    }

    @Test
    fun `restores multiple functions in chronological order`() = kotlinx.coroutines.runBlocking {
        val mockHistory = listOf(
            // Most recent first (as returned by ORDER BY timestamp DESC)
            dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity(
                expression = "cos(x)",
                result = "f2(x) = cos(x)",
                source = dhn.intern.smart_ai_caculator_app.enum.HistorySource.GRAPHING_CALCULATOR.name,
                colorHex = "#FF5722",
                isVisible = true,
                timestamp = 2000L
            ),
            dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity(
                expression = "sin(x)",
                result = "f1(x) = sin(x)",
                source = dhn.intern.smart_ai_caculator_app.enum.HistorySource.GRAPHING_CALCULATOR.name,
                colorHex = "#2196F3",
                isVisible = true,
                timestamp = 1000L
            )
        )
        val mockDao = object : dhn.intern.smart_ai_caculator_app.data.local.dao.CalculatorHistoryDao {
            override suspend fun insert(history: dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity) {}
            override fun getAllHistory() = kotlinx.coroutines.flow.flowOf(emptyList<dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity>())
            override fun getHistoryBySource(source: String) = kotlinx.coroutines.flow.flowOf(mockHistory)
            override suspend fun getLatestBySource(source: String, limit: Int) = mockHistory
            override suspend fun clearAll() {}
            override suspend fun clearBySource(source: String) {}
        }
        val historyRepo = dhn.intern.smart_ai_caculator_app.data.repository.CalculatorHistoryRepository(mockDao)
        val vm = GraphingCalculatorViewModel(
            graphingEngine = DefaultGraphingEngine(),
            historyRepository = historyRepo
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(2, state.functions.size)
        // With .reversed(), sin(x) should be first, cos(x) second
        assertEquals("sin(x)", state.functions[0].expression)
        assertEquals("cos(x)", state.functions[1].expression)
        assertEquals(2, state.sampledCurves.size)
    }
}

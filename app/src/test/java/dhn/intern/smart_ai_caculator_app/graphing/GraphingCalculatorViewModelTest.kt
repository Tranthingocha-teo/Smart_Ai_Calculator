package dhn.intern.smart_ai_caculator_app.graphing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import dhn.intern.smart_ai_caculator_app.domain.graphing.DefaultGraphingEngine
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.GraphingCalculatorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GraphingCalculatorViewModelTest {

    private lateinit var viewModel: GraphingCalculatorViewModel

    @Before
    fun setUp() {
        viewModel = GraphingCalculatorViewModel(DefaultGraphingEngine())
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
}

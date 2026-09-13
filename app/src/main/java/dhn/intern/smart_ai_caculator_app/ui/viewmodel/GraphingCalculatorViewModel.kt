package dhn.intern.smart_ai_caculator_app.ui.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import dhn.intern.smart_ai_caculator_app.domain.graphing.CompiledFunction
import dhn.intern.smart_ai_caculator_app.domain.graphing.CoordinateTransform
import dhn.intern.smart_ai_caculator_app.domain.graphing.GraphPoint
import dhn.intern.smart_ai_caculator_app.domain.graphing.GraphingEngine
import dhn.intern.smart_ai_caculator_app.domain.graphing.SampledCurve
import dhn.intern.smart_ai_caculator_app.domain.graphing.SpecialPoint
import dhn.intern.smart_ai_caculator_app.domain.graphing.ViewportBounds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import kotlin.math.abs

data class FunctionItem(
    val id: String = UUID.randomUUID().toString(),
    val expression: String = "",
    val color: Color,
    val isVisible: Boolean = true,
    val errorMessage: String? = null
)

data class GraphingUiState(
    val functions: List<FunctionItem> = emptyList(),
    val activeFunctionIndex: Int = 0,
    val viewport: ViewportBounds = ViewportBounds(),
    val sampledCurves: List<SampledCurve> = emptyList(),
    val intersections: List<SpecialPoint> = emptyList(),
    val tracePoint: GraphPoint? = null,
    val traceSpecialPoint: SpecialPoint? = null,
    val isKeypadVisible: Boolean = true
)

class GraphingCalculatorViewModel(
    private val graphingEngine: GraphingEngine
) : ViewModel() {

    private val colorPalette = listOf(
        Color(0xFF2196F3), // Blue
        Color(0xFFFF5722), // Coral / Orange
        Color(0xFF4CAF50), // Green
        Color(0xFF9C27B0), // Purple
        Color(0xFF00BCD4)  // Cyan
    )

    private val _uiState = MutableStateFlow(
        GraphingUiState(
            functions = listOf(
                FunctionItem(expression = "x^2 - 4", color = colorPalette[0])
            )
        )
    )
    val uiState: StateFlow<GraphingUiState> = _uiState.asStateFlow()

    init {
        recomputeCurves()
    }

    fun onKeyPress(key: String) {
        val currentIndex = _uiState.value.activeFunctionIndex
        if (currentIndex !in _uiState.value.functions.indices) return

        val currentExpr = _uiState.value.functions[currentIndex].expression
        val updatedExpr = when (key) {
            "AC" -> ""
            "⌫" -> if (currentExpr.isNotEmpty()) currentExpr.dropLast(1) else ""
            "+f(x)" -> {
                addFunction("")
                return
            }
            "Hide" -> {
                toggleKeypad()
                return
            }
            "sin", "cos", "tan", "ln", "log", "sqrt" -> "$currentExpr$key("
            "√" -> "${currentExpr}sqrt("
            "x²" -> "${currentExpr}x²"
            "x^" -> "${currentExpr}x^"
            "π" -> "${currentExpr}π"
            else -> "$currentExpr$key"
        }

        updateFunctionExpression(currentIndex, updatedExpr)
    }

    fun updateFunctionExpression(index: Int, newExpression: String) {
        if (index !in _uiState.value.functions.indices) return
        _uiState.update { state ->
            val updated = state.functions.toMutableList()
            updated[index] = updated[index].copy(expression = newExpression, errorMessage = null)
            state.copy(functions = updated)
        }
        recomputeCurves()
    }

    fun addFunction(initialExpression: String = "") {
        _uiState.update { state ->
            if (state.functions.size >= 5) return@update state // limit to 5 functions
            val nextColor = colorPalette[state.functions.size % colorPalette.size]
            val newFunctions = state.functions + FunctionItem(
                expression = initialExpression,
                color = nextColor
            )
            state.copy(
                functions = newFunctions,
                activeFunctionIndex = newFunctions.lastIndex
            )
        }
        recomputeCurves()
    }

    fun removeFunction(index: Int) {
        if (index !in _uiState.value.functions.indices) return
        _uiState.update { state ->
            if (state.functions.size <= 1) {
                // If only 1, just clear its expression
                val cleared = listOf(state.functions[0].copy(expression = "", errorMessage = null))
                return@update state.copy(functions = cleared, activeFunctionIndex = 0)
            }
            val updated = state.functions.toMutableList()
            updated.removeAt(index)
            val newActive = if (state.activeFunctionIndex >= updated.size) updated.lastIndex else state.activeFunctionIndex
            state.copy(functions = updated, activeFunctionIndex = newActive)
        }
        recomputeCurves()
    }

    fun toggleFunctionVisibility(index: Int) {
        if (index !in _uiState.value.functions.indices) return
        _uiState.update { state ->
            val updated = state.functions.toMutableList()
            updated[index] = updated[index].copy(isVisible = !updated[index].isVisible)
            state.copy(functions = updated)
        }
        recomputeCurves()
    }

    fun selectFunction(index: Int) {
        if (index in _uiState.value.functions.indices) {
            _uiState.update { it.copy(activeFunctionIndex = index, isKeypadVisible = true) }
        }
    }

    fun toggleKeypad() {
        _uiState.update { it.copy(isKeypadVisible = !it.isKeypadVisible) }
    }

    fun onPan(deltaScreen: Offset, canvasSize: Size) {
        _uiState.update { state ->
            val newViewport = CoordinateTransform.pan(state.viewport, deltaScreen, canvasSize)
            state.copy(viewport = newViewport)
        }
        recomputeCurves()
    }

    fun onZoom(zoomFactor: Float, centerScreen: Offset, canvasSize: Size) {
        _uiState.update { state ->
            val newViewport = CoordinateTransform.zoom(state.viewport, zoomFactor, centerScreen, canvasSize)
            state.copy(viewport = newViewport)
        }
        recomputeCurves()
    }

    fun resetViewport() {
        _uiState.update { it.copy(viewport = ViewportBounds(-10.0, 10.0, -10.0, 10.0)) }
        recomputeCurves()
    }

    fun onTrace(screenOffset: Offset?, canvasSize: Size) {
        if (screenOffset == null) {
            _uiState.update { it.copy(tracePoint = null, traceSpecialPoint = null) }
            return
        }

        val mathPoint = CoordinateTransform.screenToMath(screenOffset, _uiState.value.viewport, canvasSize)
        val viewport = _uiState.value.viewport

        // Check if cursor is near any special point (magnetic snap)
        val allSpecialPoints = _uiState.value.sampledCurves.flatMap { it.specialPoints } + _uiState.value.intersections
        val snapThresholdMath = viewport.width * 0.03 // within 3% of viewport width

        val snappedSpecial = allSpecialPoints.find {
            abs(it.point.x - mathPoint.x) < snapThresholdMath && abs(it.point.y - mathPoint.y) < (viewport.height * 0.05)
        }

        if (snappedSpecial != null) {
            _uiState.update { it.copy(tracePoint = snappedSpecial.point, traceSpecialPoint = snappedSpecial) }
        } else {
            // Snap to the active function curve at touch x
            val activeFunc = _uiState.value.functions.getOrNull(_uiState.value.activeFunctionIndex)
            if (activeFunc != null && activeFunc.isVisible && activeFunc.expression.isNotBlank()) {
                val compiled = graphingEngine.compile(activeFunc.expression).getOrNull()
                if (compiled != null) {
                    val y = compiled.evaluate(mathPoint.x)
                    if (!y.isNaN() && !y.isInfinite()) {
                        _uiState.update { it.copy(tracePoint = GraphPoint(mathPoint.x, y), traceSpecialPoint = null) }
                        return
                    }
                }
            }
            _uiState.update { it.copy(tracePoint = mathPoint, traceSpecialPoint = null) }
        }
    }

    private fun recomputeCurves() {
        val currentState = _uiState.value
        val viewport = currentState.viewport
        val newCurves = mutableListOf<SampledCurve>()
        val compiledList = mutableListOf<CompiledFunction>()

        currentState.functions.forEachIndexed { index, fnItem ->
            if (fnItem.isVisible && fnItem.expression.isNotBlank()) {
                val compileResult = graphingEngine.compile(fnItem.expression)
                if (compileResult.isSuccess) {
                    val compiled = compileResult.getOrThrow()
                    compiledList.add(compiled)
                    val curve = graphingEngine.sample(compiled, viewport, screenPixelWidth = 500)
                    newCurves.add(curve)
                }
            }
        }

        // Compute intersections between visible curves
        val intersections = mutableListOf<SpecialPoint>()
        if (compiledList.size >= 2) {
            for (i in 0 until compiledList.size - 1) {
                for (j in i + 1 until compiledList.size) {
                    intersections.addAll(
                        graphingEngine.findIntersections(compiledList[i], compiledList[j], viewport)
                    )
                }
            }
        }

        _uiState.update { it.copy(sampledCurves = newCurves, intersections = intersections) }
    }
}

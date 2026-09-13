package dhn.intern.smart_ai_caculator_app.ui.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dhn.intern.smart_ai_caculator_app.data.repository.CalculatorHistoryRepository
import dhn.intern.smart_ai_caculator_app.data.repository.GraphPreset
import dhn.intern.smart_ai_caculator_app.data.repository.GraphPresetRepository
import dhn.intern.smart_ai_caculator_app.domain.graphing.CompiledFunction
import dhn.intern.smart_ai_caculator_app.domain.graphing.CoordinateTransform
import dhn.intern.smart_ai_caculator_app.domain.graphing.GraphPoint
import dhn.intern.smart_ai_caculator_app.domain.graphing.GraphingEngine
import dhn.intern.smart_ai_caculator_app.domain.graphing.SampledCurve
import dhn.intern.smart_ai_caculator_app.domain.graphing.SpecialPoint
import dhn.intern.smart_ai_caculator_app.domain.graphing.ViewportBounds
import dhn.intern.smart_ai_caculator_app.enum.HistorySource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.abs

data class FunctionItem(
    val id: String = UUID.randomUUID().toString(),
    val expression: String = "",
    val color: Color,
    val isVisible: Boolean = true,
    val errorMessage: String? = null
) {
    val colorHex: String
        get() {
            val alpha = (color.alpha * 255).toInt()
            val red = (color.red * 255).toInt()
            val green = (color.green * 255).toInt()
            val blue = (color.blue * 255).toInt()
            return if (alpha == 255) {
                String.format("#%02X%02X%02X", red, green, blue)
            } else {
                String.format("#%02X%02X%02X%02X", alpha, red, green, blue)
            }
        }
}

data class RenderedCurve(
    val curve: SampledCurve,
    val color: Color,
    val functionId: String
)

data class GraphingUiState(
    val functions: List<FunctionItem> = emptyList(),
    val activeFunctionIndex: Int = 0,
    val viewport: ViewportBounds = ViewportBounds(),
    val sampledCurves: List<SampledCurve> = emptyList(),
    val renderedCurves: List<RenderedCurve> = emptyList(),
    val intersections: List<SpecialPoint> = emptyList(),
    val tracePoint: GraphPoint? = null,
    val traceSpecialPoint: SpecialPoint? = null,
    val isKeypadVisible: Boolean = true,
    val isTraceMode: Boolean = false
)

class GraphingCalculatorViewModel(
    private val graphingEngine: GraphingEngine,
    private val historyRepository: CalculatorHistoryRepository? = null,
    private val presetRepository: GraphPresetRepository = GraphPresetRepository()
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

    private var isRestoringFromHistory = true

    init {
        restoreFromHistoryIfAvailable()
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
                setKeypadVisible(false)
                return
            }
            "÷" -> "$currentExpr/"
            "×" -> "$currentExpr*"
            "−" -> "$currentExpr-"
            "sin", "cos", "tan", "ln", "log" -> "$currentExpr$key("
            "√" -> "${currentExpr}sqrt("
            "x²" -> "${currentExpr}x²"
            "x^" -> "${currentExpr}x^"
            "π" -> "${currentExpr}π"
            "e" -> "${currentExpr}e"
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
        persistActiveFunctions()
    }

    fun clearActiveFunction() {
        val currentIndex = _uiState.value.activeFunctionIndex
        if (currentIndex in _uiState.value.functions.indices) {
            updateFunctionExpression(currentIndex, "")
        }
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
                activeFunctionIndex = newFunctions.lastIndex,
                isKeypadVisible = true
            )
        }
        recomputeCurves()
        persistActiveFunctions()
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
        persistActiveFunctions()
    }

    fun toggleFunctionVisibility(index: Int) {
        if (index !in _uiState.value.functions.indices) return
        _uiState.update { state ->
            val updated = state.functions.toMutableList()
            updated[index] = updated[index].copy(isVisible = !updated[index].isVisible)
            state.copy(functions = updated)
        }
        recomputeCurves()
        persistActiveFunctions()
    }

    fun selectFunction(index: Int) {
        if (index in _uiState.value.functions.indices) {
            _uiState.update { it.copy(activeFunctionIndex = index, isKeypadVisible = true) }
        }
    }

    fun toggleKeypad() {
        _uiState.update { it.copy(isKeypadVisible = !it.isKeypadVisible) }
    }

    fun setKeypadVisible(visible: Boolean) {
        _uiState.update { it.copy(isKeypadVisible = visible) }
    }

    fun toggleTraceMode() {
        _uiState.update {
            val next = !it.isTraceMode
            it.copy(isTraceMode = next, tracePoint = if (!next) null else it.tracePoint)
        }
    }

    fun clearTracePoint() {
        _uiState.update { it.copy(tracePoint = null, traceSpecialPoint = null) }
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

    fun zoomIn() {
        _uiState.update { state ->
            val vp = state.viewport
            val factor = 0.8
            val newW = vp.width * factor
            val newH = vp.height * factor
            val cx = (vp.minX + vp.maxX) / 2.0
            val cy = (vp.minY + vp.maxY) / 2.0
            state.copy(
                viewport = ViewportBounds(cx - newW / 2.0, cx + newW / 2.0, cy - newH / 2.0, cy + newH / 2.0)
            )
        }
        recomputeCurves()
    }

    fun zoomOut() {
        _uiState.update { state ->
            val vp = state.viewport
            val factor = 1.25
            val newW = vp.width * factor
            val newH = vp.height * factor
            val cx = (vp.minX + vp.maxX) / 2.0
            val cy = (vp.minY + vp.maxY) / 2.0
            state.copy(
                viewport = ViewportBounds(cx - newW / 2.0, cx + newW / 2.0, cy - newH / 2.0, cy + newH / 2.0)
            )
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
        val snapThresholdMath = viewport.width * 0.04 // within 4% of viewport width

        val snappedSpecial = allSpecialPoints.find {
            abs(it.point.x - mathPoint.x) < snapThresholdMath && abs(it.point.y - mathPoint.y) < (viewport.height * 0.06)
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
        val newSampledCurves = mutableListOf<SampledCurve>()
        val newRenderedCurves = mutableListOf<RenderedCurve>()
        val compiledList = mutableListOf<CompiledFunction>()
        val updatedFunctions = currentState.functions.toMutableList()

        currentState.functions.forEachIndexed { index, fnItem ->
            if (fnItem.expression.isBlank()) {
                updatedFunctions[index] = fnItem.copy(errorMessage = null)
            } else {
                val compileResult = graphingEngine.compile(fnItem.expression)
                if (compileResult.isSuccess) {
                    val compiled = compileResult.getOrThrow()
                    updatedFunctions[index] = fnItem.copy(errorMessage = null)
                    if (fnItem.isVisible) {
                        compiledList.add(compiled)
                        val curve = graphingEngine.sample(compiled, viewport, screenPixelWidth = 600)
                        newSampledCurves.add(curve)
                        newRenderedCurves.add(RenderedCurve(curve, fnItem.color, fnItem.id))
                    }
                } else {
                    updatedFunctions[index] = fnItem.copy(errorMessage = "Biểu thức không hợp lệ")
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

        _uiState.update {
            it.copy(
                functions = updatedFunctions,
                sampledCurves = newSampledCurves,
                renderedCurves = newRenderedCurves,
                intersections = intersections
            )
        }
    }

    fun applyPreset(preset: GraphPreset) {
        val currentIndex = _uiState.value.activeFunctionIndex
        if (currentIndex in _uiState.value.functions.indices) {
            updateFunctionExpression(currentIndex, preset.expression)
        } else {
            addFunction(preset.expression)
        }
    }

    fun getPresets(): List<GraphPreset> = presetRepository.getPresets()

    private fun restoreFromHistoryIfAvailable() {
        if (historyRepository == null) {
            isRestoringFromHistory = false
            recomputeCurves()
            return
        }
        viewModelScope.launch {
            try {
                val savedHistories = historyRepository.getLatestBySource(HistorySource.GRAPHING_CALCULATOR, limit = 5)
                if (savedHistories.isNotEmpty()) {
                    val chronological = savedHistories.reversed()
                    val restored = chronological.mapIndexed { index, entity ->
                        val color = entity.colorHex?.let { parseHexColor(it) }
                            ?: colorPalette[index % colorPalette.size]
                        FunctionItem(
                            expression = entity.expression,
                            color = color,
                            isVisible = entity.isVisible
                        )
                    }
                    val restoredViewport = savedHistories.firstOrNull()?.viewportBounds?.let { parseViewport(it) }
                    _uiState.update { state ->
                        state.copy(
                            functions = restored,
                            activeFunctionIndex = 0,
                            viewport = restoredViewport ?: state.viewport
                        )
                    }
                }
            } catch (_: Exception) {
                // Keep defaults if failed
            } finally {
                isRestoringFromHistory = false
                recomputeCurves()
            }
        }
    }

    fun persistActiveFunctions() {
        if (historyRepository == null || isRestoringFromHistory) return
        val currentFunctions = _uiState.value.functions
        val viewport = _uiState.value.viewport
        val viewportStr = "${viewport.minX},${viewport.maxX},${viewport.minY},${viewport.maxY}"
        viewModelScope.launch {
            try {
                historyRepository.clearBySource(HistorySource.GRAPHING_CALCULATOR)
                for (fn in currentFunctions) {
                    if (fn.expression.isNotBlank()) {
                        historyRepository.saveGraphing(
                            expression = fn.expression,
                            colorHex = fn.colorHex,
                            isVisible = fn.isVisible,
                            viewportBounds = viewportStr
                        )
                    }
                }
            } catch (_: Exception) {
                // Ignore failure
            }
        }
    }

    private fun parseHexColor(hex: String): Color {
        return try {
            val clean = hex.removePrefix("#")
            val colorLong = clean.toLong(16)
            if (clean.length <= 6) {
                Color(colorLong or 0xFF000000)
            } else {
                Color(colorLong)
            }
        } catch (_: Exception) {
            colorPalette[0]
        }
    }

    private fun parseViewport(boundsStr: String): ViewportBounds? {
        return try {
            val parts = boundsStr.split(",").map { it.trim().toDouble() }
            if (parts.size == 4) {
                ViewportBounds(minX = parts[0], maxX = parts[1], minY = parts[2], maxY = parts[3])
            } else null
        } catch (_: Exception) {
            null
        }
    }
}

package dhn.intern.smart_ai_caculator_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dhn.intern.smart_ai_caculator_app.domain.calculator.CalculatorEngine
import dhn.intern.smart_ai_caculator_app.data.repository.CalculatorHistoryRepository
import dhn.intern.smart_ai_caculator_app.util.handleParentheses
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalculatorViewModel(
    private val engine: CalculatorEngine,
    private val historyRepo: CalculatorHistoryRepository
) : ViewModel() {

    private val _expression = MutableStateFlow("")
    val expression = _expression.asStateFlow()

    private val _result = MutableStateFlow("0")
    val result = _result.asStateFlow()

    val history = historyRepo.getHistory()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )


    fun onKeyPress(key: String) {
        when (key) {
            "( )" -> _expression.value = handleParentheses(_expression.value)
            "⌫" -> {
                val expr = _expression.value
                if (expr.isNotEmpty()) {
                    _expression.value = expr.dropLast(1)
                }
            }
            "=" -> calculate()
            "AC" -> {
                _expression.value = ""
                _result.value = "0"
            }
            else -> _expression.value += key
        }
    }

    fun calculate() {
        val exp = _expression.value
        if (exp.isBlank()) return

        viewModelScope.launch {
            try {
                val resultText = withContext(Dispatchers.Default) {
                    engine.calculate(exp).getOrThrow().toString()
                }

                _result.value = resultText

                withContext(Dispatchers.IO) {
                    historyRepo.save(exp, resultText)
                }

            } catch (e: Exception) {
                _result.value = "Error"
            }
        }
    }

}


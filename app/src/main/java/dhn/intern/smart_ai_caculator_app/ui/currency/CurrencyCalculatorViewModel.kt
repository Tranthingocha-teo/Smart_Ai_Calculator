package dhn.intern.smart_ai_caculator_app.ui.currency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dhn.intern.smart_ai_caculator_app.data.constant.DefaultCurrencyRates
import dhn.intern.smart_ai_caculator_app.data.repository.CalculatorHistoryRepository
import dhn.intern.smart_ai_caculator_app.data.repository.CurrencyRepository
import dhn.intern.smart_ai_caculator_app.util.calculator.SmartFormatter
import dhn.intern.smart_ai_caculator_app.util.calculator.UnitConverterUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CurrencyUiState(
    val fromCurrency: String = "USD",
    val toCurrency: String = "EUR",
    val inputAmount: Double = 1.0,
    val convertedResult: String = "",
    val rates: Map<String, Double> = DefaultCurrencyRates.STATIC_RATES,
    val lastUpdatedText: String = "Dữ liệu ngoại tuyến",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CurrencyCalculatorViewModel(
    private val currencyRepository: CurrencyRepository,
    private val historyRepository: CalculatorHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    private var debounceJob: Job? = null
    private var lastSavedExpression: String? = null

    init {
        calculateConversion()
        loadCurrencyRates()
    }

    fun loadCurrencyRates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val rates = currencyRepository.getRates()
                val lastUpdated = currencyRepository.getLastUpdatedTimestamp()

                val timeText = if (lastUpdated != null) {
                    val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        .format(Date(lastUpdated))
                    "Cập nhật: $date"
                } else {
                    "Dữ liệu ngoại tuyến"
                }

                _uiState.update { state ->
                    state.copy(
                        rates = rates,
                        lastUpdatedText = timeText,
                        isLoading = false
                    )
                }
                calculateConversion()
            } catch (e: Exception) {
                android.util.Log.e("CURRENCY_API_ERROR", "Lỗi fetch: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage
                    )
                }
            }
        }
    }

    fun onAmountChanged(amount: Double) {
        _uiState.update { it.copy(inputAmount = amount) }
        calculateConversion()

        debounceJob?.cancel()
        if (amount <= 0.0) return

        debounceJob = viewModelScope.launch {
            delay(1500L)
            saveHistory()
        }
    }

    fun onFromCurrencyChanged(code: String) {
        _uiState.update { it.copy(fromCurrency = code) }
        calculateConversion()
    }

    fun onToCurrencyChanged(code: String) {
        _uiState.update { it.copy(toCurrency = code) }
        calculateConversion()
    }

    fun swapCurrencies() {
        debounceJob?.cancel()
        _uiState.update {
            it.copy(
                fromCurrency = it.toCurrency,
                toCurrency = it.fromCurrency
            )
        }
        calculateConversion()
        viewModelScope.launch {
            saveHistory()
        }
    }

    private suspend fun saveHistory() {
        val state = _uiState.value
        val expression = "${state.inputAmount} ${state.fromCurrency}"

        if (state.inputAmount > 0 && state.convertedResult.isNotBlank() && expression != lastSavedExpression) {
            lastSavedExpression = expression
            historyRepository.saveUnitConverter(
                expression = expression,
                result = state.convertedResult
            )
        }
    }

    private fun calculateConversion() {
        val state = _uiState.value
        if (state.rates.isEmpty()) return

        val rawResult = UnitConverterUtil.convertCurrency(
            value = state.inputAmount,
            fromCode = state.fromCurrency,
            toCode = state.toCurrency,
            ratesToUsd = state.rates
        )

        val formattedResult = SmartFormatter.formatCurrency(rawResult, state.toCurrency)
        _uiState.update { it.copy(convertedResult = formattedResult) }
    }
}

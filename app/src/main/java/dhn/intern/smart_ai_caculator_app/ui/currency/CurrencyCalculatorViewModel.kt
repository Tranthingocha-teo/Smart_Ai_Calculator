package dhn.intern.smart_ai_caculator_app.ui.currency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dhn.intern.smart_ai_caculator_app.data.constant.DefaultCurrencyRates
import dhn.intern.smart_ai_caculator_app.data.repository.CurrencyRepository
import dhn.intern.smart_ai_caculator_app.util.calculator.SmartFormatter
import dhn.intern.smart_ai_caculator_app.util.calculator.UnitConverterUtil
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
    // Khởi tạo sẵn bằng STATIC_RATES để UI luôn có dữ liệu tính toán ngay lập tức
    val rates: Map<String, Double> = DefaultCurrencyRates.STATIC_RATES,
    val lastUpdatedText: String = "Dữ liệu ngoại tuyến",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class CurrencyCalculatorViewModel(
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState: StateFlow<CurrencyUiState> = _uiState.asStateFlow()

    init {
        // Tính toán ngay 1 lần với STATIC_RATES ban đầu
        calculateConversion()
        // Sau đó mới fetch API để cập nhật đè lên
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
                // In log để biết chính xác Retrofit/Room bị lỗi gì
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
        _uiState.update {
            it.copy(
                fromCurrency = it.toCurrency,
                toCurrency = it.fromCurrency
            )
        }
        calculateConversion()
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

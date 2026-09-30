package dhn.intern.smart_ai_caculator_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dhn.intern.smart_ai_caculator_app.data.local.dao.CalculatorHistoryDao
import dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity
import dhn.intern.smart_ai_caculator_app.data.repository.CalculatorHistoryRepository
import dhn.intern.smart_ai_caculator_app.data.repository.CurrencyRepository
import dhn.intern.smart_ai_caculator_app.data.source.currencies.DefaultCurrencyRates
import dhn.intern.smart_ai_caculator_app.data.source.unit_calculator.UnitItemUI
import dhn.intern.smart_ai_caculator_app.data.source.unit_calculator.UnitUi
import dhn.intern.smart_ai_caculator_app.data.source.unit_calculator.unitsByCategory
import dhn.intern.smart_ai_caculator_app.enum.ActiveField
import dhn.intern.smart_ai_caculator_app.enum.HistorySource
import dhn.intern.smart_ai_caculator_app.enum.UnitCategory
import dhn.intern.smart_ai_caculator_app.ui.components.keypad.handleInput
import dhn.intern.smart_ai_caculator_app.util.calculator.SmartFormatter
import dhn.intern.smart_ai_caculator_app.util.calculator.UnitConverterUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UnitCalculatorViewModel(
    private val currencyRepository: CurrencyRepository,
    private val historyRepo: CalculatorHistoryRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel(){
    private val _uiState = MutableStateFlow(UnitCalculatorUiState())
    val uiState: StateFlow<UnitCalculatorUiState> = _uiState.asStateFlow()

    private var debounceJob: Job? = null

    init {
        loadCurrencyRates()
    }

    fun loadCurrencyRates() {
        viewModelScope.launch {
            try {
                val rates = currencyRepository.getRates()
                if (rates.isNotEmpty()) {
                    _uiState.update { it.copy(currencyRates = rates) }
                    recalculate(ActiveField.FROM)
                }
            } catch (_: Exception) {
                // Đã có FALLBACK_RATES khởi tạo mặc định trong state
            }
        }
    }

    fun selectCategory(tab: UnitUi, category: UnitCategory) {
        debounceJob?.cancel()
        val list = unitsByCategory(category)
        val defaultFrom = if (category == UnitCategory.CURRENCY) {
            list.firstOrNull { it.lable == "USD" } ?: list.firstOrNull()
        } else {
            list.firstOrNull { it.lable == "m" } ?: list.firstOrNull()
        }
        val defaultTo = if (category == UnitCategory.CURRENCY) {
            list.firstOrNull { it.lable == "EUR" } ?: list.getOrNull(1) ?: list.firstOrNull()
        } else {
            list.firstOrNull { it.lable == "cm" } ?: list.getOrNull(1) ?: list.firstOrNull()
        }

        _uiState.update {
            it.copy(
                unitTab = tab,
                currentCategory = category,
                units = list,
                fromUnit = defaultFrom,
                toUnit = defaultTo,
                fromValue = "1",
                activeField = ActiveField.FROM
            )
        }
        recalculate(ActiveField.FROM)
        saveHistoryImmediate()
    }

    fun onKeyPress(key: String) {
        if (key == "⇅") {
            onSwapUnits()
            return
        }

        val currentState = _uiState.value
        when (currentState.activeField) {
            ActiveField.FROM -> {
                val updated = handleInput(currentState.fromValue, key)
                _uiState.update { it.copy(fromValue = updated) }
                recalculate(ActiveField.FROM)
            }
            ActiveField.TO -> {
                val updated = handleInput(currentState.toValue, key)
                _uiState.update { it.copy(toValue = updated) }
                recalculate(ActiveField.TO)
            }
            else -> {} // Fix lỗi when tại onKeyPress
        }
        scheduleDebouncedSave()
    }

    fun onSwapUnits() {
        debounceJob?.cancel()
        val current = _uiState.value
        val newFromUnit = current.toUnit
        val newToUnit = current.fromUnit
        val newFromValue = current.toValue

        _uiState.update {
            it.copy(
                fromUnit = newFromUnit,
                toUnit = newToUnit,
                fromValue = newFromValue,
                activeField = ActiveField.FROM
            )
        }
        // Tính toán lại giá trị đích trước khi lưu lịch sử để tránh đảo ngược kết quả
        recalculate(ActiveField.FROM)
        saveHistoryImmediate()
    }

    fun onFocusChanged(field: ActiveField) {
        _uiState.update { it.copy(activeField = field) }
    }

    fun openPicker(field: ActiveField) {
        _uiState.update { it.copy(showUnitPicker = true, unitPickerFor = field) }
    }

    fun dismissPicker() {
        _uiState.update { it.copy(showUnitPicker = false, unitPickerFor = null) }
    }

    fun onUnitSelected(unit: UnitItemUI) {
        debounceJob?.cancel()
        val current = _uiState.value
        when (current.unitPickerFor) {
            ActiveField.FROM -> _uiState.update { it.copy(fromUnit = unit, showUnitPicker = false) }
            ActiveField.TO -> _uiState.update { it.copy(toUnit = unit, showUnitPicker = false) }
            else -> _uiState.update { it.copy(showUnitPicker = false) } // Fix lỗi when nullable
        }
        recalculate(ActiveField.FROM)
        saveHistoryImmediate()
    }

    private fun recalculate(sourceField: ActiveField) {
        val state = _uiState.value
        val sourceVal = if (sourceField == ActiveField.FROM) state.fromValue else state.toValue
        val num = sourceVal.toDoubleOrNull()

        if (num == null) {
            _uiState.update {
                if (sourceField == ActiveField.FROM) it.copy(toValue = "0") else it.copy(fromValue = "0")
            }
            return
        }

        val from = state.fromUnit ?: return
        val to = state.toUnit ?: return

        if (state.currentCategory == UnitCategory.CURRENCY) {
            val rates = if (state.currencyRates.isNotEmpty()) state.currencyRates else DefaultCurrencyRates.FALLBACK_RATES
            when (sourceField) {
                ActiveField.FROM -> {
                    val converted = UnitConverterUtil.convertCurrency(
                        value = num,
                        fromCode = from.lable.uppercase().trim(),
                        toCode = to.lable.uppercase().trim(),
                        ratesToUsd = rates
                    )
                    _uiState.update { it.copy(toValue = SmartFormatter.formatCurrency(converted, to.lable)) }
                }
                ActiveField.TO -> {
                    val converted = UnitConverterUtil.convertCurrency(
                        value = num,
                        fromCode = to.lable.uppercase().trim(),
                        toCode = from.lable.uppercase().trim(),
                        ratesToUsd = rates
                    )
                    _uiState.update { it.copy(fromValue = SmartFormatter.formatCurrency(converted, from.lable)) }
                }
                else -> {} // Fix lỗi when Currency
            }
        } else {
            when (sourceField) {
                ActiveField.FROM -> {
                    val converted = UnitConverterUtil.convert(
                        value = num,
                        fromUnitId = from.lable,
                        toUnitId = to.lable,
                        category = state.currentCategory
                    )
                    _uiState.update { it.copy(toValue = SmartFormatter.format(converted)) }
                }
                ActiveField.TO -> {
                    val converted = UnitConverterUtil.convert(
                        value = num,
                        fromUnitId = to.lable,
                        toUnitId = from.lable,
                        category = state.currentCategory
                    )
                    _uiState.update { it.copy(fromValue = SmartFormatter.format(converted)) }
                }
                else -> {} // Fix lỗi when thông thường
            }
        }
    }

    private fun scheduleDebouncedSave() {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(1500)
            saveHistoryImmediate()
        }
    }

    private fun saveHistoryImmediate() {
        val state = _uiState.value
        val sourceVal = if (state.activeField == ActiveField.FROM) state.fromValue else state.toValue
        val targetVal = if (state.activeField == ActiveField.FROM) state.toValue else state.fromValue
        val from = if (state.activeField == ActiveField.FROM) state.fromUnit else state.toUnit
        val to = if (state.activeField == ActiveField.FROM) state.toUnit else state.fromUnit

        if (from == null || to == null) return
        val num = sourceVal.toDoubleOrNull() ?: return
        if (num <= 0.0) return

        viewModelScope.launch(ioDispatcher) {
            historyRepo.saveUnitConverter(
                expression = "$sourceVal ${from.lable}",
                result = "$targetVal ${to.lable}"
            )
        }
    }
}

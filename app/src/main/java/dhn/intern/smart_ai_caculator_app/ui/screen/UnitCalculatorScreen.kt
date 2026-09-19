package dhn.intern.smart_ai_caculator_app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.data.local.dao.CalculatorHistoryDao
import dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity
import dhn.intern.smart_ai_caculator_app.data.repository.CurrencyRepository
import dhn.intern.smart_ai_caculator_app.data.source.currencies.DefaultCurrencyRates
import dhn.intern.smart_ai_caculator_app.data.source.unit_calculator.UnitData
import dhn.intern.smart_ai_caculator_app.data.source.unit_calculator.UnitItemUI
import dhn.intern.smart_ai_caculator_app.data.source.unit_calculator.unitsByCategory
import dhn.intern.smart_ai_caculator_app.enum.ActiveField
import dhn.intern.smart_ai_caculator_app.enum.HistorySource
import dhn.intern.smart_ai_caculator_app.enum.UnitCategory
import dhn.intern.smart_ai_caculator_app.ui.components.NavBar
import dhn.intern.smart_ai_caculator_app.ui.components.UnitPickerBottomSheet
import dhn.intern.smart_ai_caculator_app.ui.components.keypad.UnitKeypad
import dhn.intern.smart_ai_caculator_app.ui.components.keypad.handleInput
import dhn.intern.smart_ai_caculator_app.ui.components.unitCalculator.ItemsRowUnit
import dhn.intern.smart_ai_caculator_app.ui.components.unitCalculator.UnitInputField
import dhn.intern.smart_ai_caculator_app.util.calculator.SmartFormatter
import dhn.intern.smart_ai_caculator_app.util.calculator.UnitConverterUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource

@Composable
fun UnitCalculatorScreen(
    navController: NavHostController,
    historyDao: CalculatorHistoryDao = koinInject(),
    currencyRepository: CurrencyRepository = koinInject()
) {
    val coroutineScope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }
    var currencyRates by remember { mutableStateOf<Map<String, Double>>(DefaultCurrencyRates.FALLBACK_RATES) }

    // Tải tỷ giá hối đoái khi khởi động màn hình
    LaunchedEffect(Unit) {
        try {
            val rates = currencyRepository.getRates()
            if (rates.isNotEmpty()) {
                currencyRates = rates
            }
        } catch (_: Exception) {
            // Đã có FALLBACK_RATES dự phòng mặc định
        }
    }

    var showUnitPicker by remember { mutableStateOf(false) }
    val defaultTab = UnitData.getUnitData().firstOrNull { it.category == UnitCategory.LENGTH }
        ?: UnitData.getUnitData().first()
    var unitTab by remember { mutableStateOf(defaultTab) }
    var currentCategory by remember { mutableStateOf(UnitCategory.LENGTH) }

    var units by remember(currentCategory) { mutableStateOf(unitsByCategory(currentCategory)) }
    var fromUnit by remember(currentCategory) {
        mutableStateOf(
            if (currentCategory == UnitCategory.CURRENCY) {
                units.firstOrNull { it.lable == "USD" } ?: units.firstOrNull()
            } else {
                units.firstOrNull { it.lable == "m" } ?: units.firstOrNull()
            }
        )
    }
    var toUnit by remember(currentCategory) {
        mutableStateOf(
            if (currentCategory == UnitCategory.CURRENCY) {
                units.firstOrNull { it.lable == "EUR" } ?: units.getOrNull(1) ?: units.firstOrNull()
            } else {
                units.firstOrNull { it.lable == "cm" } ?: units.getOrNull(1) ?: units.firstOrNull()
            }
        )
    }

    var activeField by remember { mutableStateOf(ActiveField.FROM) }
    var unitPickerFor by remember { mutableStateOf<ActiveField?>(null) }

    var fromValue by remember { mutableStateOf("1") }
    var toValue by remember { mutableStateOf("0") }

    fun saveHistory(sourceVal: String, targetVal: String, from: UnitItemUI?, to: UnitItemUI?) {
        if (from == null || to == null) return
        val num = sourceVal.toDoubleOrNull() ?: return
        if (num <= 0.0) return

        coroutineScope.launch {
            val expression = "$sourceVal ${from.lable}"
            val result = "$targetVal ${to.lable}"
            historyDao.insert(
                CalculatorHistoryEntity(
                    expression = expression,
                    result = result,
                    source = HistorySource.UNIT_CONVERTER.name,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun triggerDebouncedSave() {
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(1500)
            if (activeField == ActiveField.FROM) {
                saveHistory(fromValue, toValue, fromUnit, toUnit)
            } else {
                saveHistory(toValue, fromValue, toUnit, fromUnit)
            }
        }
    }

    fun recalculate(sourceField: ActiveField, sourceValue: String) {
        val num = sourceValue.toDoubleOrNull()
        if (num == null) {
            when (sourceField) {
                ActiveField.FROM -> toValue = "0"
                ActiveField.TO -> fromValue = "0"
                else -> {}
            }
            return
        }

        if (fromUnit == null || toUnit == null) return

        if (currentCategory == UnitCategory.CURRENCY) {
            // Đảm bảo rates luôn có dữ liệu fallback nếu repository chưa trả về kịp
            val activeRates = if (currencyRates.isNotEmpty()) currencyRates else DefaultCurrencyRates.FALLBACK_RATES

            when (sourceField) {
                ActiveField.FROM -> {
                    val converted = UnitConverterUtil.convertCurrency(
                        value = num,
                        fromCode = fromUnit!!.lable.uppercase().trim(),
                        toCode = toUnit!!.lable.uppercase().trim(),
                        ratesToUsd = activeRates
                    )
                    toValue = SmartFormatter.formatCurrency(converted, toUnit!!.lable)
                }
                ActiveField.TO -> {
                    val converted = UnitConverterUtil.convertCurrency(
                        value = num,
                        fromCode = toUnit!!.lable.uppercase().trim(),
                        toCode = fromUnit!!.lable.uppercase().trim(),
                        ratesToUsd = activeRates
                    )
                    fromValue = SmartFormatter.formatCurrency(converted, fromUnit!!.lable)
                }
                else -> {}
            }
        } else {
            when (sourceField) {
                ActiveField.FROM -> {
                    val converted = UnitConverterUtil.convert(
                        value = num,
                        fromUnitId = fromUnit!!.lable,
                        toUnitId = toUnit!!.lable,
                        category = currentCategory
                    )
                    toValue = SmartFormatter.format(converted)
                }
                ActiveField.TO -> {
                    val converted = UnitConverterUtil.convert(
                        value = num,
                        fromUnitId = toUnit!!.lable,
                        toUnitId = fromUnit!!.lable,
                        category = currentCategory
                    )
                    fromValue = SmartFormatter.format(converted)
                }
                else -> {}
            }
        }
        triggerDebouncedSave()
    }

    // Tự động tính toán khi mở màn hình hoặc khi tỷ giá vừa được tải về
    LaunchedEffect(currencyRates) {
        recalculate(ActiveField.FROM, fromValue)
    }

    val fromLabel = if (fromUnit == null) {
        stringResource(R.string.unit_calculator_please_choose)
    } else {
        "${fromUnit!!.lable} (${fromUnit!!.des})"
    }
    val toLabel = if (toUnit == null) {
        stringResource(R.string.unit_calculator_please_choose)
    } else {
        "${toUnit!!.lable} (${toUnit!!.des})"
    }

    val statusBarTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.primary),
        topBar = {
            Box(modifier = Modifier.padding(top = statusBarTopPadding + 16.dp)) {
                NavBar(
                    navController = navController,
                    title = R.string.menu_unit_converter,
                    trailingContent = {
                        IconButton(
                            onClick = {
                                // Điều hướng sang màn hình lịch sử với nguồn UNIT_CONVERTER
                                navController.navigate("history_screen/${HistorySource.UNIT_CONVERTER.name}")
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.history),
                                contentDescription = "History",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopCenter)
            ) {
                ItemsRowUnit(
                    unitTab = unitTab,
                    onSelectUnitTab = { selectedUnitTab, selectedCategory ->
                        unitTab = selectedUnitTab
                        currentCategory = selectedCategory
                        units = unitsByCategory(selectedCategory)
                        if (selectedCategory == UnitCategory.CURRENCY) {
                            fromUnit = units.firstOrNull { it.lable == "USD" } ?: units.firstOrNull()
                            toUnit = units.firstOrNull { it.lable == "EUR" } ?: units.getOrNull(1) ?: units.firstOrNull()
                        } else {
                            fromUnit = units.firstOrNull()
                            toUnit = units.getOrNull(1) ?: units.firstOrNull()
                        }
                        fromValue = "1"
                        recalculate(ActiveField.FROM, "1")
                        saveHistory("1", toValue, fromUnit, toUnit)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                UnitInputField(
                    label = fromLabel,
                    value = fromValue,
                    iconRes = R.drawable.select_unit,
                    isActive = activeField == ActiveField.FROM,
                    onFocus = { activeField = ActiveField.FROM },
                    onClick = {
                        unitPickerFor = ActiveField.FROM
                        showUnitPicker = true
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                UnitInputField(
                    label = toLabel,
                    value = toValue,
                    iconRes = R.drawable.select_unit,
                    isActive = activeField == ActiveField.TO,
                    onFocus = { activeField = ActiveField.TO },
                    onClick = {
                        unitPickerFor = ActiveField.TO
                        showUnitPicker = true
                    }
                )
            }

            Box(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                UnitKeypad(
                    onKeyPress = { key ->
                        when (key) {
                            "⇅" -> {
                                val tmpValue = fromValue
                                fromValue = toValue
                                toValue = tmpValue

                                val tmpUnit = fromUnit
                                fromUnit = toUnit
                                toUnit = tmpUnit

                                saveHistory(fromValue, toValue, fromUnit, toUnit)
                            }

                            else -> {
                                when (activeField) {
                                    ActiveField.FROM -> {
                                        fromValue = handleInput(fromValue, key)
                                        recalculate(ActiveField.FROM, fromValue)
                                    }
                                    ActiveField.TO -> {
                                        toValue = handleInput(toValue, key)
                                        recalculate(ActiveField.TO, toValue)
                                    }
                                    else -> {
                                        fromValue = handleInput(fromValue, key)
                                        recalculate(ActiveField.FROM, fromValue)
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    if (showUnitPicker) {
        UnitPickerBottomSheet(
            title = R.string.unit_calculator_choose,
            units = units,
            selectedUnit = when (unitPickerFor) {
                ActiveField.FROM -> fromUnit
                ActiveField.TO -> toUnit
                else -> null
            },
            onSelect = { unit ->
                when (unitPickerFor) {
                    ActiveField.FROM -> {
                        fromUnit = unit
                        recalculate(ActiveField.FROM, fromValue)
                    }
                    ActiveField.TO -> {
                        toUnit = unit
                        recalculate(ActiveField.FROM, fromValue)
                    }
                    else -> {}
                }
                saveHistory(fromValue, toValue, fromUnit, toUnit)
                showUnitPicker = false
            },
            onDismiss = {
                showUnitPicker = false
            }
        )
    }
}

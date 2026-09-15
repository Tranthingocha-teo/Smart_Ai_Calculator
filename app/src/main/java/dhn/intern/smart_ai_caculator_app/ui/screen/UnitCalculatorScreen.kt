package dhn.intern.smart_ai_caculator_app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.data.source.unit_calculator.UnitData
import dhn.intern.smart_ai_caculator_app.data.source.unit_calculator.UnitItemUI
import dhn.intern.smart_ai_caculator_app.data.source.unit_calculator.unitsByCategory
import dhn.intern.smart_ai_caculator_app.enum.ActiveField
import dhn.intern.smart_ai_caculator_app.enum.UnitCategory
import dhn.intern.smart_ai_caculator_app.ui.components.NavBar
import dhn.intern.smart_ai_caculator_app.ui.components.UnitPickerBottomSheet
import dhn.intern.smart_ai_caculator_app.ui.components.keypad.UnitKeypad
import dhn.intern.smart_ai_caculator_app.ui.components.keypad.handleInput
import dhn.intern.smart_ai_caculator_app.ui.components.unitCalculator.ItemsRowUnit
import dhn.intern.smart_ai_caculator_app.ui.components.unitCalculator.UnitInputField
import dhn.intern.smart_ai_caculator_app.util.calculator.SmartFormatter
import dhn.intern.smart_ai_caculator_app.util.calculator.UnitConverterUtil
import dhn.intern.smart_ai_caculator_app.ui.currency.CurrencyConverterView

@Composable
fun UnitCalculatorScreen(
    navController: NavHostController
) {
    var showUnitPicker by remember { mutableStateOf(false) }
    val defaultTab = UnitData.getUnitData()
        .first { it.navHost == "length_unit_converter" }
    var unitTab by remember { mutableStateOf(defaultTab) }
    var currentCategory by remember { mutableStateOf(UnitCategory.LENGTH) }

    val units = unitsByCategory(currentCategory)
    var fromUnit by remember {
        mutableStateOf<UnitItemUI?>(
            units.firstOrNull { it.lable == "m" } ?: units.firstOrNull()
        )
    }
    var toUnit by remember {
        mutableStateOf<UnitItemUI?>(
            units.firstOrNull { it.lable == "cm" } ?: units.getOrNull(1) ?: units.firstOrNull()
        )
    }

    var activeField by remember { mutableStateOf(ActiveField.FROM) }
    var unitPickerFor by remember { mutableStateOf<ActiveField?>(null) }

    var fromValue by remember { mutableStateOf("1") }
    var toValue by remember {
        val initialConverted = if (fromUnit != null && toUnit != null) {
            SmartFormatter.format(
                UnitConverterUtil.convert(1.0, fromUnit!!.lable, toUnit!!.lable, currentCategory)
            )
        } else {
            "0"
        }
        mutableStateOf(initialConverted)
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

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary),
        topBar = {
            NavBar(
                navController = navController,
                title = R.string.menu_unit_converter,
            )
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
                // Thanh chọn Tab luôn hiển thị ở trên cùng
                ItemsRowUnit(
                    unitTab = unitTab,
                    onSelectUnitTab = { selectedUnitTab, selectedCategory ->
                        unitTab = selectedUnitTab
                        currentCategory = selectedCategory
                        if (selectedCategory != UnitCategory.CURRENCY) {
                            val categoryUnits = unitsByCategory(selectedCategory)
                            fromUnit = categoryUnits.firstOrNull()
                            toUnit = categoryUnits.getOrNull(1) ?: categoryUnits.firstOrNull()
                            fromValue = "1"
                            recalculate(ActiveField.FROM, "1")
                        }
                    }
                )

                // Rẽ nhánh: CURRENCY hiển thị giao diện tiền tệ, danh mục khác giữ nguyên
                if (currentCategory == UnitCategory.CURRENCY) {
                    CurrencyConverterView(
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Spacer(modifier = Modifier.height(15.dp))
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
                    Spacer(modifier = Modifier.height(20.dp))
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
            }

            // Bàn phím chỉ hiển thị cho các đơn vị đo thông thường
            if (currentCategory != UnitCategory.CURRENCY) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
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
                        recalculate(ActiveField.TO, toValue)
                    }
                    ActiveField.TO -> {
                        toUnit = unit
                        recalculate(ActiveField.FROM, fromValue)
                    }
                    else -> {}
                }
                showUnitPicker = false
            },
            onDismiss = {
                showUnitPicker = false
            }
        )
    }
}

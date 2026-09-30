package dhn.intern.smart_ai_caculator_app.ui.viewmodel

import dhn.intern.smart_ai_caculator_app.data.source.currencies.DefaultCurrencyRates
import dhn.intern.smart_ai_caculator_app.data.source.unit_calculator.UnitUi
import dhn.intern.smart_ai_caculator_app.data.source.unit_calculator.UnitData
import dhn.intern.smart_ai_caculator_app.data.source.unit_calculator.UnitItemUI
import dhn.intern.smart_ai_caculator_app.data.source.unit_calculator.unitsByCategory
import dhn.intern.smart_ai_caculator_app.enum.ActiveField
import dhn.intern.smart_ai_caculator_app.enum.UnitCategory

data class UnitCalculatorUiState(
    val currentCategory: UnitCategory = UnitCategory.LENGTH,
    val unitTab: UnitUi = UnitData.getUnitData().firstOrNull { it.category == UnitCategory.LENGTH }
        ?: UnitData.getUnitData().first(),
    val units: List<UnitItemUI> = unitsByCategory(UnitCategory.LENGTH),
    val fromUnit: UnitItemUI? = unitsByCategory(UnitCategory.LENGTH).firstOrNull { it.lable == "m" }
        ?: unitsByCategory(UnitCategory.LENGTH).firstOrNull(),
    val toUnit: UnitItemUI? = unitsByCategory(UnitCategory.LENGTH).firstOrNull { it.lable == "cm" }
        ?: unitsByCategory(UnitCategory.LENGTH).getOrNull(1),
    val activeField: ActiveField = ActiveField.FROM,
    val fromValue: String = "1",
    val toValue: String = "100",
    val currencyRates: Map<String, Double> = DefaultCurrencyRates.FALLBACK_RATES,
    val showUnitPicker: Boolean = false,
    val unitPickerFor: ActiveField? = null
)

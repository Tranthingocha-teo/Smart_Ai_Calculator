package dhn.intern.smart_ai_caculator_app.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.data.source.currencies.CurrenciesData
import dhn.intern.smart_ai_caculator_app.data.source.currencies.CurrenciesUi
import dhn.intern.smart_ai_caculator_app.enum.ActiveField
import dhn.intern.smart_ai_caculator_app.ui.components.CurrenciesPickerBottomSheet
import dhn.intern.smart_ai_caculator_app.ui.components.NavBar
import dhn.intern.smart_ai_caculator_app.ui.components.keypad.UnitKeypad
import dhn.intern.smart_ai_caculator_app.ui.components.keypad.handleInput
import dhn.intern.smart_ai_caculator_app.ui.components.unitCalculator.UnitInputField
import dhn.intern.smart_ai_caculator_app.ui.currency.CurrencyCalculatorViewModel
import dhn.intern.smart_ai_caculator_app.util.calculator.SmartFormatter
import dhn.intern.smart_ai_caculator_app.util.calculator.UnitConverterUtil
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ConverterScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: CurrencyCalculatorViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currencies = remember { CurrenciesData.getCurrenciesData() }

    var showUnitPicker by remember { mutableStateOf(false) }
    var fromCurrencies by remember {
        mutableStateOf<CurrenciesUi?>(currencies.firstOrNull { it.title.equals("EUR", ignoreCase = true) } ?: currencies.firstOrNull())
    }
    var toCurrencies by remember {
        mutableStateOf<CurrenciesUi?>(currencies.firstOrNull { it.title.equals("USD", ignoreCase = true) } ?: currencies.getOrNull(1))
    }

    var activeField by remember { mutableStateOf(ActiveField.FROM) }
    var currenciesPickerFor by remember { mutableStateOf<ActiveField?>(null) }

    var fromValue by remember { mutableStateOf("1") }
    var toValue by remember { mutableStateOf("0.0") }

    // Hàm quy đổi 2 chiều áp dụng quy tắc làm tròn chuyên biệt
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

        if (fromCurrencies == null || toCurrencies == null || uiState.rates.isEmpty()) return

        val fromCode = fromCurrencies!!.title.trim().uppercase()
        val toCode = toCurrencies!!.title.trim().uppercase()

        when (sourceField) {
            ActiveField.FROM -> {
                val converted = UnitConverterUtil.convertCurrency(
                    value = num,
                    fromCode = fromCode,
                    toCode = toCode,
                    ratesToUsd = uiState.rates
                )
                toValue = SmartFormatter.formatCurrency(converted, toCode)
            }
            ActiveField.TO -> {
                val converted = UnitConverterUtil.convertCurrency(
                    value = num,
                    fromCode = toCode,
                    toCode = fromCode,
                    ratesToUsd = uiState.rates
                )
                fromValue = SmartFormatter.formatCurrency(converted, fromCode)
            }
            else -> {}
        }
    }

    // Tự động tính toán khi tỷ giá vừa tải xong
    LaunchedEffect(uiState.rates, fromCurrencies, toCurrencies) {
        if (uiState.rates.isNotEmpty()) {
            recalculate(activeField, if (activeField == ActiveField.FROM) fromValue else toValue)
        }
    }

    // Chuỗi hiển thị tỷ giá trực tiếp (ví dụ: 1 EUR = 1.08 USD)
    val realTimeRateString = remember(uiState.rates, fromCurrencies, toCurrencies) {
        if (fromCurrencies != null && toCurrencies != null && uiState.rates.isNotEmpty()) {
            val fromCode = fromCurrencies!!.title.trim().uppercase()
            val toCode = toCurrencies!!.title.trim().uppercase()
            val oneUnitConverted = UnitConverterUtil.convertCurrency(1.0, fromCode, toCode, uiState.rates)
            "1 $fromCode = ${SmartFormatter.formatCurrency(oneUnitConverted, toCode)} $toCode"
        } else {
            "Đang cập nhật..."
        }
    }

    val fromLabel = if (fromCurrencies == null) {
        stringResource(R.string.currencies_please_choose)
    } else {
        "${fromCurrencies!!.image} ${fromCurrencies!!.title} (${fromCurrencies!!.des})"
    }
    val toLabel = if (toCurrencies == null) {
        stringResource(R.string.currencies_please_choose)
    } else {
        "${toCurrencies!!.image} ${toCurrencies!!.title} (${toCurrencies!!.des})"
    }

    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.primary),
        topBar = {
            NavBar(
                navController = navController,
                title = R.string.menu_currency_converter,
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                Spacer(modifier = Modifier.height(15.dp))
                UnitInputField(
                    label = fromLabel,
                    value = fromValue,
                    iconRes = R.drawable.select_unit,
                    isActive = activeField == ActiveField.FROM,
                    onFocus = { activeField = ActiveField.FROM },
                    onClick = {
                        currenciesPickerFor = ActiveField.FROM
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
                        currenciesPickerFor = ActiveField.TO
                        showUnitPicker = true
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    ConverterRealTime(
                        rateText = realTimeRateString,
                        updatedText = uiState.lastUpdatedText,
                        onRefresh = { viewModel.loadCurrencyRates() }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
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

                                val tmpUnit = fromCurrencies
                                fromCurrencies = toCurrencies
                                toCurrencies = tmpUnit

                                activeField = when (activeField) {
                                    ActiveField.FROM -> ActiveField.TO
                                    ActiveField.TO -> ActiveField.FROM
                                    else -> activeField
                                }
                                recalculate(activeField, if (activeField == ActiveField.FROM) fromValue else toValue)
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

                                    else -> {}
                                }
                            }
                        }
                    }
                )
            }
        }
    }
    if (showUnitPicker) {
        CurrenciesPickerBottomSheet(
            title = R.string.currencies_choose,
            units = currencies,
            selectedUnit = when (currenciesPickerFor) {
                ActiveField.FROM -> fromCurrencies
                ActiveField.TO -> toCurrencies
                else -> null
            },
            onSelect = { unit ->
                when (currenciesPickerFor) {
                    ActiveField.FROM -> {
                        fromCurrencies = unit
                        recalculate(ActiveField.TO, toValue)
                    }
                    ActiveField.TO -> {
                        toCurrencies = unit
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

@Composable
fun ConverterRealTime(
    rateText: String,
    updatedText: String,
    onRefresh: () -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.End
    ) {
        Text(
            text = rateText,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = updatedText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = onRefresh) {
                Icon(
                    painter = painterResource(R.drawable.rewrite),
                    contentDescription = "Refresh rates",
                    tint = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ConverterScreenPreview() {
    ConverterScreen(
        modifier = Modifier,
        navController = NavHostController(LocalContext.current)
    )
}

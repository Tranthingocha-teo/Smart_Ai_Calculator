package dhn.intern.smart_ai_caculator_app.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
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
    var currenciesPickerFor by remember { mutableStateOf<ActiveField?>(null) }

    var fromCurrencies by remember {
        mutableStateOf<CurrenciesUi?>(
            currencies.firstOrNull { it.title.equals("USD", ignoreCase = true) } ?: currencies.firstOrNull()
        )
    }
    var toCurrencies by remember {
        mutableStateOf<CurrenciesUi?>(
            currencies.firstOrNull { it.title.equals("EUR", ignoreCase = true) } ?: currencies.getOrNull(1)
        )
    }

    var fromValue by remember { mutableStateOf("1") }

    // Đồng bộ mã tiền ban đầu vào ViewModel
    LaunchedEffect(fromCurrencies, toCurrencies) {
        fromCurrencies?.title?.trim()?.uppercase()?.let { viewModel.onFromCurrencyChanged(it) }
        toCurrencies?.title?.trim()?.uppercase()?.let { viewModel.onToCurrencyChanged(it) }
    }

    // Kết quả lấy trực tiếp từ StateFlow trong ViewModel
    val toValue = uiState.convertedResult.ifEmpty { "0" }

    // Dòng thông tin tỷ giá thời gian thực: 1 USD = x EUR
    val realTimeRateString = remember(uiState.rates, uiState.fromCurrency, uiState.toCurrency) {
        if (uiState.rates.isNotEmpty()) {
            val oneUnitConverted = UnitConverterUtil.convertCurrency(
                value = 1.0,
                fromCode = uiState.fromCurrency,
                toCode = uiState.toCurrency,
                ratesToUsd = uiState.rates
            )
            "1 ${uiState.fromCurrency} = ${SmartFormatter.formatCurrency(oneUnitConverted, uiState.toCurrency)} ${uiState.toCurrency}"
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

    val statusBarTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.primary),
        topBar = {
            Box(modifier = Modifier.padding(top = statusBarTopPadding + 16.dp)) {
                NavBar(
                    navController = navController,
                    title = R.string.menu_currency_converter,
                )
            }
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
                Spacer(modifier = Modifier.height(16.dp))
                UnitInputField(
                    label = fromLabel,
                    value = fromValue,
                    iconRes = R.drawable.select_unit,
                    isActive = true,
                    onFocus = {},
                    onClick = {
                        currenciesPickerFor = ActiveField.FROM
                        showUnitPicker = true
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                UnitInputField(
                    label = toLabel,
                    value = toValue,
                    iconRes = R.drawable.select_unit,
                    isActive = false,
                    onFocus = {},
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
                                val tmpCurrencies = fromCurrencies
                                fromCurrencies = toCurrencies
                                toCurrencies = tmpCurrencies

                                fromValue = if (toValue == "0") "1" else toValue
                                viewModel.swapCurrencies()
                                val amount = fromValue.toDoubleOrNull() ?: 0.0
                                viewModel.onAmountChanged(amount)
                            }
                            else -> {
                                fromValue = handleInput(fromValue, key)
                                val amount = fromValue.toDoubleOrNull() ?: 0.0
                                viewModel.onAmountChanged(amount)
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
                        unit?.title?.trim()?.uppercase()?.let { viewModel.onFromCurrencyChanged(it) }
                    }
                    ActiveField.TO -> {
                        toCurrencies = unit
                        unit?.title?.trim()?.uppercase()?.let { viewModel.onToCurrencyChanged(it) }
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

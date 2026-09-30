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
import dhn.intern.smart_ai_caculator_app.data.source.unit_calculator.UnitData
import dhn.intern.smart_ai_caculator_app.data.source.unit_calculator.UnitItemUI
import dhn.intern.smart_ai_caculator_app.enum.ActiveField
import dhn.intern.smart_ai_caculator_app.enum.HistorySource
import dhn.intern.smart_ai_caculator_app.enum.UnitCategory
import dhn.intern.smart_ai_caculator_app.navigation.NavScreen
import dhn.intern.smart_ai_caculator_app.ui.components.CurrenciesPickerBottomSheet
import dhn.intern.smart_ai_caculator_app.ui.components.NavBar
import dhn.intern.smart_ai_caculator_app.ui.components.keypad.UnitKeypad
import dhn.intern.smart_ai_caculator_app.ui.components.unitCalculator.UnitInputField
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.UnitCalculatorViewModel
import dhn.intern.smart_ai_caculator_app.util.calculator.SmartFormatter
import dhn.intern.smart_ai_caculator_app.util.calculator.UnitConverterUtil
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ConverterScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: UnitCalculatorViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val currencies = remember { CurrenciesData.getCurrenciesData() }

    var showUnitPicker by remember { mutableStateOf(false) }
    var currenciesPickerFor by remember { mutableStateOf<ActiveField?>(null) }

    // Khởi tạo tab CURRENCY tự động khi vào màn hình Converter
    LaunchedEffect(Unit) {
        val currencyCategory = UnitData.getUnitData().firstOrNull { it.category == UnitCategory.CURRENCY }
            ?: UnitData.getUnitData().first()
        viewModel.selectCategory(currencyCategory, UnitCategory.CURRENCY)

        // Gán đơn vị mặc định để fromUnit và toUnit không bị null
        val defaultFrom = currencies.firstOrNull { it.title.equals("USD", ignoreCase = true) }
            ?: currencies.firstOrNull()
        val defaultTo = currencies.firstOrNull { it.title.equals("EUR", ignoreCase = true) }
            ?: currencies.getOrNull(1)

        defaultFrom?.let {
            viewModel.openPicker(ActiveField.FROM)
            viewModel.onUnitSelected(UnitItemUI(lable = it.title, des = it.des))
        }
        defaultTo?.let {
            viewModel.openPicker(ActiveField.TO)
            viewModel.onUnitSelected(UnitItemUI(lable = it.title, des = it.des))
        }
    }

    // Ghép đối tượng hiển thị (lá cờ + tên) từ danh sách CurrenciesUi tương ứng với mã tiền tệ trong ViewModel
    val fromCurrencies = currencies.firstOrNull { it.title.equals(uiState.fromUnit?.lable, ignoreCase = true) }
        ?: currencies.firstOrNull()
    val toCurrencies = currencies.firstOrNull { it.title.equals(uiState.toUnit?.lable, ignoreCase = true) }
        ?: currencies.getOrNull(1)

    val fromValue = uiState.fromValue
    val toValue = uiState.toValue.ifEmpty { "0" }

    // Dòng thông tin tỷ giá tự động: "1 USD = x.xx EUR" theo đúng cặp tiền đang chọn
    val fromCode = uiState.fromUnit?.lable ?: "USD"
    val toCode = uiState.toUnit?.lable ?: "EUR"
    val realTimeRateString = remember(uiState.currencyRates, fromCode, toCode) {
        if (uiState.currencyRates.isNotEmpty()) {
            val oneUnitConverted = UnitConverterUtil.convertCurrency(
                value = 1.0,
                fromCode = fromCode,
                toCode = toCode,
                ratesToUsd = uiState.currencyRates
            )
            "1 $fromCode = ${SmartFormatter.formatCurrency(oneUnitConverted, toCode)} $toCode"
        } else {
            "Đang cập nhật..."
        }
    }

    val fromLabel = if (fromCurrencies == null) {
        stringResource(R.string.currencies_please_choose)
    } else {
        "${fromCurrencies.image} ${fromCurrencies.title} (${fromCurrencies.des})"
    }
    val toLabel = if (toCurrencies == null) {
        stringResource(R.string.currencies_please_choose)
    } else {
        "${toCurrencies.image} ${toCurrencies.title} (${toCurrencies.des})"
    }

    val statusBarTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.primary),
        topBar = {
            Box(modifier = Modifier.padding(top = statusBarTopPadding + 16.dp)) {
                NavBar(
                    navController = navController,
                    title = R.string.menu_currency_converter,
                    trailingContent = {
                        IconButton(
                            onClick = {
                                navController.navigate(NavScreen.HistoryScreen.createRoute(HistorySource.UNIT_CONVERTER))
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
                    isActive = uiState.activeField == ActiveField.FROM,
                    onFocus = { viewModel.onFocusChanged(ActiveField.FROM) },
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
                    isActive = uiState.activeField == ActiveField.TO,
                    onFocus = { viewModel.onFocusChanged(ActiveField.TO) },
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
                        // Tái sử dụng hàm onKeyPress của UnitCalculatorViewModel
                        viewModel.onKeyPress(key)
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
                if (unit != null) {
                    val unitItemUI = UnitItemUI(lable = unit.title, des = unit.des)
                    when (currenciesPickerFor) {
                        ActiveField.FROM -> {
                            viewModel.openPicker(ActiveField.FROM)
                            viewModel.onUnitSelected(unitItemUI)
                        }
                        ActiveField.TO -> {
                            viewModel.openPicker(ActiveField.TO)
                            viewModel.onUnitSelected(unitItemUI)
                        }
                        else -> {}
                    }
                }
                showUnitPicker = false
            },
            onDismiss = {
                showUnitPicker = false
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ConverterRealTime(
    rateText: String = "1 USD = 87.33 INR",
    updatedAt: LocalDateTime? = LocalDateTime.now(),
    onRefresh: () -> Unit = {}
) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm, MMM dd, yyyy")
    val updatedLabel = updatedAt?.let { "Rates of ${it.format(formatter)}" } ?: ""

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
                text = updatedLabel,
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

package dhn.intern.smart_ai_caculator_app.ui.currency

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel

@Composable
fun CurrencyConverterView(
    modifier: Modifier = Modifier,
    viewModel: CurrencyCalculatorViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val availableCurrencies = remember(uiState.rates) {
        if (uiState.rates.isEmpty()) listOf("USD", "VND", "EUR", "JPY", "GBP")
        else uiState.rates.keys.sorted()
    }

    var amountText by remember { mutableStateOf("1") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Trạng thái cập nhật / Ngoại tuyến
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = uiState.lastUpdatedText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            }
        }

        // Card Tiền tệ Nguồn (From)
        CurrencyCard(
            label = "Từ",
            amount = amountText,
            selectedCurrency = uiState.fromCurrency,
            currencyList = availableCurrencies,
            isEditable = true,
            onAmountChange = { text ->
                amountText = text
                text.toDoubleOrNull()?.let { viewModel.onAmountChanged(it) }
            },
            onCurrencySelected = { viewModel.onFromCurrencyChanged(it) }
        )

        // Nút Hoán đổi (Swap)
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { viewModel.swapCurrencies() },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(50))
            ) {
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = "Hoán đổi",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        // Card Tiền tệ Đích (To - Kết quả)
        CurrencyCard(
            label = "Sang",
            amount = uiState.convertedResult.ifEmpty { "0" },
            selectedCurrency = uiState.toCurrency,
            currencyList = availableCurrencies,
            isEditable = false,
            onAmountChange = {},
            onCurrencySelected = { viewModel.onToCurrencyChanged(it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyCard(
    label: String,
    amount: String,
    selectedCurrency: String,
    currencyList: List<String>,
    isEditable: Boolean,
    onAmountChange: (String) -> Unit,
    onCurrencySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ô nhập số tiền / Hiển thị kết quả
                if (isEditable) {
                    TextField(
                        value = amount,
                        onValueChange = onAmountChange,
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                } else {
                    Text(
                        text = amount,
                        modifier = Modifier.weight(1f),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Dropdown chọn loại tiền
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.menuAnchor()
                    ) {
                        Text(text = selectedCurrency, fontWeight = FontWeight.Bold)
                    }
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        currencyList.forEach { code ->
                            DropdownMenuItem(
                                text = { Text(code) },
                                onClick = {
                                    onCurrencySelected(code)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

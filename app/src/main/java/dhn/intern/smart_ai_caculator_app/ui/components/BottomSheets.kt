package dhn.intern.smart_ai_caculator_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.data.source.currencies.CurrenciesUi
import dhn.intern.smart_ai_caculator_app.data.local.entity.CalculatorHistoryEntity
import dhn.intern.smart_ai_caculator_app.data.source.menu_home.MenuData
import dhn.intern.smart_ai_caculator_app.data.source.unit_calculator.UnitItemUI
import dhn.intern.smart_ai_caculator_app.data.model.MenuUI
import dhn.intern.smart_ai_caculator_app.ui.components.history.CaculatorHistory
import dhn.intern.smart_ai_caculator_app.util.toDateTimeString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalfScreenBottomSheet(
    show: Boolean,
    history: CalculatorHistoryEntity?,
    onDismiss: () -> Unit,
    onMenuItemClick: ((MenuUI, CalculatorHistoryEntity) -> Unit)? = null
) {
    val menuList = MenuData.getSettingHistoryData()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    if (show && history != null) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = history.timestamp.toDateTimeString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.close),
                            modifier = Modifier.size(14.dp),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                CaculatorHistory(
                    history = history,
                    onClick = {}
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    menuList.forEachIndexed { index, item ->
                        BottomItemRow(item) {
                            onMenuItemClick?.invoke(item, history)
                        }
                        if (index < menuList.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitPickerBottomSheet(
    title: Int,
    units: List<UnitItemUI>,
    selectedUnit: UnitItemUI?,
    onSelect: (UnitItemUI) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val popularSymbols = remember {
        setOf(
            "km", "m", "cm", "mi", "ft",
            "kg", "g", "lb", "oz",
            "m²", "km²", "ft²", "ha",
            "L", "mL", "m³", "gal",
            "H", "m", "s", "d",
            "GB", "MB", "KB", "B",
            "km/h", "m/s", "mph",
            "bar", "kPa", "psi", "atm",
            "kW", "W", "hp",
            "kWh", "J", "kcal",
            "°", "rad",
            "km/L", "mpg",
            "°C", "°F", "K",
            "USD", "EUR", "GBP", "JPY", "VND", "CNY"
        )
    }

    val trimmedQuery = query.trim()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            keyboardController?.hide()
                            onDismiss()
                        },
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = {
                    Text(
                        text = stringResource(R.string.currencies_search),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.search),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground.copy(0.6f)
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        Icon(
                            painter = painterResource(R.drawable.close),
                            contentDescription = "Clear search",
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { query = "" },
                            tint = MaterialTheme.colorScheme.onBackground.copy(0.6f)
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .background(MaterialTheme.colorScheme.background.copy(1f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (trimmedQuery.isEmpty()) {
                    val popularList = units.filter { popularSymbols.contains(it.lable) }
                    val remainingList = units.filterNot { popularSymbols.contains(it.lable) }

                    if (popularList.isNotEmpty()) {
                        item {
                            Text(
                                text = "Popular",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        items(popularList, key = { "pop_${it.lable}" }) { unit ->
                            UnitPickerItem(
                                unit = unit,
                                selected = unit == selectedUnit,
                                onClick = {
                                    keyboardController?.hide()
                                    onSelect(unit)
                                    onDismiss()
                                }
                            )
                        }
                    }

                    if (remainingList.isNotEmpty()) {
                        if (popularList.isNotEmpty()) {
                            item {
                                Text(
                                    text = "All Units",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                )
                            }
                        }
                        items(remainingList, key = { "all_${it.lable}" }) { unit ->
                            UnitPickerItem(
                                unit = unit,
                                selected = unit == selectedUnit,
                                onClick = {
                                    keyboardController?.hide()
                                    onSelect(unit)
                                    onDismiss()
                                }
                            )
                        }
                    }
                } else {
                    val filteredUnits = units.filter {
                        it.lable.contains(trimmedQuery, ignoreCase = true) ||
                                it.des.contains(trimmedQuery, ignoreCase = true) ||
                                getCurrencyCountryName(it.lable).contains(trimmedQuery, ignoreCase = true)
                    }

                    if (filteredUnits.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No units found",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            }
                        }
                    } else {
                        items(filteredUnits, key = { "search_${it.lable}" }) { unit ->
                            UnitPickerItem(
                                unit = unit,
                                selected = unit == selectedUnit,
                                onClick = {
                                    keyboardController?.hide()
                                    onSelect(unit)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getCurrencyFlagEmoji(code: String): String? {
    return when (code.uppercase()) {
        "USD" -> "\uD83C\uDDFA\uD83C\uDDF8"
        "EUR" -> "\uD83C\uDDEA\uD83C\uDDFA"
        "GBP" -> "\uD83C\uDDEC\uD83C\uDDE7"
        "JPY" -> "\uD83C\uDDEF\uD83C\uDDF5"
        "CNY" -> "\uD83C\uDDE8\uD83C\uDDF3"
        "VND" -> "\uD83C\uDDFB\uD83C\uDDF3"
        "KRW" -> "\uD83C\uDDF0\uD83C\uDDF7"
        "AUD" -> "\uD83C\uDDE6\uD83C\uDDFA"
        "CAD" -> "\uD83C\uDDE8\uD83C\uDDE6"
        "SGD" -> "\uD83C\uDDF8\uD83C\uDDEC"
        "CHF" -> "\uD83C\uDDE8\uD83C\uDDED"
        "THB" -> "\uD83C\uDDF9\uD83C\uDDF0"
        else -> null
    }
}

private fun getCurrencyCountryName(code: String): String {
    return when (code.uppercase()) {
        "USD" -> "United States US America Dollar"
        "EUR" -> "Europe Euro"
        "GBP" -> "United Kingdom UK Britain Pound"
        "JPY" -> "Japan Yen"
        "CNY" -> "China Yuan"
        "VND" -> "Vietnam Dong"
        "KRW" -> "South Korea Won"
        "AUD" -> "Australia Dollar"
        "CAD" -> "Canada Dollar"
        "SGD" -> "Singapore Dollar"
        "CHF" -> "Switzerland Franc"
        "THB" -> "Thailand Baht"
        else -> ""
    }
}

@Composable
fun UnitPickerItem(
    unit: UnitItemUI,
    selected: Boolean,
    onClick: () -> Unit
) {
    val flag = getCurrencyFlagEmoji(unit.lable)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected)
                    MaterialTheme.colorScheme.outline
                else
                    MaterialTheme.colorScheme.background.copy(1f)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (flag != null) {
            Text(
                text = flag,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = unit.lable,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = unit.des,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected)
                    Color.White.copy(alpha = 0.8f)
                else
                    MaterialTheme.colorScheme.onBackground.copy(0.6f)
            )
        }

        Icon(
            painter = painterResource(R.drawable.back),
            contentDescription = null,
            tint = if (selected) Color.White else MaterialTheme.colorScheme.onBackground.copy(0.4f)
        )
    }
}

@Composable
fun BottomItemRow(
    item: MenuUI,
    onClick: () -> Unit,
) {
    val (iconBgColor, iconTintColor, textColor) = when (item.code) {
        1 -> Triple(Color(0xFF3B82F6).copy(alpha = 0.12f), Color(0xFF3B82F6), MaterialTheme.colorScheme.onBackground)
        2 -> Triple(Color(0xFFF59E0B).copy(alpha = 0.12f), Color(0xFFF59E0B), MaterialTheme.colorScheme.onBackground)
        3 -> Triple(Color(0xFF10B981).copy(alpha = 0.12f), Color(0xFF10B981), MaterialTheme.colorScheme.onBackground)
        4 -> Triple(Color(0xFF8B5CF6).copy(alpha = 0.12f), Color(0xFF8B5CF6), MaterialTheme.colorScheme.onBackground)
        5 -> Triple(Color(0xFFEF4444).copy(alpha = 0.12f), Color(0xFFEF4444), Color(0xFFEF4444))
        else -> Triple(Color(0xFF0B57D0).copy(alpha = 0.12f), Color(0xFF0B57D0), MaterialTheme.colorScheme.onBackground)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = item.icon.toInt()),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconTintColor
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = stringResource(id = item.name),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 15.sp,
            fontWeight = if (item.code == 5) FontWeight.SemiBold else FontWeight.Medium,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(R.drawable.back),
            contentDescription = null,
            modifier = Modifier.size(12.dp),
            tint = if (item.code == 5) Color(0xFFEF4444).copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrenciesPickerBottomSheet(
    title: Int,
    units: List<CurrenciesUi>,
    selectedUnit: CurrenciesUi?,
    onSelect: (CurrenciesUi) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    val filteredUnits = units.filter {
        it.title.contains(query, ignoreCase = true) ||
                it.des.contains(query, ignoreCase = true)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor =  MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onDismiss() },
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = {
                    Text(
                        text = stringResource(R.string.currencies_search),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.6f)
                    )
                              },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.search),
                        contentDescription = null
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .background(MaterialTheme.colorScheme.background.copy(1f))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredUnits) { unit ->
                    CurrenciesPickerItem(
                        unit = unit,
                        selected = unit == selectedUnit,
                        onClick = {
                            onSelect(unit)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CurrenciesPickerItem(
    unit: CurrenciesUi,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (selected)
                    MaterialTheme.colorScheme.outline
                else
                    MaterialTheme.colorScheme.background.copy(1f)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = unit.image,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = unit.title,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = unit.des,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected)
                    Color.White.copy(alpha = 0.8f)
                else
                    MaterialTheme.colorScheme.onBackground.copy(0.6f)
            )
        }

        Icon(
            painter = painterResource(R.drawable.back),
            contentDescription = null,
            tint = if (selected) Color.White else MaterialTheme.colorScheme.onBackground.copy(0.4f)
        )
    }
}

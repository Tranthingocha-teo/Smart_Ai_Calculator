package dhn.intern.smart_ai_caculator_app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier =  Modifier
                        .background(MaterialTheme.colorScheme.background),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = history.timestamp.toDateTimeString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        painter = painterResource(id = R.drawable.close),
                        modifier = Modifier
                            .size(15.dp)
                            .clickable{
                                onDismiss()
                            },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                CaculatorHistory(
                    history = history,
                    onClick = {}
                )
                LazyColumn(
                    modifier = Modifier.wrapContentHeight(),
                    userScrollEnabled = false
                ) {
                    itemsIndexed(menuList) { index, item ->
                        BottomItemRow(item) {
                        }
                        if(index < menuList.lastIndex)
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        )
                    }
                }
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
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor =  MaterialTheme.colorScheme.primary,
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
                        .clickable { onDismiss() },
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(units) { unit ->
                    UnitPickerItem(
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
fun UnitPickerItem(
    unit: UnitItemUI,
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = item.icon.toInt()),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(id = item.name),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
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
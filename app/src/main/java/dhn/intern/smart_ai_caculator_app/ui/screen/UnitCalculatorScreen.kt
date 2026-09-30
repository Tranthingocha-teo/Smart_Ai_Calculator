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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.enum.ActiveField
import dhn.intern.smart_ai_caculator_app.enum.HistorySource
import dhn.intern.smart_ai_caculator_app.navigation.NavScreen
import dhn.intern.smart_ai_caculator_app.ui.components.NavBar
import dhn.intern.smart_ai_caculator_app.ui.components.UnitPickerBottomSheet
import dhn.intern.smart_ai_caculator_app.ui.components.keypad.UnitKeypad
import dhn.intern.smart_ai_caculator_app.ui.components.unitCalculator.ItemsRowUnit
import dhn.intern.smart_ai_caculator_app.ui.components.unitCalculator.UnitInputField
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.UnitCalculatorViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun UnitCalculatorScreen(
    navController: NavHostController,
    viewModel: UnitCalculatorViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val fromLabel = if (state.fromUnit == null) {
        stringResource(R.string.unit_calculator_please_choose)
    } else {
        "${state.fromUnit!!.lable} (${state.fromUnit!!.des})"
    }
    val toLabel = if (state.toUnit == null) {
        stringResource(R.string.unit_calculator_please_choose)
    } else {
        "${state.toUnit!!.lable} (${state.toUnit!!.des})"
    }

    val statusBarTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.primary),
        topBar = {
            Box(modifier = Modifier.padding(top = statusBarTopPadding + 16.dp)) {
                NavBar(
                    navController = navController,
                    title = R.string.menu_unit_converter,
                    // Trong UnitCalculatorScreen.kt, phần trailingContent của NavBar:
                    trailingContent = {
                        IconButton(
                            onClick = {
                                navController.navigate(
                                    NavScreen.HistoryScreen.createRoute(HistorySource.UNIT_CONVERTER)
                                )
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
                    unitTab = state.unitTab,
                    onSelectUnitTab = { selectedUnitTab, selectedCategory ->
                        viewModel.selectCategory(selectedUnitTab, selectedCategory)
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
                UnitInputField(
                    label = fromLabel,
                    value = state.fromValue,
                    iconRes = R.drawable.select_unit,
                    isActive = state.activeField == ActiveField.FROM,
                    onFocus = { viewModel.onFocusChanged(ActiveField.FROM) },
                    onClick = { viewModel.openPicker(ActiveField.FROM) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                UnitInputField(
                    label = toLabel,
                    value = state.toValue,
                    iconRes = R.drawable.select_unit,
                    isActive = state.activeField == ActiveField.TO,
                    onFocus = { viewModel.onFocusChanged(ActiveField.TO) },
                    onClick = { viewModel.openPicker(ActiveField.TO) }
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
                        viewModel.onKeyPress(key)
                    }
                )
            }
        }
    }

    if (state.showUnitPicker) {
        UnitPickerBottomSheet(
            title = R.string.unit_calculator_choose,
            units = state.units,
            selectedUnit = when (state.unitPickerFor) {
                ActiveField.FROM -> state.fromUnit
                ActiveField.TO -> state.toUnit
                else -> null
            },
            onSelect = { unit ->
                viewModel.onUnitSelected(unit)
            },
            onDismiss = {
                viewModel.dismissPicker()
            }
        )
    }
}

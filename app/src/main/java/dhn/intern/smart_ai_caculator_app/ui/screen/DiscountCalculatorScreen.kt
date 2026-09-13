package dhn.intern.smart_ai_caculator_app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.enum.ActiveField
import dhn.intern.smart_ai_caculator_app.ui.components.NavBar_basic
import dhn.intern.smart_ai_caculator_app.ui.components.discountCalculator.ResultOutput
import dhn.intern.smart_ai_caculator_app.ui.components.discountCalculator.SplitInput

@Composable
fun DiscountCalculatorScreen(
    modifier: Modifier,
    navController: NavHostController
) {
    var fromValue by remember { mutableStateOf("") }
    var toValue by remember { mutableStateOf("") }
    var nextValue by remember { mutableStateOf("") }


    var activeField by remember { mutableStateOf(ActiveField.FROM) }

    var showUnitPicker by remember { mutableStateOf(false) }
    var currenciesPickerFor by remember { mutableStateOf<ActiveField?>(null) }
    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary),
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            NavBar_basic(
                title = R.string.menu_discount_calculator,
                icon = R.drawable.autorenew,
                navController = navController,
                onclick = {

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.primary),
            ) {
            SplitInput(
                label = R.string.discount_calculator_split,
                value = fromValue,
                isActive = activeField == ActiveField.FROM,
                onFocus = { activeField = ActiveField.FROM },
                onClickUnit = {
                    currenciesPickerFor = ActiveField.FROM
                    showUnitPicker = true
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            SplitInput(
                label = R.string.discount_calculator_split,
                value = toValue,
                isActive = activeField == ActiveField.TO,
                onFocus = { activeField = ActiveField.TO },
                onClickUnit = {
                    currenciesPickerFor = ActiveField.TO
                    showUnitPicker = true
                }
            )
            Spacer(modifier = Modifier.height(20.dp))

            SplitInput(
                label = R.string.discount_calculator_split,
                value = nextValue,
                isActive = activeField == ActiveField.NEXT,
                onFocus = { activeField = ActiveField.NEXT },
                onClickUnit = {
                    currenciesPickerFor = ActiveField.NEXT
                    showUnitPicker = false
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ResultOutput(
                value = "24,500",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

package dhn.intern.smart_ai_caculator_app.ui.screen.bmi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.data.model.BmiInput
import dhn.intern.smart_ai_caculator_app.enum.ActiveField
import dhn.intern.smart_ai_caculator_app.ui.components.NavBar
import dhn.intern.smart_ai_caculator_app.ui.components.bmi.BmiCalculateButton
import dhn.intern.smart_ai_caculator_app.ui.components.bmi.BmiGenderSelector
import dhn.intern.smart_ai_caculator_app.ui.components.bmi.BmiHeightInput
import dhn.intern.smart_ai_caculator_app.ui.components.bmi.BmiInputField
import dhn.intern.smart_ai_caculator_app.ui.components.keypad.UnitKeypad
import dhn.intern.smart_ai_caculator_app.ui.components.keypad.handleInput
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.BmiViewModel
import dhn.intern.smart_ai_caculator_app.navigation.NavScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun BMIScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: BmiViewModel = koinViewModel()
) {
    var activeField by remember { mutableStateOf<ActiveField?>(null) }

    var weightValue by remember { mutableStateOf("90.00") }
    var heightFtValue by remember { mutableStateOf("5") }
    var heightInValue by remember { mutableStateOf("6") }
    var ageValue by remember { mutableStateOf("27") }
    var selectedGender by remember { mutableStateOf("Male") }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        topBar = {
            NavBar(
                navController = navController,
                title = R.string.menu_bmi_calculator,
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = if (activeField != null) 380.dp else 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                BmiInputField(
                    label = R.string.bmi_weight,
                    value = weightValue,
                    isActive = activeField == ActiveField.WEIGHT,
                    onFocus = { activeField = ActiveField.WEIGHT }
                )

                BmiHeightInput(
                    label = R.string.bmi_height,
                    valueFt = heightFtValue,
                    valueIn = heightInValue,
                    isActiveFt = activeField == ActiveField.HEIGHT_FT,
                    isActiveIn = activeField == ActiveField.HEIGHT_IN,
                    onFocusFt = { activeField = ActiveField.HEIGHT_FT },
                    onFocusIn = { activeField = ActiveField.HEIGHT_IN }
                )

                BmiInputField(
                    label = R.string.bmi_age,
                    value = ageValue,
                    isActive = activeField == ActiveField.AGE,
                    onFocus = { activeField = ActiveField.AGE }
                )

                BmiGenderSelector(
                    label = R.string.bmi_gender,
                    selectedGender = selectedGender,
                    onGenderSelected = { selectedGender = it }
                )

                BmiCalculateButton(
                    text = R.string.bmi_calculate,
                    onClick = {
                        val input = BmiInput(
                            weightKg = weightValue.toFloatOrNull() ?: 0f,
                            heightFt = heightFtValue.toIntOrNull() ?: 0,
                            heightIn = heightInValue.toIntOrNull() ?: 0,
                            gender = selectedGender,
                            age = ageValue.toIntOrNull() ?: 0
                        )
                        viewModel.submitInput(input)
                        val calculatedBmi = viewModel.bmi

                        // Navigate to result screen with all data
                        navController.navigate(
                            NavScreen.ResultBMIScreen.createRoute(
                                weight = input.weightKg,
                                heightFt = input.heightFt,
                                heightIn = input.heightIn,
                                age = input.age,
                                gender = input.gender,
                                bmi = calculatedBmi
                            )
                        )
                        activeField = null
                    }
                )


                Spacer(modifier = Modifier.height(16.dp))
            }

            if (activeField != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(16.dp)
                ) {
                    UnitKeypad(
                        onKeyPress = { key ->
                            when (activeField) {
                                ActiveField.WEIGHT -> {
                                    weightValue = handleInput(weightValue, key)
                                }
                                ActiveField.HEIGHT_FT -> {
                                    heightFtValue = handleInput(heightFtValue, key)
                                }
                                ActiveField.HEIGHT_IN -> {
                                    heightInValue = handleInput(heightInValue, key)
                                }
                                ActiveField.AGE -> {
                                    ageValue = handleInput(ageValue, key)
                                }
                                else -> {}
                            }
                        }
                    )
                }
            }
        }
    }
}



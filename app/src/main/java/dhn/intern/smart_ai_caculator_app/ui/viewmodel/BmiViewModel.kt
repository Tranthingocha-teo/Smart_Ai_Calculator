package dhn.intern.smart_ai_caculator_app.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dhn.intern.smart_ai_caculator_app.data.model.BmiInput

class BmiViewModel : ViewModel() {

    var input by mutableStateOf<BmiInput?>(null)
        private set

    var bmi by mutableStateOf(0f)
        private set

    fun submitInput(input: BmiInput) {
        this.input = input
        this.bmi = calculateBmi(input)
    }

    private fun calculateBmi(input: BmiInput): Float {
        val heightMeter =
            ((input.heightFt * 12 + input.heightIn) * 2.54f) / 100f
        return input.weightKg / (heightMeter * heightMeter)
    }
}

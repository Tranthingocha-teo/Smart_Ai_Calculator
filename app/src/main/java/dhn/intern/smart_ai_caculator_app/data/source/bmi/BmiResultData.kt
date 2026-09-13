package dhn.intern.smart_ai_caculator_app.data.source.bmi

import androidx.compose.ui.graphics.Color

object BmiResultData {
    fun getBmiResults(bmi: Float): List<BmiResultUi> {
        return listOf(
            BmiResultUi("Very severely underweight", "<16", Color(0xFF006CF2), bmi < 16),
            BmiResultUi("Severely underweight", "16.0-16.9", Color(0xFF3EB1FF), bmi in 16f..16.9f),
            BmiResultUi("Underweight", "17.0-18.4", Color(0xFF6ED6FF), bmi in 17f..18.4f),
            BmiResultUi("Normal", "18.5-24.9", Color(0xFF7BCF00), bmi in 18.5f..24.9f),
            BmiResultUi("Overweight", "25.0-29.9", Color(0xFFFFC107), bmi in 25f..29.9f),
            BmiResultUi("Obese Class I", "30.0-34.9", Color(0xFFFF9800), bmi in 30f..34.9f),
            BmiResultUi("Obese Class II", "35.0-39.9", Color(0xFFFF5722), bmi in 35f..39.9f),
            BmiResultUi("Obese Class III", ">40", Color.Red, bmi >= 40)
        )
    }
}
package dhn.intern.smart_ai_caculator_app.domain.bmi

fun bmiToAngle(bmi: Float): Float {
    val minBmi = 0f
    val maxBmi = 50f

    val clamped = bmi.coerceIn(minBmi, maxBmi)

    return 180f - (clamped / maxBmi) * 180f
}



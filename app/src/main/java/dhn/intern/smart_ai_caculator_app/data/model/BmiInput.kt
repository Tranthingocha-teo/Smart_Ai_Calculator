package dhn.intern.smart_ai_caculator_app.data.model

data class BmiInput(
    val weightKg: Float,
    val heightFt: Int,
    val heightIn: Int,
    val gender: String,
    val age: Int
)


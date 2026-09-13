package dhn.intern.smart_ai_caculator_app.data.repository

data class GraphPreset(
    val id: String,
    val title: String,
    val expression: String,
    val category: PresetCategory,
    val description: String = ""
)

enum class PresetCategory {
    POLYNOMIAL,
    TRIGONOMETRIC,
    RATIONAL,
    EXPONENTIAL_LOGARITHMIC
}

class GraphPresetRepository {

    fun getPresets(): List<GraphPreset> = PRESETS

    fun getPresetsByCategory(category: PresetCategory): List<GraphPreset> =
        PRESETS.filter { it.category == category }

    fun getPresetById(id: String): GraphPreset? =
        PRESETS.find { it.id == id }

    companion object {
        private val PRESETS = listOf(
            GraphPreset(
                id = "linear",
                title = "Đường thẳng (Linear)",
                expression = "2x + 1",
                category = PresetCategory.POLYNOMIAL,
                description = "y = 2x + 1"
            ),
            GraphPreset(
                id = "quadratic",
                title = "Parabol bậc 2 (Quadratic)",
                expression = "x^2 - 4",
                category = PresetCategory.POLYNOMIAL,
                description = "y = x² - 4"
            ),
            GraphPreset(
                id = "cubic",
                title = "Đồ thị bậc 3 (Cubic)",
                expression = "x^3 - 3x",
                category = PresetCategory.POLYNOMIAL,
                description = "y = x³ - 3x"
            ),
            GraphPreset(
                id = "sine",
                title = "Hàm lượng giác Sin",
                expression = "sin(x)",
                category = PresetCategory.TRIGONOMETRIC,
                description = "y = sin(x)"
            ),
            GraphPreset(
                id = "cosine",
                title = "Hàm lượng giác Cos",
                expression = "cos(x)",
                category = PresetCategory.TRIGONOMETRIC,
                description = "y = cos(x)"
            ),
            GraphPreset(
                id = "rational",
                title = "Hypebol phân thức",
                expression = "1/x",
                category = PresetCategory.RATIONAL,
                description = "y = 1/x"
            ),
            GraphPreset(
                id = "exponential",
                title = "Hàm số mũ",
                expression = "e^x",
                category = PresetCategory.EXPONENTIAL_LOGARITHMIC,
                description = "y = e^x"
            ),
            GraphPreset(
                id = "logarithmic",
                title = "Hàm logarit tự nhiên",
                expression = "ln(x)",
                category = PresetCategory.EXPONENTIAL_LOGARITHMIC,
                description = "y = ln(x)"
            )
        )
    }
}

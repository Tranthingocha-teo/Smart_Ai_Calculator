package dhn.intern.smart_ai_caculator_app.data.source.unit_calculator

import dhn.intern.smart_ai_caculator_app.enum.UnitCategory

data class UnitUi(
    val image: Int? = null,
    val iconText: String? = null,
    val title: Int,
    val navHost: String,
    val category: UnitCategory
)

package dhn.intern.smart_ai_caculator_app.data.source.unit_calculator

import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.enum.UnitCategory

object UnitData {
    fun getUnitData(): List<UnitUi> {
        return listOf(
            UnitUi(
                image = R.drawable.length,
                iconText = null,
                title = R.string.unit_calculator_length,
                navHost = "length_unit_converter",
                category = UnitCategory.LENGTH
            ),
            UnitUi(
                image = null,
                iconText = "⚖\uFE0F",
                title = R.string.unit_calculator_mass,
                navHost = "mass_unit_converter",
                category = UnitCategory.MASS
            ),
            UnitUi(
                image = null,
                iconText = "\uD83C\uDFDD\uFE0F",
                title = R.string.unit_calculator_area,
                navHost = "area_unit_converter",
                category = UnitCategory.AREA
            ),
            UnitUi(
                image = R.drawable.volume,
                iconText = null,
                title = R.string.unit_calculator_volume,
                navHost = "volume_unit_converter",
                category = UnitCategory.VOLUME
            ),
            UnitUi(
                image = null,
                iconText = "⏳",
                title = R.string.unit_calculator_time,
                navHost = "time_unit_converter",
                category = UnitCategory.TIME
            ),
            UnitUi(
                image = R.drawable.data,
                iconText = null,
                title = R.string.unit_calculator_data,
                navHost = "data_unit_converter",
                category = UnitCategory.DATA
            ),
        )
    }
}
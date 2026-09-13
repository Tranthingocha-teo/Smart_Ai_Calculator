package dhn.intern.smart_ai_caculator_app.data.source.unit_calculator

import dhn.intern.smart_ai_caculator_app.enum.UnitCategory


fun UnitUi.toCategory(): UnitCategory = when (navHost) {
    "length_unit_converter" -> UnitCategory.LENGTH
    "mass_unit_converter" -> UnitCategory.MASS
    "area_unit_converter" -> UnitCategory.AREA
    "volume_unit_converter" -> UnitCategory.VOLUME
    "time_unit_converter" -> UnitCategory.TIME
    "data_unit_converter" -> UnitCategory.DATA
    else -> UnitCategory.LENGTH
}

package dhn.intern.smart_ai_caculator_app.util.calculator

import dhn.intern.smart_ai_caculator_app.enum.UnitCategory

object UnitConverterUtil {

    // Ratio to base unit for each category
    private val lengthRatios = mapOf(
        "m" to 1.0,
        "km" to 1000.0,
        "cm" to 0.01,
        "mm" to 0.001,
        "µm" to 1e-6,
        "nm" to 1e-9,
        "in" to 0.0254,
        "ft" to 0.3048,
        "yd" to 0.9144,
        "mi" to 1609.344,
        "nmi" to 1852.0
    )

    private val massRatios = mapOf(
        "kg" to 1.0,
        "g" to 0.001,
        "mg" to 1e-6,
        "t" to 1000.0,
        "lb" to 0.45359237,
        "oz" to 0.028349523125,
        "ct" to 0.0002
    )

    private val areaRatios = mapOf(
        "m²" to 1.0,
        "km²" to 1e6,
        "cm²" to 1e-4,
        "mm²" to 1e-6,
        "ha" to 10000.0,
        "ac" to 4046.8564224,
        "in²" to 0.00064516,
        "ft²" to 0.09290304,
        "yd²" to 0.83612736,
        "mi²" to 2589988.110336
    )

    private val volumeRatios = mapOf(
        "L" to 1.0,
        "mL" to 0.001,
        "cL" to 0.01,
        "dL" to 0.1,
        "hL" to 100.0,
        "kL" to 1000.0,
        "m³" to 1000.0,
        "cm³" to 0.001,
        "gal" to 3.785411784,
        "qt" to 0.946352946,
        "pt" to 0.473176473,
        "cup" to 0.2365882365,
        "fl oz" to 0.0295735295625
    )

    private val timeRatios = mapOf(
        "s" to 1.0,
        "ms" to 0.001,
        "µs" to 1e-6,
        "ns" to 1e-9,
        "m" to 60.0,
        "min" to 60.0,
        "H" to 3600.0,
        "h" to 3600.0,
        "d" to 86400.0,
        "wk" to 604800.0,
        "mo" to 2629746.0,
        "yr" to 31556952.0
    )

    private val dataRatios = mapOf(
        "B" to 1.0,
        "b" to 0.125,
        "KB" to 1024.0,
        "Kb" to 128.0,
        "MB" to 1024.0 * 1024.0,
        "Mb" to 131072.0,
        "GB" to 1024.0 * 1024.0 * 1024.0,
        "Gb" to 134217728.0,
        "TB" to 1024.0 * 1024.0 * 1024.0 * 1024.0
    )

    private val speedRatios = mapOf(
        "m/s" to 1.0,
        "km/h" to (1.0 / 3.6),
        "mph" to 0.44704,
        "knot" to 0.5144444444,
        "ft/s" to 0.3048
    )

    private val pressureRatios = mapOf(
        "Pa" to 1.0,
        "kPa" to 1000.0,
        "bar" to 100000.0,
        "psi" to 6894.757,
        "atm" to 101325.0,
        "mmHg" to 133.322
    )

    private val powerRatios = mapOf(
        "W" to 1.0,
        "kW" to 1000.0,
        "mW" to 0.001,
        "hp" to 745.699872
    )

    private val energyRatios = mapOf(
        "J" to 1.0,
        "kJ" to 1000.0,
        "cal" to 4.184,
        "kcal" to 4184.0,
        "Wh" to 3600.0,
        "kWh" to 3600000.0
    )

    private val angleRatios = mapOf(
        "°" to 1.0,
        "rad" to (180.0 / Math.PI),
        "grad" to 0.9
    )

    private val fuelRatios = mapOf(
        "km/L" to 1.0,
        "mpg" to 0.425144
    )

    /**
     * Converts a numeric value between two units of the same category.
     */
    fun convert(
        value: Double,
        fromUnitId: String,
        toUnitId: String,
        category: UnitCategory
    ): Double {
        if (value == 0.0) return 0.0
        if (fromUnitId == toUnitId) return value

        val ratioMap = when (category) {
            UnitCategory.LENGTH -> lengthRatios
            UnitCategory.MASS -> massRatios
            UnitCategory.AREA -> areaRatios
            UnitCategory.VOLUME -> volumeRatios
            UnitCategory.TIME -> timeRatios
            UnitCategory.DATA -> dataRatios
            UnitCategory.SPEED -> speedRatios
            UnitCategory.PRESSURE -> pressureRatios
            UnitCategory.POWER -> powerRatios
            UnitCategory.ENERGY -> energyRatios
            UnitCategory.ANGLE -> angleRatios
            UnitCategory.FUEL -> fuelRatios
            UnitCategory.TEMPERATURE -> emptyMap() // Handled in #30
        }

        val fromRatio = ratioMap[fromUnitId] ?: return value
        val toRatio = ratioMap[toUnitId] ?: return value

        // Convert to base unit, then to target unit
        val valueInBase = value * fromRatio
        return valueInBase / toRatio
    }
}

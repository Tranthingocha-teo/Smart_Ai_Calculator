package dhn.intern.smart_ai_caculator_app.data.source.unit_calculator

import dhn.intern.smart_ai_caculator_app.enum.UnitCategory

fun unitsByCategory(category: UnitCategory): List<UnitItemUI> =
    when (category) {
        UnitCategory.LENGTH -> listOf(
            UnitItemUI("cm", "Centimeter"),
            UnitItemUI("in", "Inch"),
            UnitItemUI("m", "Meter"),
            UnitItemUI("km", "Kilometer"),
            UnitItemUI("mi", "Mile"),
            UnitItemUI("ft", "Foot"),
            UnitItemUI("yd", "Yard"),
        )

        UnitCategory.MASS -> listOf(
            UnitItemUI("kg", "Kilogram"),
            UnitItemUI("lb", "Pound"),
            UnitItemUI("mg", "Milligram"),
            UnitItemUI("g", "Gram"),
            UnitItemUI("oz", "Ounce"),
            UnitItemUI("t", "Ton")
        )

        UnitCategory.AREA -> listOf(
            UnitItemUI("m²", "Square meter"),
            UnitItemUI("in²", "Square inch"),
            UnitItemUI("ft²", "Square foot"),
            UnitItemUI("mi²", "Square mile"),
            UnitItemUI("yd²", "Square yard"),
            UnitItemUI("mm²", "Square millimeter"),
            UnitItemUI("cm²", "Square centimeter"),
        )
        UnitCategory.VOLUME -> listOf(
            UnitItemUI("L", "Liter"),
            UnitItemUI("mL", "Milliliter"),
            UnitItemUI("cL", "Centiliter"),
            UnitItemUI("dL", "Deciliter"),
            UnitItemUI("hL", "Hectoliter"),
            UnitItemUI("kL", "Kiloliter"),
            UnitItemUI("m³", "Cubic meter"),
            UnitItemUI("cm³", "Cubic centimeter"),
            UnitItemUI("gal", "Gallon (US)"),
            UnitItemUI("qt", "Quart (US)"),
            UnitItemUI("pt", "Pint (US)"),
            UnitItemUI("cup", "Cup (US)"),
            UnitItemUI("fl oz", "Fluid ounce (US)")
        )
        UnitCategory.TIME -> listOf(
            UnitItemUI("H", "Hour"),
            UnitItemUI("s", "Second"),
            UnitItemUI("ms", "millisecond"),
            UnitItemUI("µs", "Microsecond"),
            UnitItemUI("ns", "Nanosecond"),
            UnitItemUI("m", "Minute"),
            UnitItemUI("d", "Day")
        )
        UnitCategory.DATA -> listOf(
            UnitItemUI("GB", "Gigabyte"),
            UnitItemUI("MB", "Megabyte"),
            UnitItemUI("B", "Byte"),
            UnitItemUI("KB", "Kilobyte"),
            UnitItemUI("TB", "Terabyte"),
            UnitItemUI("b", "Bit"),
            UnitItemUI("Kb", "Kilobit")
        )
        UnitCategory.SPEED -> listOf(
            UnitItemUI("m/s", "Meter per second"),
            UnitItemUI("km/h", "Kilometer per hour"),
            UnitItemUI("mph", "Mile per hour"),
            UnitItemUI("knot", "Knot"),
            UnitItemUI("ft/s", "Foot per second")
        )
        UnitCategory.PRESSURE -> listOf(
            UnitItemUI("Pa", "Pascal"),
            UnitItemUI("kPa", "Kilopascal"),
            UnitItemUI("bar", "Bar"),
            UnitItemUI("psi", "Pound per square inch"),
            UnitItemUI("atm", "Standard atmosphere"),
            UnitItemUI("mmHg", "Millimeter of mercury")
        )
        UnitCategory.POWER -> listOf(
            UnitItemUI("W", "Watt"),
            UnitItemUI("kW", "Kilowatt"),
            UnitItemUI("mW", "Milliwatt"),
            UnitItemUI("hp", "Horsepower")
        )
        UnitCategory.ENERGY -> listOf(
            UnitItemUI("J", "Joule"),
            UnitItemUI("kJ", "Kilojoule"),
            UnitItemUI("cal", "Calorie"),
            UnitItemUI("kcal", "Kilocalorie"),
            UnitItemUI("Wh", "Watt-hour"),
            UnitItemUI("kWh", "Kilowatt-hour")
        )
        UnitCategory.ANGLE -> listOf(
            UnitItemUI("°", "Degree"),
            UnitItemUI("rad", "Radian"),
            UnitItemUI("grad", "Gradian")
        )
        UnitCategory.FUEL -> listOf(
            UnitItemUI("km/L", "Kilometer per liter"),
            UnitItemUI("mpg", "Miles per gallon")
        )
        UnitCategory.TEMPERATURE -> listOf(
            UnitItemUI("°C", "Celsius"),
            UnitItemUI("°F", "Fahrenheit"),
            UnitItemUI("K", "Kelvin")
        )
    }


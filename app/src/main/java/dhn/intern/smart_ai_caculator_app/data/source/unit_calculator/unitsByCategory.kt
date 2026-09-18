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
            UnitItemUI("kL", "Kiliter"),
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
            UnitItemUI("ms", "Millisecond"),
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
        // Cập nhật danh sách tiền tệ toàn cầu cho Ticket #31
        UnitCategory.CURRENCY -> listOf(
            UnitItemUI("USD", "United States Dollar"),
            UnitItemUI("EUR", "Euro"),
            UnitItemUI("VND", "Vietnamese Dong"),
            UnitItemUI("JPY", "Japanese Yen"),
            UnitItemUI("GBP", "British Pound"),
            UnitItemUI("AUD", "Australian Dollar"),
            UnitItemUI("CAD", "Canadian Dollar"),
            UnitItemUI("CHF", "Swiss Franc"),
            UnitItemUI("CNY", "Chinese Yuan"),
            UnitItemUI("KRW", "South Korean Won"),
            UnitItemUI("SGD", "Singapore Dollar"),
            UnitItemUI("THB", "Thai Baht")
        )
    }

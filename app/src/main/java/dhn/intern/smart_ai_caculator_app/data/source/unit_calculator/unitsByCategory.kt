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
            UnitItemUI("mL", "SMilliliter"),
            UnitItemUI("cL", "Centiliter"),
            UnitItemUI("dL", "Deciliter"),
            UnitItemUI("hL", "Hectoliter"),
            UnitItemUI("kL", "Kiloliter"),
        )
        UnitCategory.TIME -> listOf(
            UnitItemUI("H", "Hour"),
            UnitItemUI("s", "Second"),
            UnitItemUI("ms", "millisecond"),
            UnitItemUI("µs", "Microsecond"),
            UnitItemUI("ns", "Nanosecond"),
            UnitItemUI("m", "Minute"),
            UnitItemUI("d", "Day"),
        )
        UnitCategory.DATA -> listOf(
            UnitItemUI("GB", "Gigabyte"),
            UnitItemUI("MB", "Megabyte"),
            UnitItemUI("B", "Byte"),
            UnitItemUI("KB", "Kilobyte"),
            UnitItemUI("TB", "Terabyte"),
            UnitItemUI("b", "Bit"),
            UnitItemUI("Kb", "Kilobit"),
        )
    }

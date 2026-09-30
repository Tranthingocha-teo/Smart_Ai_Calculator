package dhn.intern.smart_ai_caculator_app.calculator

import dhn.intern.smart_ai_caculator_app.enum.UnitCategory
import dhn.intern.smart_ai_caculator_app.util.calculator.UnitConverterUtil
import org.junit.Assert.assertEquals
import org.junit.Test

class UnitConverterUtilTest {

    @Test
    fun testLengthConversion() {
        // 1 km = 1000 m
        val meters = UnitConverterUtil.convert(1.0, "km", "m", UnitCategory.LENGTH)
        assertEquals(1000.0, meters, 0.001)

        // 1 meter = 100 cm
        val centimeters = UnitConverterUtil.convert(1.0, "m", "cm", UnitCategory.LENGTH)
        assertEquals(100.0, centimeters, 0.001)

        // 1 inch = 2.54 cm
        val inchesToCm = UnitConverterUtil.convert(1.0, "in", "cm", UnitCategory.LENGTH)
        assertEquals(2.54, inchesToCm, 0.001)

        // 1 mile = 1.609344 km
        val milesToKm = UnitConverterUtil.convert(1.0, "mi", "km", UnitCategory.LENGTH)
        assertEquals(1.609344, milesToKm, 0.001)
    }

    @Test
    fun testMassConversion() {
        // 1 kg = 1000 g
        val grams = UnitConverterUtil.convert(1.0, "kg", "g", UnitCategory.MASS)
        assertEquals(1000.0, grams, 0.001)

        // 1 lb ≈ 0.45359237 kg
        val lbsToKg = UnitConverterUtil.convert(1.0, "lb", "kg", UnitCategory.MASS)
        assertEquals(0.453592, lbsToKg, 0.001)
    }

    @Test
    fun testVolumeConversion() {
        // 1 L = 1000 mL
        val ml = UnitConverterUtil.convert(1.0, "L", "mL", UnitCategory.VOLUME)
        assertEquals(1000.0, ml, 0.001)

        // 1 m³ = 1000 L
        val m3ToL = UnitConverterUtil.convert(1.0, "m³", "L", UnitCategory.VOLUME)
        assertEquals(1000.0, m3ToL, 0.001)
    }

    @Test
    fun testDataConversion() {
        // 1 GB = 1024 MB
        val mb = UnitConverterUtil.convert(1.0, "GB", "MB", UnitCategory.DATA)
        assertEquals(1024.0, mb, 0.001)

        // 1 Byte = 8 bits
        val bits = UnitConverterUtil.convert(1.0, "B", "b", UnitCategory.DATA)
        assertEquals(8.0, bits, 0.001)
    }

    @Test
    fun testSpeedConversion() {
        // 36 km/h = 10 m/s
        val ms = UnitConverterUtil.convert(36.0, "km/h", "m/s", UnitCategory.SPEED)
        assertEquals(10.0, ms, 0.001)

        // 100 mph to km/h
        val kmh = UnitConverterUtil.convert(100.0, "mph", "km/h", UnitCategory.SPEED)
        assertEquals(160.934, kmh, 0.01)
    }

    @Test
    fun testTimeConversion() {
        // 1 Hour = 3600 seconds
        val s = UnitConverterUtil.convert(1.0, "H", "s", UnitCategory.TIME)
        assertEquals(3600.0, s, 0.001)

        // 1 day = 24 hours
        val h = UnitConverterUtil.convert(1.0, "d", "H", UnitCategory.TIME)
        assertEquals(24.0, h, 0.001)
    }

    @Test
    fun testAreaConversion() {
        // 1 ha = 10,000 m²
        val sqm = UnitConverterUtil.convert(1.0, "ha", "m²", UnitCategory.AREA)
        assertEquals(10000.0, sqm, 0.001)

        // 1 km² = 100 ha
        val ha = UnitConverterUtil.convert(1.0, "km²", "ha", UnitCategory.AREA)
        assertEquals(100.0, ha, 0.001)
    }

    @Test
    fun testPressureConversion() {
        // 1 bar = 100,000 Pa = 100 kPa
        val kpa = UnitConverterUtil.convert(1.0, "bar", "kPa", UnitCategory.PRESSURE)
        assertEquals(100.0, kpa, 0.001)

        // 1 atm = 101.325 kPa
        val atmToKpa = UnitConverterUtil.convert(1.0, "atm", "kPa", UnitCategory.PRESSURE)
        assertEquals(101.325, atmToKpa, 0.01)
    }

    @Test
    fun testPowerAndEnergyConversion() {
        // 1 kW = 1000 W
        val w = UnitConverterUtil.convert(1.0, "kW", "W", UnitCategory.POWER)
        assertEquals(1000.0, w, 0.001)

        // 1 kWh = 3,600,000 J = 3600 kJ
        val kj = UnitConverterUtil.convert(1.0, "kWh", "kJ", UnitCategory.ENERGY)
        assertEquals(3600.0, kj, 0.001)
    }

    @Test
    fun testAngleConversion() {
        // 180 degrees = π radians
        val rad = UnitConverterUtil.convert(180.0, "°", "rad", UnitCategory.ANGLE)
        assertEquals(Math.PI, rad, 0.0001)
    }

    @Test
    fun testFuelConversion() {
        // 10 km/L to mpg
        val mpg = UnitConverterUtil.convert(10.0, "km/L", "mpg", UnitCategory.FUEL)
        assertEquals(23.521, mpg, 0.01)
    }

    @Test
    fun testIdentityConversion() {
        val same = UnitConverterUtil.convert(42.5, "m", "m", UnitCategory.LENGTH)
        assertEquals(42.5, same, 0.0001)
    }

    @Test
    fun testZeroConversion() {
        val zero = UnitConverterUtil.convert(0.0, "km", "mi", UnitCategory.LENGTH)
        assertEquals(0.0, zero, 0.0001)
    }

    @Test
    fun testTemperatureConversion() {
        // 0°C = 32°F
        val cToFZero = UnitConverterUtil.convert(0.0, "°C", "°F", UnitCategory.TEMPERATURE)
        assertEquals(32.0, cToFZero, 0.001)

        // 0°C = 273.15K
        val cToKZero = UnitConverterUtil.convert(0.0, "°C", "K", UnitCategory.TEMPERATURE)
        assertEquals(273.15, cToKZero, 0.001)

        // 100°C = 212°F
        val cToF100 = UnitConverterUtil.convert(100.0, "°C", "°F", UnitCategory.TEMPERATURE)
        assertEquals(212.0, cToF100, 0.001)

        // 100°C = 373.15K
        val cToK100 = UnitConverterUtil.convert(100.0, "°C", "K", UnitCategory.TEMPERATURE)
        assertEquals(373.15, cToK100, 0.001)

        // -40°C = -40°F
        val cToFMinus40 = UnitConverterUtil.convert(-40.0, "°C", "°F", UnitCategory.TEMPERATURE)
        assertEquals(-40.0, cToFMinus40, 0.001)

        // 0K = -273.15°C
        val kToCZero = UnitConverterUtil.convert(0.0, "K", "°C", UnitCategory.TEMPERATURE)
        assertEquals(-273.15, kToCZero, 0.001)

        // 36.6°C to °F & K
        val bodyTempF = UnitConverterUtil.convert(36.6, "°C", "°F", UnitCategory.TEMPERATURE)
        assertEquals(97.88, bodyTempF, 0.01)

        val bodyTempK = UnitConverterUtil.convert(36.6, "°C", "K", UnitCategory.TEMPERATURE)
        assertEquals(309.75, bodyTempK, 0.01)

        // 98.6°F to °C
        val fToC = UnitConverterUtil.convert(98.6, "°F", "°C", UnitCategory.TEMPERATURE)
        assertEquals(37.0, fToC, 0.01)

        // 300.15K to °C & °F
        val kToC = UnitConverterUtil.convert(300.15, "K", "°C", UnitCategory.TEMPERATURE)
        assertEquals(27.0, kToC, 0.01)

        val kToF = UnitConverterUtil.convert(300.15, "K", "°F", UnitCategory.TEMPERATURE)
        assertEquals(80.6, kToF, 0.01)

        // Negative values & decimals
        val negFToC = UnitConverterUtil.convert(-20.0, "°F", "°C", UnitCategory.TEMPERATURE)
        assertEquals(-28.8889, negFToC, 0.01)

        // Identity
        val sameC = UnitConverterUtil.convert(25.5, "°C", "°C", UnitCategory.TEMPERATURE)
        assertEquals(25.5, sameC, 0.0001)
    }
}

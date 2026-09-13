package dhn.intern.smart_ai_caculator_app.calculator

import dhn.intern.smart_ai_caculator_app.util.calculator.SmartFormatter
import org.junit.Assert.assertEquals
import org.junit.Test

class SmartFormatterTest {

    @Test
    fun testStripsTrailingZeros() {
        assertEquals("0", SmartFormatter.format(0.0))
        assertEquals("1.5", SmartFormatter.format(1.5000))
        assertEquals("2", SmartFormatter.format(2.000))
        assertEquals("100", SmartFormatter.format(100.0))
    }

    @Test
    fun testSignificantDigitsPrecision() {
        assertEquals("1.234568", SmartFormatter.format(1.23456789))
        assertEquals("123.4568", SmartFormatter.format(123.456789))
    }

    @Test
    fun testExtremeMagnitudesScientificNotation() {
        // Less than 1e-6
        val tiny = SmartFormatter.format(0.000000125)
        assertEquals("1.25e-7", tiny.lowercase())

        // Microscopic value (< 1e-15)
        val microscopic = SmartFormatter.format(3.17e-17)
        assertEquals("3.17e-17", microscopic.lowercase())

        // Greater than 1e9
        val huge = SmartFormatter.format(2500000000.0)
        assertEquals("2.5e9", huge.lowercase())
    }

    @Test
    fun testNegativeNumbers() {
        assertEquals("-40", SmartFormatter.format(-40.0))
        assertEquals("-1.5", SmartFormatter.format(-1.500))
    }

    @Test
    fun testCurrencyFormatting() {
        // VND and JPY: Integer rounded
        assertEquals("25401", SmartFormatter.formatCurrency(25400.75, "VND"))
        assertEquals("155", SmartFormatter.formatCurrency(155.49, "JPY"))

        // Standard major currencies (USD, EUR, GBP): 2-4 decimal places, trimming trailing zeros
        assertEquals("10.5", SmartFormatter.formatCurrency(10.50, "USD"))
        assertEquals("10.55", SmartFormatter.formatCurrency(10.55, "EUR"))
        assertEquals("10.5525", SmartFormatter.formatCurrency(10.55254, "GBP"))
    }
}

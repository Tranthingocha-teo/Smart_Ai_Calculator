package dhn.intern.smart_ai_caculator_app

import dhn.intern.smart_ai_caculator_app.util.calculator.SmartFormatter
import dhn.intern.smart_ai_caculator_app.util.calculator.UnitConverterUtil
import org.junit.Assert.assertEquals
import org.junit.Test

class CurrencyConversionTest {

    private val sampleRates = mapOf(
        "USD" to 1.0,
        "VND" to 25450.0,
        "JPY" to 155.0,
        "EUR" to 0.92
    )

    @Test
    fun testCurrencyConversionCalculation() {
        // 1 USD -> VND
        val vndAmount = UnitConverterUtil.convertCurrency(1.0, "USD", "VND", sampleRates)
        assertEquals(25450.0, vndAmount, 0.001)

        // 25450 VND -> USD
        val usdAmount = UnitConverterUtil.convertCurrency(25450.0, "VND", "USD", sampleRates)
        assertEquals(1.0, usdAmount, 0.001)

        // EUR -> JPY (quy đổi chéo qua base USD)
        // 0.92 EUR = 1 USD = 155 JPY => 1 EUR = 155 / 0.92 JPY
        val jpyFromEur = UnitConverterUtil.convertCurrency(1.0, "EUR", "JPY", sampleRates)
        val expectedJpy = 1.0 / 0.92 * 155.0
        assertEquals(expectedJpy, jpyFromEur, 0.001)
    }

    @Test
    fun testSpecialCurrencyRounding() {
        // VND và JPY phải làm tròn số nguyên không có số thập phân
        assertEquals("25450", SmartFormatter.formatCurrency(25450.4, "VND"))
        assertEquals("25451", SmartFormatter.formatCurrency(25450.6, "VND"))
        assertEquals("155", SmartFormatter.formatCurrency(155.2, "JPY"))

        // USD và EUR giữ thập phân
        assertEquals("1.085", SmartFormatter.formatCurrency(1.0850, "USD"))
        assertEquals("0.92", SmartFormatter.formatCurrency(0.9200, "EUR"))
    }
}

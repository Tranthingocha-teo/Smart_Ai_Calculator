package dhn.intern.smart_ai_caculator_app.util.calculator

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import kotlin.math.abs

object SmartFormatter {

    private val symbols = DecimalFormatSymbols(Locale.US)

    /**
     * Formats numerical conversion results:
     * - Strips superfluous trailing zeros
     * - Limits to 6-8 significant digits
     * - Formats extreme magnitudes (< 1e-6 or >= 1e9) in scientific notation
     */
    fun format(value: Double, maxSignificantDigits: Int = 7): String {
        if (value.isNaN() || value.isInfinite()) return "0"
        if (value == 0.0) return "0"

        val absVal = abs(value)

        // Scientific notation for extreme numbers
        if (absVal < 1e-6 || absVal >= 1e9) {
            val df = DecimalFormat("0.######E0", symbols)
            val formatted = df.format(value)
            return cleanFormattedString(formatted)
        }

        // Standard decimal with significant digits
        val bd = BigDecimal(value, MathContext(maxSignificantDigits, RoundingMode.HALF_UP))
            .stripTrailingZeros()

        return bd.toPlainString()
    }

    /**
     * Formats currency conversions:
     * - Integer rounding for zero-decimal currencies (VND, JPY, KRW, etc.)
     * - Minimum 2 and up to 4 decimal places for standard fiat currencies (USD, EUR, GBP, etc.)
     */
    fun formatCurrency(value: Double, currencyCode: String): String {
        if (value.isNaN() || value.isInfinite()) return "0"

        val zeroDecimalCurrencies = setOf("VND", "JPY", "KRW", "CLP", "HUF", "TWD")
        if (zeroDecimalCurrencies.contains(currencyCode.uppercase())) {
            return Math.round(value).toString()
        }

        // "0.00##" đảm bảo luôn giữ tối thiểu 2 chữ số thập phân (1.50, 2.00) và tối đa 4 số (1.2345)
        val df = DecimalFormat("0.00##", symbols)
        return df.format(value)
    }

    private fun cleanFormattedString(formatted: String): String {
        return if (formatted.contains("E") || formatted.contains("e")) {
            val parts = formatted.split(Regex("[eE]"))
            val mantissa = parts[0].trimEnd('0').trimEnd('.')
            val exponent = parts[1]
            "${mantissa}e${exponent}"
        } else {
            formatted.trimEnd('0').trimEnd('.')
        }
    }
}

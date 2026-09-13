package dhn.intern.smart_ai_caculator_app.calculator

import dhn.intern.smart_ai_caculator_app.util.calculator.DateCalculatorUtil
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class DateCalculatorUtilTest {

    @Test
    fun testFigmaDurationCalculation() {
        // Aug 22, 2025 to Sep 29, 2077
        val startCal = Calendar.getInstance().apply {
            set(2025, Calendar.AUGUST, 22, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endCal = Calendar.getInstance().apply {
            set(2077, Calendar.SEPTEMBER, 29, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val result = DateCalculatorUtil.calculateDuration(
            startDateMillis = startCal.timeInMillis,
            endDateMillis = endCal.timeInMillis
        )

        assertEquals(19031L, result.totalDays)
        assertEquals(2718L, result.totalWeeks)
        assertEquals(5L, result.remDaysWeeks)
        assertEquals(52L, result.years)
    }

    @Test
    fun testInvertedDatesYieldIdenticalResult() {
        val startCal = Calendar.getInstance().apply { set(2025, Calendar.JANUARY, 1, 12, 0, 0) }
        val endCal = Calendar.getInstance().apply { set(2025, Calendar.FEBRUARY, 1, 12, 0, 0) }

        val normal = DateCalculatorUtil.calculateDuration(startCal.timeInMillis, endCal.timeInMillis)
        val inverted = DateCalculatorUtil.calculateDuration(endCal.timeInMillis, startCal.timeInMillis)

        assertEquals(31L, normal.totalDays)
        assertEquals(normal.totalDays, inverted.totalDays)
        assertEquals(normal.totalWeeks, inverted.totalWeeks)
        assertEquals(normal.remDaysWeeks, inverted.remDaysWeeks)
    }

    @Test
    fun testSameDayDuration() {
        val cal = Calendar.getInstance().apply { set(2026, Calendar.SEPTEMBER, 13, 12, 0, 0) }
        val result = DateCalculatorUtil.calculateDuration(cal.timeInMillis, cal.timeInMillis)

        assertEquals(0L, result.totalDays)
        assertEquals(0L, result.totalWeeks)
        assertEquals(0L, result.remDaysWeeks)
        assertEquals(0L, result.years)
    }

    @Test
    fun testTargetDateAddition() {
        val baseCal = Calendar.getInstance().apply {
            set(2026, Calendar.SEPTEMBER, 13, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Add 2 months
        val targetMillis = DateCalculatorUtil.calculateTargetDate(
            baseDateMillis = baseCal.timeInMillis,
            isAdd = true,
            months = 2
        )

        val targetCal = Calendar.getInstance().apply { timeInMillis = targetMillis }
        assertEquals(2026, targetCal.get(Calendar.YEAR))
        assertEquals(Calendar.NOVEMBER, targetCal.get(Calendar.MONTH))
        assertEquals(13, targetCal.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun testTargetDateSubtraction() {
        val baseCal = Calendar.getInstance().apply {
            set(2026, Calendar.SEPTEMBER, 13, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Subtract 1 year and 13 days -> Aug 31, 2025
        val targetMillis = DateCalculatorUtil.calculateTargetDate(
            baseDateMillis = baseCal.timeInMillis,
            isAdd = false,
            years = 1,
            days = 13
        )

        val targetCal = Calendar.getInstance().apply { timeInMillis = targetMillis }
        assertEquals(2025, targetCal.get(Calendar.YEAR))
        assertEquals(Calendar.AUGUST, targetCal.get(Calendar.MONTH))
        assertEquals(31, targetCal.get(Calendar.DAY_OF_MONTH))
    }
}

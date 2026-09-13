package dhn.intern.smart_ai_caculator_app.util.calculator

import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.math.abs

data class DateDurationResult(
    val totalDays: Long,
    val totalWeeks: Long,
    val remDaysWeeks: Long,
    val years: Long,
    val remDaysYears: Long
)

object DateCalculatorUtil {

    fun calculateDuration(startDateMillis: Long, endDateMillis: Long): DateDurationResult {
        val earlierMillis = minOf(startDateMillis, endDateMillis)
        val laterMillis = maxOf(startDateMillis, endDateMillis)

        val totalDays = TimeUnit.MILLISECONDS.toDays(laterMillis - earlierMillis)
        val totalWeeks = totalDays / 7
        val remDaysWeeks = totalDays % 7

        val cStart = Calendar.getInstance().apply { timeInMillis = earlierMillis }
        val cEnd = Calendar.getInstance().apply { timeInMillis = laterMillis }

        var years = (cEnd.get(Calendar.YEAR) - cStart.get(Calendar.YEAR)).toLong()
        val tempCal = (cStart.clone() as Calendar).apply { add(Calendar.YEAR, years.toInt()) }
        if (tempCal.after(cEnd)) {
            years--
            tempCal.timeInMillis = cStart.timeInMillis
            tempCal.add(Calendar.YEAR, years.toInt())
        }
        val remDaysYears = TimeUnit.MILLISECONDS.toDays(cEnd.timeInMillis - tempCal.timeInMillis)

        return DateDurationResult(
            totalDays = totalDays,
            totalWeeks = totalWeeks,
            remDaysWeeks = remDaysWeeks,
            years = years,
            remDaysYears = remDaysYears
        )
    }

    fun calculateTargetDate(
        baseDateMillis: Long,
        isAdd: Boolean,
        years: Int = 0,
        months: Int = 0,
        weeks: Int = 0,
        days: Int = 0
    ): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = baseDateMillis
            val sign = if (isAdd) 1 else -1
            if (years != 0) add(Calendar.YEAR, sign * years)
            if (months != 0) add(Calendar.MONTH, sign * months)
            val totalExtraDays = weeks * 7 + days
            if (totalExtraDays != 0) add(Calendar.DAY_OF_MONTH, sign * totalExtraDays)
        }
        return cal.timeInMillis
    }
}

package dhn.intern.smart_ai_caculator_app.util.calculator

data class TipCalculationResult(
    val bill: Double,
    val taxAmount: Double,
    val tipAmount: Double,
    val total: Double,
    val totalPerPerson: Double,
    val tipPerPerson: Double
)

object TipCalculatorUtil {

    fun calculateTip(
        bill: Double,
        people: Int,
        tipPercentage: Double,
        taxPercentage: Double = 0.0,
        isPostTax: Boolean = true
    ): TipCalculationResult {
        if (bill <= 0.0) {
            return TipCalculationResult(
                bill = 0.0,
                taxAmount = 0.0,
                tipAmount = 0.0,
                total = 0.0,
                totalPerPerson = 0.0,
                tipPerPerson = 0.0
            )
        }

        val safePeople = people.coerceAtLeast(1)
        val safeTaxPct = taxPercentage.coerceAtLeast(0.0)
        val safeTipPct = tipPercentage.coerceAtLeast(0.0)

        val taxAmount = bill * (safeTaxPct / 100.0)
        val tipBase = if (isPostTax) bill + taxAmount else bill
        val tipAmount = tipBase * (safeTipPct / 100.0)
        val total = bill + taxAmount + tipAmount

        return TipCalculationResult(
            bill = bill,
            taxAmount = taxAmount,
            tipAmount = tipAmount,
            total = total,
            totalPerPerson = total / safePeople,
            tipPerPerson = tipAmount / safePeople
        )
    }
}

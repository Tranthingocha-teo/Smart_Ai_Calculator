package dhn.intern.smart_ai_caculator_app.util.calculator

import kotlin.math.pow

enum class LoanType {
    EQUAL_PRINCIPAL,
    EQUAL_PRINCIPAL_AND_INTEREST
}

data class LoanCalculationResult(
    val principal: Double,
    val totalPayment: Double,
    val totalInterest: Double,
    val monthlyPaymentMin: Double,
    val monthlyPaymentMax: Double,
    val isVariableMonthly: Boolean
)

object LoanCalculatorUtil {

    fun calculateLoan(
        principal: Double,
        annualRate: Double,
        termMonths: Int,
        type: LoanType
    ): LoanCalculationResult {
        if (principal <= 0.0 || termMonths <= 0) {
            return LoanCalculationResult(
                principal = 0.0,
                totalPayment = 0.0,
                totalInterest = 0.0,
                monthlyPaymentMin = 0.0,
                monthlyPaymentMax = 0.0,
                isVariableMonthly = false
            )
        }

        val safeAnnualRate = annualRate.coerceAtLeast(0.0)
        val monthlyRate = (safeAnnualRate / 100.0) / 12.0

        return when (type) {
            LoanType.EQUAL_PRINCIPAL_AND_INTEREST -> {
                // EMI
                val emi = if (monthlyRate > 0.0) {
                    val powTerm = (1.0 + monthlyRate).pow(termMonths.toDouble())
                    principal * monthlyRate * powTerm / (powTerm - 1.0)
                } else {
                    principal / termMonths
                }
                val totalPayment = emi * termMonths
                val totalInterest = totalPayment - principal

                LoanCalculationResult(
                    principal = principal,
                    totalPayment = totalPayment,
                    totalInterest = totalInterest,
                    monthlyPaymentMin = emi,
                    monthlyPaymentMax = emi,
                    isVariableMonthly = false
                )
            }
            LoanType.EQUAL_PRINCIPAL -> {
                val principalPerMonth = principal / termMonths
                val totalInterest = if (monthlyRate > 0.0) {
                    ((termMonths + 1.0) / 2.0) * principal * monthlyRate
                } else {
                    0.0
                }
                val totalPayment = principal + totalInterest

                val firstMonth = principalPerMonth + (principal * monthlyRate)
                val lastMonth = principalPerMonth + (principalPerMonth * monthlyRate)

                LoanCalculationResult(
                    principal = principal,
                    totalPayment = totalPayment,
                    totalInterest = totalInterest,
                    monthlyPaymentMin = minOf(firstMonth, lastMonth),
                    monthlyPaymentMax = maxOf(firstMonth, lastMonth),
                    isVariableMonthly = termMonths > 1 && safeAnnualRate > 0.0
                )
            }
        }
    }
}

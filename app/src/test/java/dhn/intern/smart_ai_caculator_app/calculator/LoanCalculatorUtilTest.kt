package dhn.intern.smart_ai_caculator_app.calculator

import dhn.intern.smart_ai_caculator_app.util.calculator.LoanCalculatorUtil
import dhn.intern.smart_ai_caculator_app.util.calculator.LoanType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LoanCalculatorUtilTest {

    @Test
    fun testEqualPrincipalPayment() {
        // Principal: $10,000, 5% annual rate, 12 months
        // Monthly rate = 5 / 100 / 12 = 0.00416667
        // Monthly principal = 10,000 / 12 = 833.333
        // Total interest = (13 / 2) * 10,000 * (0.05 / 12) = 270.833
        // Total payment = 10,270.833
        // First month = 833.333 + 10,000 * 0.00416667 = 875.00
        // Last month = 833.333 + 833.333 * 0.00416667 = 836.805 -> 836.81
        val result = LoanCalculatorUtil.calculateLoan(
            principal = 10000.0,
            annualRate = 5.0,
            termMonths = 12,
            type = LoanType.EQUAL_PRINCIPAL
        )

        assertEquals(10000.0, result.principal, 0.01)
        assertEquals(10270.83, result.totalPayment, 0.02)
        assertEquals(270.83, result.totalInterest, 0.02)
        assertEquals(836.81, result.monthlyPaymentMin, 0.02)
        assertEquals(875.00, result.monthlyPaymentMax, 0.02)
        assertTrue(result.isVariableMonthly)
    }

    @Test
    fun testEqualPrincipalAndInterestEMI() {
        // Principal: $10,000, 5% annual rate, 12 months
        // EMI = 10000 * r * (1+r)^12 / ((1+r)^12 - 1) = 856.07
        // Total = 856.0747 * 12 = 10,272.90
        // Total interest = 272.90
        val result = LoanCalculatorUtil.calculateLoan(
            principal = 10000.0,
            annualRate = 5.0,
            termMonths = 12,
            type = LoanType.EQUAL_PRINCIPAL_AND_INTEREST
        )

        assertEquals(10000.0, result.principal, 0.01)
        assertEquals(856.07, result.monthlyPaymentMin, 0.02)
        assertEquals(856.07, result.monthlyPaymentMax, 0.02)
        assertEquals(10272.90, result.totalPayment, 0.05)
        assertEquals(272.90, result.totalInterest, 0.05)
    }

    @Test
    fun testZeroInterestLoan() {
        val result = LoanCalculatorUtil.calculateLoan(
            principal = 12000.0,
            annualRate = 0.0,
            termMonths = 12,
            type = LoanType.EQUAL_PRINCIPAL_AND_INTEREST
        )

        assertEquals(12000.0, result.totalPayment, 0.01)
        assertEquals(0.0, result.totalInterest, 0.01)
        assertEquals(1000.0, result.monthlyPaymentMin, 0.01)
    }

    @Test
    fun testEdgeCases() {
        val zeroPrincipal = LoanCalculatorUtil.calculateLoan(0.0, 5.0, 12, LoanType.EQUAL_PRINCIPAL)
        assertEquals(0.0, zeroPrincipal.totalPayment, 0.001)

        val zeroMonths = LoanCalculatorUtil.calculateLoan(10000.0, 5.0, 0, LoanType.EQUAL_PRINCIPAL)
        assertEquals(0.0, zeroMonths.totalPayment, 0.001)
    }
}

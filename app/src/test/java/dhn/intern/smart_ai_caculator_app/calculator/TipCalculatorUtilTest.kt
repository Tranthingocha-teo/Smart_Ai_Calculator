package dhn.intern.smart_ai_caculator_app.calculator

import dhn.intern.smart_ai_caculator_app.util.calculator.TipCalculatorUtil
import org.junit.Assert.assertEquals
import org.junit.Test

class TipCalculatorUtilTest {

    @Test
    fun testStandardTipCalculation() {
        val result = TipCalculatorUtil.calculateTip(
            bill = 100.0,
            people = 1,
            tipPercentage = 15.0,
            taxPercentage = 0.0,
            isPostTax = true
        )

        assertEquals(100.0, result.bill, 0.001)
        assertEquals(0.0, result.taxAmount, 0.001)
        assertEquals(15.0, result.tipAmount, 0.001)
        assertEquals(115.0, result.total, 0.001)
        assertEquals(115.0, result.totalPerPerson, 0.001)
        assertEquals(15.0, result.tipPerPerson, 0.001)
    }

    @Test
    fun testMultiPersonSplitPostTax() {
        // Bill: 100, Tax: 10% (10.0), Tip: 20% on (100+10 = 110) -> 22.0. Total = 132.0.
        // People: 4 -> Total per person = 33.0, Tip per person = 5.5
        val result = TipCalculatorUtil.calculateTip(
            bill = 100.0,
            people = 4,
            tipPercentage = 20.0,
            taxPercentage = 10.0,
            isPostTax = true
        )

        assertEquals(10.0, result.taxAmount, 0.001)
        assertEquals(22.0, result.tipAmount, 0.001)
        assertEquals(132.0, result.total, 0.001)
        assertEquals(33.0, result.totalPerPerson, 0.001)
        assertEquals(5.5, result.tipPerPerson, 0.001)
    }

    @Test
    fun testPreTaxTipCalculation() {
        // Bill: 100, Tax: 10% (10.0), Tip: 15% on 100 -> 15.0. Total = 125.0.
        // People: 2 -> Total per person = 62.5
        val result = TipCalculatorUtil.calculateTip(
            bill = 100.0,
            people = 2,
            tipPercentage = 15.0,
            taxPercentage = 10.0,
            isPostTax = false
        )

        assertEquals(10.0, result.taxAmount, 0.001)
        assertEquals(15.0, result.tipAmount, 0.001)
        assertEquals(125.0, result.total, 0.001)
        assertEquals(62.5, result.totalPerPerson, 0.001)
        assertEquals(7.5, result.tipPerPerson, 0.001)
    }

    @Test
    fun testEdgeCases() {
        val zeroBill = TipCalculatorUtil.calculateTip(0.0, 2, 15.0)
        assertEquals(0.0, zeroBill.total, 0.001)

        val negativePeople = TipCalculatorUtil.calculateTip(100.0, -5, 10.0)
        assertEquals(110.0, negativePeople.total, 0.001)
        assertEquals(110.0, negativePeople.totalPerPerson, 0.001)
    }
}

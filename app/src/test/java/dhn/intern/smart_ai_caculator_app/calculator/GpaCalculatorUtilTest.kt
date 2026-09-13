package dhn.intern.smart_ai_caculator_app.calculator

import dhn.intern.smart_ai_caculator_app.util.calculator.GpaCalculatorUtil
import dhn.intern.smart_ai_caculator_app.util.calculator.GpaSubjectInput
import org.junit.Assert.assertEquals
import org.junit.Test

class GpaCalculatorUtilTest {

    @Test
    fun testFigmaSampleGpaCalculation() {
        val figmaSubjects = listOf(
            GpaSubjectInput(credits = 100.0, grade = 85.0),
            GpaSubjectInput(credits = 90.0, grade = 78.0),
            GpaSubjectInput(credits = 80.0, grade = 88.0),
            GpaSubjectInput(credits = 70.0, grade = 74.0),
            GpaSubjectInput(credits = 65.0, grade = 82.0),
            GpaSubjectInput(credits = 95.0, grade = 91.0),
            GpaSubjectInput(credits = 85.0, grade = 76.0),
            GpaSubjectInput(credits = 88.0, grade = 84.0),
            GpaSubjectInput(credits = 72.0, grade = 80.0),
            GpaSubjectInput(credits = 78.0, grade = 89.0)
        )

        val result = GpaCalculatorUtil.calculateGpa(figmaSubjects)

        // Total Credits = 100+90+80+70+65+95+85+88+72+78 = 823.0
        assertEquals(823.0, result.totalCredits, 0.001)

        // Weighted Sum = 68269.0 -> GPA = 68269 / 823 = 82.95139...
        assertEquals(68269.0, result.weightedSum, 0.001)
        assertEquals(82.95, result.gpa, 0.01)
    }

    @Test
    fun testStandardFourPointScale() {
        // 3 credits A (4.0), 4 credits B+ (3.5), 3 credits B (3.0)
        // Weighted = 3*4 + 4*3.5 + 3*3 = 12 + 14 + 9 = 35.0
        // Total credits = 10.0 -> GPA = 3.5
        val subjects = listOf(
            GpaSubjectInput(credits = 3.0, grade = 4.0),
            GpaSubjectInput(credits = 4.0, grade = 3.5),
            GpaSubjectInput(credits = 3.0, grade = 3.0)
        )

        val result = GpaCalculatorUtil.calculateGpa(subjects)

        assertEquals(10.0, result.totalCredits, 0.001)
        assertEquals(35.0, result.weightedSum, 0.001)
        assertEquals(3.5, result.gpa, 0.001)
    }

    @Test
    fun testEdgeCases() {
        val empty = GpaCalculatorUtil.calculateGpa(emptyList())
        assertEquals(0.0, empty.totalCredits, 0.001)
        assertEquals(0.0, empty.gpa, 0.001)

        val invalidCredits = listOf(
            GpaSubjectInput(credits = -1.0, grade = 80.0),
            GpaSubjectInput(credits = 0.0, grade = 90.0)
        )
        val resultInvalid = GpaCalculatorUtil.calculateGpa(invalidCredits)
        assertEquals(0.0, resultInvalid.totalCredits, 0.001)
        assertEquals(0.0, resultInvalid.gpa, 0.001)
    }
}

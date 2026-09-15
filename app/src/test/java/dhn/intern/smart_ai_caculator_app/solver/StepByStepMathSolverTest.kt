package dhn.intern.smart_ai_caculator_app.solver

import dhn.intern.smart_ai_caculator_app.domain.solver.StepByStepMathSolver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StepByStepMathSolverTest {

    private lateinit var solver: StepByStepMathSolver

    @Before
    fun setUp() {
        solver = StepByStepMathSolver()
    }

    @Test
    fun testSolveSimpleArithmetic() {
        val result = solver.solve("25 * 4 + 10")
        assertNotNull(result)
        assertTrue(result.steps.isNotEmpty())
        assertEquals("110", result.finalAnswer)
        assertTrue(result.explanation.contains("110"))
    }

    @Test
    fun testSolveLinearEquationSimple() {
        val result = solver.solve("2x + 5 = 15")
        assertNotNull(result)
        assertTrue(result.isEquation)
        assertTrue(result.steps.isNotEmpty())
        assertEquals("x = 5", result.finalAnswer)
        assertTrue(result.explanation.contains("x = 5"))
    }

    @Test
    fun testSolveLinearEquationWithNegativeCoefficients() {
        val result = solver.solve("3x - 9 = 0")
        assertNotNull(result)
        assertTrue(result.isEquation)
        assertEquals("x = 3", result.finalAnswer)
    }

    @Test
    fun testSolveLinearEquationBothSides() {
        val result = solver.solve("5x - 4 = 2x + 5")
        assertNotNull(result)
        assertTrue(result.isEquation)
        assertEquals("x = 3", result.finalAnswer)
    }

    @Test
    fun testSolveQuadraticEquation() {
        val result = solver.solve("x^2 - 5x + 6 = 0")
        assertNotNull(result)
        assertTrue(result.isEquation)
        assertTrue(result.finalAnswer.contains("2") && result.finalAnswer.contains("3"))
    }
}

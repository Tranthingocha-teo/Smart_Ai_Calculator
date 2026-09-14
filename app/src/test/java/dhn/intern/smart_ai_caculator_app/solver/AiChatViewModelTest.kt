package dhn.intern.smart_ai_caculator_app.solver

import dhn.intern.smart_ai_caculator_app.domain.solver.StepByStepMathSolver
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.AiChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AiChatViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var solver: StepByStepMathSolver
    private lateinit var viewModel: AiChatViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        solver = StepByStepMathSolver()
        viewModel = AiChatViewModel(solver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has welcome message and isGenerating is false`() {
        val messages = viewModel.messages.value
        assertEquals(1, messages.size)
        assertFalse(messages[0].isFromUser)
        assertTrue(messages[0].text.contains("Gia sư Toán học AI"))
        assertFalse(viewModel.isGenerating.value)
    }

    @Test
    fun `blank message does not change messages`() {
        viewModel.sendMessage("   ")
        assertEquals(1, viewModel.messages.value.size)
    }

    @Test
    fun `sendMessage with arithmetic expression adds user and AI response`() = runTest {
        viewModel.sendMessage("25 * 4")
        
        // Immediately after sendMessage, user message is present
        assertEquals(2, viewModel.messages.value.size)
        assertEquals("25 * 4", viewModel.messages.value[1].text)
        assertTrue(viewModel.messages.value[1].isFromUser)
        assertTrue(viewModel.isGenerating.value)

        // Advance coroutine virtual time
        advanceUntilIdle()

        assertFalse(viewModel.isGenerating.value)
        assertEquals(3, viewModel.messages.value.size)

        val aiMsg = viewModel.messages.value[2]
        assertFalse(aiMsg.isFromUser)
        assertNotNull(aiMsg.solution)
        assertEquals("100", aiMsg.solution?.finalAnswer)
        assertTrue(aiMsg.text.contains("100"))
    }

    @Test
    fun `sendMessage with prompt prefix strips prefix and solves linear equation`() = runTest {
        viewModel.sendMessage("Hãy giải chi tiết từng bước bài toán này: 2x + 5 = 15")
        advanceUntilIdle()

        val messages = viewModel.messages.value
        assertEquals(3, messages.size)

        val aiMsg = messages[2]
        assertNotNull(aiMsg.solution)
        assertTrue(aiMsg.solution?.isEquation == true)
        assertEquals("x = 5", aiMsg.solution?.finalAnswer)
        assertTrue(aiMsg.text.contains("x = 5"))
    }

    @Test
    fun `sendMessage with quadratic equation solves roots`() = runTest {
        viewModel.sendMessage("x^2 - 5x + 6 = 0")
        advanceUntilIdle()

        val messages = viewModel.messages.value
        assertEquals(3, messages.size)

        val aiMsg = messages[2]
        assertNotNull(aiMsg.solution)
        assertTrue(aiMsg.solution?.isEquation == true)
        assertTrue(aiMsg.solution?.finalAnswer?.contains("3") == true)
        assertTrue(aiMsg.solution?.finalAnswer?.contains("2") == true)
    }
}

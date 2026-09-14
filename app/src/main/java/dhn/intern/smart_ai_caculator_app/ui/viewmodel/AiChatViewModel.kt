package dhn.intern.smart_ai_caculator_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dhn.intern.smart_ai_caculator_app.domain.solver.StepByStepMathSolver
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * ViewModel for Interactive AI Math Tutor Chat Screen.
 * Automatically resolves math questions step-by-step using on-device math solver.
 */
class AiChatViewModel(
    private val mathSolver: StepByStepMathSolver
) : ViewModel() {

    data class ChatMessage(
        val id: String = UUID.randomUUID().toString(),
        val text: String,
        val isFromUser: Boolean,
        val timestamp: String = getCurrentTime(),
        val solution: StepByStepMathSolver.MathSolution? = null
    )

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Xin chào! Tôi là Gia sư Toán học AI. Hãy chụp ảnh công thức hoặc nhập bất kỳ bài toán nào (phép tính, phương trình bậc nhất, bậc hai...), tôi sẽ giải chi tiết từng bước cho bạn!",
                isFromUser = false
            )
        )
    )
    val messages = _messages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    fun sendMessage(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isBlank() || _isGenerating.value) return

        val userMessage = ChatMessage(
            text = trimmed,
            isFromUser = true
        )
        _messages.value = _messages.value + userMessage
        _isGenerating.value = true

        viewModelScope.launch {
            // Simulate realistic fast AI thinking time (~300ms)
            delay(300)

            // Extract formula if user sent prompt with prefix like "Hãy giải chi tiết từng bước bài toán này: ..."
            val mathTarget = extractMathExpression(trimmed)
            val solution = mathSolver.solve(mathTarget)

            val aiResponseText = solution.explanation

            val aiMessage = ChatMessage(
                text = aiResponseText,
                isFromUser = false,
                solution = solution
            )

            _messages.value = _messages.value + aiMessage
            _isGenerating.value = false
        }
    }

    private fun extractMathExpression(input: String): String {
        val prefix = "Hãy giải chi tiết từng bước bài toán này:"
        return if (input.contains(prefix, ignoreCase = true)) {
            input.substringAfter(prefix).trim()
        } else {
            input
        }
    }

    companion object {
        private fun getCurrentTime(): String {
            val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
            return sdf.format(Date())
        }
    }
}

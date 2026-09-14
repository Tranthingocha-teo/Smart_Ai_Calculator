package dhn.intern.smart_ai_caculator_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dhn.intern.smart_ai_caculator_app.data.preferences.ai.AiPreferences
import dhn.intern.smart_ai_caculator_app.domain.ai.GeminiMathService
import dhn.intern.smart_ai_caculator_app.domain.solver.StepByStepMathSolver
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * ViewModel for Interactive AI Math Tutor Chat Screen.
 * Combines Google Gemini 1.5 Flash for natural language conversation
 * with on-device StepByStepMathSolver for instant offline math solving.
 */
class AiChatViewModel(
    private val mathSolver: StepByStepMathSolver,
    private val geminiService: GeminiMathService = GeminiMathService(),
    private val aiPreferences: AiPreferences? = null
) : ViewModel() {

    data class ChatMessage(
        val id: String = UUID.randomUUID().toString(),
        val text: String,
        val isFromUser: Boolean,
        val timestamp: String = getCurrentTime(),
        val solution: StepByStepMathSolver.MathSolution? = null
    )

    val currentApiKey: StateFlow<String> = aiPreferences?.apiKey
        ?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
        ?: MutableStateFlow("")

    private val _messages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                text = "Xin chào! Tôi là Gia sư Toán học AI. 👋\n\nBạn có thể chụp ảnh đề bài từ Camera hoặc nhập bất kỳ bài toán/câu hỏi nào, tôi sẽ giải chi tiết từng bước và trò chuyện cùng bạn!",
                isFromUser = false
            )
        )
    )
    val messages = _messages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            aiPreferences?.saveApiKey(key)
        }
    }

    fun isGeminiActive(): Boolean = geminiService.isConfigured()

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
            // 1. If Gemini is configured, prioritize full LLM reasoning
            if (geminiService.isConfigured()) {
                val geminiResult = geminiService.generateResponse(trimmed)
                if (geminiResult.isSuccess) {
                    val responseText = geminiResult.getOrThrow()
                    _messages.value = _messages.value + ChatMessage(
                        text = responseText,
                        isFromUser = false
                    )
                    _isGenerating.value = false
                    return@launch
                }
            }

            // 2. Offline / Local fallback with conversational awareness
            delay(300)

            if (isConversationalGreeting(trimmed)) {
                val greetingReply = buildString {
                    appendLine("Xin chào bạn! Tôi là Gia sư Toán học AI. 👋")
                    appendLine()
                    appendLine("Hiện tại tôi đang hỗ trợ giải trực tiếp trên máy:")
                    appendLine("• Các phép tính số học (+, -, *, /)")
                    appendLine("• Phương trình bậc nhất một ẩn (`2x + 5 = 15`)")
                    appendLine("• Phương trình bậc hai (`x^2 - 5x + 6 = 0`)")
                    appendLine()
                    appendLine("💡 Để trò chuyện tự nhiên và giải mọi dạng toán nâng cao, bạn có thể bấm biểu tượng ⚙️ ở góc trên để cài đặt Google Gemini API Key nhé!")
                }
                _messages.value = _messages.value + ChatMessage(text = greetingReply, isFromUser = false)
                _isGenerating.value = false
                return@launch
            }

            if (isHelpOrAbout(trimmed)) {
                val aboutReply = buildString {
                    appendLine("Tôi là Gia sư Toán học AI của ứng dụng Smart AI Calculator! 🎓")
                    appendLine("Tôi có thể phân tích và giải chi tiết từng bước cho bạn.")
                    appendLine("Hãy thử nhập một phép tính hoặc phương trình như `5x - 4 = 2x + 5`, hoặc bấm tab \"Scan\" để chụp từ vở bài tập nhé!")
                }
                _messages.value = _messages.value + ChatMessage(text = aboutReply, isFromUser = false)
                _isGenerating.value = false
                return@launch
            }

            // 3. Mathematical solving fallback
            val mathTarget = extractMathExpression(trimmed)
            val solution = mathSolver.solve(mathTarget)

            val aiResponseText = if (solution.finalAnswer == "Không thể tính toán") {
                buildString {
                    appendLine("Tôi chưa nhận dạng được công thức toán học từ nội dung: \"$trimmed\".")
                    appendLine()
                    appendLine("💡 Bạn có thể:")
                    appendLine("1. Nhập phương trình dạng `ax + b = cx + d` hoặc `ax^2 + bx + c = 0`.")
                    appendLine("2. Nhấn biểu tượng ⚙️ ở góc trên để cài đặt Gemini API Key để trò chuyện và giải mọi câu hỏi bằng AI.")
                }
            } else {
                solution.explanation
            }

            val aiMessage = ChatMessage(
                text = aiResponseText,
                isFromUser = false,
                solution = solution
            )

            _messages.value = _messages.value + aiMessage
            _isGenerating.value = false
        }
    }

    private fun isConversationalGreeting(text: String): Boolean {
        val lower = text.trim().lowercase()
        val greetings = listOf("xin chào", "chào", "chào bạn", "hello", "hi", "hey", "alo", "chào bot", "good morning")
        return greetings.any { lower == it || lower.startsWith("$it ") || lower.endsWith(" $it") }
    }

    private fun isHelpOrAbout(text: String): Boolean {
        val lower = text.trim().lowercase()
        val terms = listOf("bạn là ai", "who are you", "giúp tôi", "hướng dẫn", "help", "bạn làm được gì", "tính năng")
        return terms.any { lower.contains(it) }
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

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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * ViewModel for Interactive AI Math Tutor Chat Screen.
 * Combines Google Gemini 1.5 Flash (with live streaming) for natural language conversation
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

    private val initialWelcomeMessage = ChatMessage(
        text = "Xin chào! Tôi là Gia sư AI. 👋\n\nBạn có thể chụp ảnh đề bài từ Camera hoặc nhập bất kỳ bài toán/câu hỏi nào, tôi sẽ giải chi tiết từng bước và trò chuyện cùng bạn!",
        isFromUser = false
    )

    val currentApiKey: StateFlow<String> = aiPreferences?.apiKey
        ?.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
        ?: MutableStateFlow("")

    private val _messages = MutableStateFlow<List<ChatMessage>>(listOf(initialWelcomeMessage))
    val messages = _messages.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            aiPreferences?.saveApiKey(key)
        }
    }

    fun isGeminiActive(): Boolean = geminiService.isConfigured()

    fun clearConversation() {
        _isGenerating.value = false
        _messages.value = listOf(initialWelcomeMessage)
    }

    fun rewriteMessage(messageId: String) {
        if (_isGenerating.value) return
        val currentList = _messages.value
        val targetIdx = currentList.indexOfFirst { it.id == messageId }
        if (targetIdx == -1) return

        val precedingUserMsg = currentList.subList(0, targetIdx).lastOrNull { it.isFromUser } ?: return
        executeGeneration(precedingUserMsg.text, isRewrite = true)
    }

    fun sendMessage(userText: String) {
        val trimmed = userText.trim()
        if (trimmed.isBlank() || _isGenerating.value) return

        val userMessage = ChatMessage(
            text = trimmed,
            isFromUser = true
        )
        _messages.value = _messages.value + userMessage
        executeGeneration(trimmed, isRewrite = false)
    }

    private fun executeGeneration(prompt: String, isRewrite: Boolean) {
        _isGenerating.value = true

        viewModelScope.launch {
            // 1. If Gemini is configured, use live streaming response
            if (geminiService.isConfigured()) {
                val aiMessage = ChatMessage(text = "", isFromUser = false)
                _messages.value = _messages.value + aiMessage
                var accumulated = ""
                var streamSuccess = false

                try {
                    val streamFlow = if (isRewrite) {
                        geminiService.generateRewriteStream(prompt)
                    } else {
                        geminiService.generateResponseStream(prompt)
                    }

                    streamFlow
                        .catch { e ->
                            if (!streamSuccess) {
                                // If error occurred before receiving any tokens, let offline fallback handle
                                handleOfflineFallback(prompt, isRewrite)
                            } else {
                                accumulated += "\n\n[Đã ngắt kết nối đến AI]"
                                _messages.value = _messages.value.dropLast(1) + aiMessage.copy(text = accumulated)
                            }
                        }
                        .collect { chunk ->
                            streamSuccess = true
                            accumulated += chunk
                            _messages.value = _messages.value.dropLast(1) + aiMessage.copy(text = accumulated)
                        }

                    if (streamSuccess && accumulated.isNotBlank()) {
                        _isGenerating.value = false
                        return@launch
                    }
                } catch (e: Exception) {
                    if (!streamSuccess) {
                        handleOfflineFallback(prompt, isRewrite)
                        return@launch
                    }
                }
            }

            // 2. Offline / Local fallback with conversational awareness
            handleOfflineFallback(prompt, isRewrite)
        }
    }

    private suspend fun handleOfflineFallback(prompt: String, isRewrite: Boolean) {
        delay(200)

        if (isConversationalGreeting(prompt)) {
            val greetingReply = buildString {
                appendLine("Xin chào bạn! Tôi là Gia sư AI. 👋")
                appendLine()
                appendLine("Hiện tại tôi đang hỗ trợ giải trực tiếp trên máy:")
                appendLine("• Các phép tính số học (+, -, *, /)")
                appendLine("• Phương trình bậc nhất một ẩn (`2x + 5 = 15`)")
                appendLine("• Phương trình bậc hai (`x^2 - 5x + 6 = 0`)")
                appendLine()
                appendLine("💡 Để trò chuyện tự do và giải mọi dạng toán nâng cao, hãy cấu hình `GEMINI_API_KEY` trong file `local.properties` nhé!")
            }
            appendOrUpdateAiMessage(greetingReply)
            _isGenerating.value = false
            return
        }

        if (isHelpOrAbout(prompt)) {
            val aboutReply = buildString {
                appendLine("Tôi là Gia sư AI của ứng dụng Smart AI Calculator! 🎓")
                appendLine("Tôi có thể phân tích và giải chi tiết từng bước cho bạn.")
                appendLine("Hãy thử nhập một phép tính hoặc phương trình như `5x - 4 = 2x + 5`, hoặc bấm tab \"Scan\" để chụp từ vở bài tập nhé!")
            }
            appendOrUpdateAiMessage(aboutReply)
            _isGenerating.value = false
            return
        }

        // Mathematical solving fallback
        val mathTarget = extractMathExpression(prompt)
        val solution = mathSolver.solve(mathTarget)

        val aiResponseText = if (solution.finalAnswer == "Không thể tính toán") {
            buildString {
                appendLine("Tôi chưa nhận dạng được công thức toán học từ nội dung: \"$prompt\".")
                appendLine()
                appendLine("💡 Bạn có thể:")
                appendLine("1. Nhập phương trình dạng `ax + b = cx + d` hoặc `ax^2 + bx + c = 0`.")
                appendLine("2. Thêm `GEMINI_API_KEY` vào `local.properties` để trò chuyện và giải mọi câu hỏi bằng AI.")
            }
        } else {
            if (isRewrite) {
                "💡 Lời giải chi tiết lại cho bài toán:\n\n" + solution.explanation
            } else {
                solution.explanation
            }
        }

        appendOrUpdateAiMessage(aiResponseText, solution)
        _isGenerating.value = false
    }

    private fun appendOrUpdateAiMessage(text: String, solution: StepByStepMathSolver.MathSolution? = null) {
        val currentList = _messages.value
        val lastMsg = currentList.lastOrNull()
        if (lastMsg != null && !lastMsg.isFromUser && lastMsg.text.isEmpty()) {
            _messages.value = currentList.dropLast(1) + lastMsg.copy(text = text, solution = solution)
        } else {
            _messages.value = currentList + ChatMessage(text = text, isFromUser = false, solution = solution)
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

package dhn.intern.smart_ai_caculator_app.domain.ai

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import dhn.intern.smart_ai_caculator_app.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GeminiMathService(
    private val customKeyProvider: () -> String? = { null }
) {
    companion object {
        const val MODEL_NAME = "gemini-1.5-flash"
        const val SYSTEM_PROMPT = """
Bạn là "Gia sư AI" thông minh, thân thiện, ân cần và chuyên nghiệp trong ứng dụng Smart AI Calculator.
Nhiệm vụ của bạn:
1. Giao tiếp & Trò chuyện: Nếu người dùng chào hỏi, tâm sự, hỏi thăm hoặc hỏi các câu hỏi kiến thức đời sống, học tập nói chung:
   - Hãy phản hồi tự nhiên, gần gũi, ấm áp bằng tiếng Việt.
   - Sẵn sàng đồng hành và hỗ trợ người dùng trong học tập và rèn luyện tư duy.
2. Gia sư Toán học & Khoa học: Nếu người dùng đưa ra câu hỏi toán học, phương trình, hình học, số học, bài toán đố:
   - Nhận diện bản chất bài toán và phương pháp giải tối ưu.
   - Giải chi tiết từng bước mạch lạc, sư phạm, dễ hiểu cho học sinh.
   - Trình bày công thức sạch đẹp bằng định dạng Markdown rõ ràng.
   - Luôn có kết luận đáp số nổi bật ở cuối bài giải.
3. Phong cách: Luôn lịch sự, khuyến khích tư duy, động viên người học và mang lại trải nghiệm học tập tích cực.
"""
    }

    fun getEffectiveApiKey(): String {
        val userKey = customKeyProvider()?.trim()
        if (!userKey.isNullOrBlank()) return userKey
        return BuildConfig.GEMINI_API_KEY.trim()
    }

    fun isConfigured(): Boolean = getEffectiveApiKey().isNotBlank()

    fun generateResponseStream(prompt: String): Flow<String> = flow {
        val key = getEffectiveApiKey()
        if (key.isBlank()) {
            throw IllegalStateException("Chưa có Gemini API Key")
        }

        val model = GenerativeModel(
            modelName = MODEL_NAME,
            apiKey = key,
            systemInstruction = content {
                text(SYSTEM_PROMPT)
            }
        )

        model.generateContentStream(prompt).collect { response ->
            response.text?.let { chunk ->
                if (chunk.isNotEmpty()) {
                    emit(chunk)
                }
            }
        }
    }

    fun generateRewriteStream(originalPrompt: String): Flow<String> {
        val rewritePrompt = "Hãy giải thích lại thật chi tiết từng bước, rõ ràng và sư phạm hơn cho câu hỏi sau:\n$originalPrompt"
        return generateResponseStream(rewritePrompt)
    }

    suspend fun generateResponse(prompt: String): Result<String> {
        val key = getEffectiveApiKey()
        if (key.isBlank()) {
            return Result.failure(IllegalStateException("Chưa có Gemini API Key"))
        }

        return try {
            val model = GenerativeModel(
                modelName = MODEL_NAME,
                apiKey = key,
                systemInstruction = content {
                    text(SYSTEM_PROMPT)
                }
            )
            val response = model.generateContent(prompt)
            val text = response.text
            if (!text.isNullOrBlank()) {
                Result.success(text.trim())
            } else {
                Result.failure(Exception("AI không trả về nội dung."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateMultimodalResponse(prompt: String, bitmap: Bitmap): Result<String> {
        val key = getEffectiveApiKey()
        if (key.isBlank()) {
            return Result.failure(IllegalStateException("Chưa có Gemini API Key"))
        }

        return try {
            val model = GenerativeModel(
                modelName = MODEL_NAME,
                apiKey = key,
                systemInstruction = content {
                    text(SYSTEM_PROMPT)
                }
            )
            val inputContent = content {
                image(bitmap)
                text(prompt)
            }
            val response = model.generateContent(inputContent)
            val text = response.text
            if (!text.isNullOrBlank()) {
                Result.success(text.trim())
            } else {
                Result.failure(Exception("AI không trả về nội dung."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

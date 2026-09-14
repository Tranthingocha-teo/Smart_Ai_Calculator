package dhn.intern.smart_ai_caculator_app.domain.ai

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import dhn.intern.smart_ai_caculator_app.BuildConfig

class GeminiMathService(
    private val customKeyProvider: () -> String? = { null }
) {
    companion object {
        const val MODEL_NAME = "gemini-1.5-flash"
        const val SYSTEM_PROMPT = """
Bạn là "Gia sư Toán học AI" thông minh, ân cần và chuyên nghiệp.
Nhiệm vụ của bạn:
1. Nếu người dùng chào hỏi, trò chuyện hoặc hỏi thông tin chung, hãy chào đón thân thiện, tự nhiên bằng tiếng Việt và giới thiệu bạn có thể giải toán, giải thích công thức, giải đề.
2. Nếu người dùng đưa ra bài toán, phương trình, bài toán đố, hình học, vật lý:
   - Phân tích đề bài và nhận dạng dạng toán.
   - Giải chi tiết từng bước bằng tiếng Việt sư phạm dễ hiểu.
   - Trình bày công thức sạch đẹp bằng ký hiệu Markdown.
   - Đưa ra kết luận đáp số nổi bật ở cuối.
3. Luôn giữ phong cách giao tiếp khích lệ, thân thiện và chính xác.
"""
    }

    fun getEffectiveApiKey(): String {
        val userKey = customKeyProvider()?.trim()
        if (!userKey.isNullOrBlank()) return userKey
        return BuildConfig.GEMINI_API_KEY.trim()
    }

    fun isConfigured(): Boolean = getEffectiveApiKey().isNotBlank()

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

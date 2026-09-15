package dhn.intern.smart_ai_caculator_app.data.preferences.ai

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.aiDataStore by preferencesDataStore(name = "ai_prefs")

object AiPreferenceKeys {
    val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
}

class AiPreferences(private val context: Context) {
    @Volatile
    var cachedKey: String = ""
        private set

    val apiKey: Flow<String> = context.aiDataStore.data.map { preferences ->
        val key = preferences[AiPreferenceKeys.GEMINI_API_KEY] ?: ""
        cachedKey = key
        key
    }

    suspend fun saveApiKey(key: String) {
        val trimmed = key.trim()
        cachedKey = trimmed
        context.aiDataStore.edit { preferences ->
            preferences[AiPreferenceKeys.GEMINI_API_KEY] = trimmed
        }
    }
}

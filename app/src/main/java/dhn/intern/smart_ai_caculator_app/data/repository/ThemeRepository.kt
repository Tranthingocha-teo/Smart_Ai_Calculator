package dhn.intern.smart_ai_caculator_app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import dhn.intern.smart_ai_caculator_app.data.preferences.themes.ThemeKeys
import dhn.intern.smart_ai_caculator_app.data.preferences.themes.themeDataStore
import kotlinx.coroutines.flow.map

class ThemeRepository(private val context: Context) {

    val isDarkModeFlow = context.themeDataStore.data
        .map { prefs ->
            prefs[ThemeKeys.DARK_MODE] ?: false
        }

    suspend fun setDarkMode(enabled: Boolean) {
        context.themeDataStore.edit {
            it[ThemeKeys.DARK_MODE] = enabled
        }
    }
}
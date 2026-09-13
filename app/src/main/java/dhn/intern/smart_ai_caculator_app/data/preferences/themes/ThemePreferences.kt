package dhn.intern.smart_ai_caculator_app.data.preferences.themes

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.themeDataStore by preferencesDataStore(name = "theme_prefs")

object ThemeKeys {
    val DARK_MODE = booleanPreferencesKey("dark_mode")
}

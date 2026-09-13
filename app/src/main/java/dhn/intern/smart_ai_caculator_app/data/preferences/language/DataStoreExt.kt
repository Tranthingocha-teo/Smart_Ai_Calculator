package dhn.intern.smart_ai_caculator_app.data.preferences.language


import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.languageDataStore by preferencesDataStore(
    name = "language_prefs"
)

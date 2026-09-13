package dhn.intern.smart_ai_caculator_app.data.preferences.language

import android.app.Activity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

class LocaleManager {
    fun changeLanguage(code: String, activity: Activity? = null) {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(code)
        )
    }
}



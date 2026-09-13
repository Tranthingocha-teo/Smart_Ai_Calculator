package dhn.intern.smart_ai_caculator_app.data.preferences.language

import dhn.intern.smart_ai_caculator_app.R

fun languageNameRes(code: String): Int {
    return when (code) {
        "en" -> R.string.language_english      // English
        "zh" -> R.string.language_chinese      // Chinese
        "hi" -> R.string.language_hindi        // Hindi
        "fr" -> R.string.language_french       // French
        "ar" -> R.string.language_arabic       // Arabic
        "it" -> R.string.language_italian      // Italian
        "es" -> R.string.language_spanish      // Spanish
        "bn" -> R.string.language_bengali      // Bengali
        "ru" -> R.string.language_russian      // Russian
        "pt" -> R.string.language_portuguese   // Portuguese
        else -> R.string.language_english
    }
}

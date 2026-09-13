package dhn.intern.smart_ai_caculator_app.data.preferences.language

import dhn.intern.smart_ai_caculator_app.R
import dhn.intern.smart_ai_caculator_app.data.model.LanguageUi

object LanguageData {

    fun getLanguages(selectedCode: String): List<LanguageUi> {
                return listOf(
                    LanguageUi("en", R.string.language_english, "🇺🇸", selectedCode == "en"),
                    LanguageUi("zh", R.string.language_chinese, "🇨🇳", selectedCode == "zh"),
                    LanguageUi("hi", R.string.language_hindi, "🇮🇳", selectedCode == "hi"),
                    LanguageUi("fr", R.string.language_french, "🇫🇷", selectedCode == "fr"),
                    LanguageUi("ar", R.string.language_arabic, "🇸🇦", selectedCode == "ar"),
                    LanguageUi("it", R.string.language_italian, "🇮🇹", selectedCode == "it"),
                    LanguageUi("es", R.string.language_spanish, "🇪🇸", selectedCode == "es"),
                    LanguageUi("bn", R.string.language_bengali, "🇧🇩", selectedCode == "bn"),
                    LanguageUi("ru", R.string.language_russian, "🇷🇺", selectedCode == "ru"),
                    LanguageUi("pt", R.string.language_portuguese, "🇵🇹", selectedCode == "pt")
                )
            }

}

package dhn.intern.smart_ai_caculator_app.ui.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dhn.intern.smart_ai_caculator_app.data.preferences.language.LocaleManager
import dhn.intern.smart_ai_caculator_app.data.repository.LanguageRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LanguageViewModel(
    private val repository: LanguageRepository,
    private val localeManager: LocaleManager
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading
    val selectedCode = repository.selectedLanguageFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "en"
        )

    fun selectLanguage(code: String, activity: Activity?) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.saveLanguage(code)
                localeManager.changeLanguage(code)
                delay(200)
            } finally {
                _isLoading.value = false
            }
        }
    }
}


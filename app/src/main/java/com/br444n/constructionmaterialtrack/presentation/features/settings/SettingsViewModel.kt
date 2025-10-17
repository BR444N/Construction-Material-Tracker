package com.br444n.constructionmaterialtrack.presentation.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br444n.constructionmaterialtrack.ui.theme.ThemeManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isDarkTheme: Boolean = false,
    val currentLanguage: String = "en",
    val showLanguageDialog: Boolean = false
)

class SettingsViewModel(
    private val themeManager: ThemeManager? = null
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(
        SettingsUiState(
            isDarkTheme = themeManager?.themeState?.isDarkTheme ?: false,
            currentLanguage = themeManager?.themeState?.currentLanguage ?: "en"
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    fun toggleTheme() {
        viewModelScope.launch {
            val newTheme = !_uiState.value.isDarkTheme
            _uiState.value = _uiState.value.copy(isDarkTheme = newTheme)
            themeManager?.updateTheme(newTheme)
        }
    }
    
    fun showLanguageDialog() {
        _uiState.value = _uiState.value.copy(showLanguageDialog = true)
    }
    
    fun hideLanguageDialog() {
        _uiState.value = _uiState.value.copy(showLanguageDialog = false)
    }
    
    fun selectLanguage(languageCode: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                currentLanguage = languageCode,
                showLanguageDialog = false
            )
            themeManager?.updateLanguage(languageCode)
        }
    }
    
    fun getLanguageDisplayName(code: String): String {
        return themeManager?.getLanguageDisplayName(code) ?: "English"
    }
}
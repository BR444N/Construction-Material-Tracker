package com.br444n.constructionmaterialtrack.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class ThemeState(
    val isDarkTheme: Boolean = false,
    val currentLanguage: String = "en"
)

val LocalThemeState = compositionLocalOf { ThemeState() }

class ThemeManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    
    var themeState by mutableStateOf(
        ThemeState(
            isDarkTheme = prefs.getBoolean("is_dark_theme", false),
            currentLanguage = prefs.getString("current_language", "en") ?: "en"
        )
    )
        private set
    
    fun updateTheme(isDarkTheme: Boolean) {
        themeState = themeState.copy(isDarkTheme = isDarkTheme)
        prefs.edit().putBoolean("is_dark_theme", isDarkTheme).apply()
    }
    
    fun updateLanguage(language: String) {
        themeState = themeState.copy(currentLanguage = language)
        prefs.edit().putString("current_language", language).apply()
    }
}

@Composable
fun ProvideThemeState(
    themeManager: ThemeManager,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalThemeState provides themeManager.themeState,
        content = content
    )
}
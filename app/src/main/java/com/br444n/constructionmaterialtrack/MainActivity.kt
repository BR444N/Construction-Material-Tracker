package com.br444n.constructionmaterialtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import android.content.Context
import com.br444n.constructionmaterialtrack.presentation.navigation.ArchitectProjectNavigationWithViewModels
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme
import com.br444n.constructionmaterialtrack.ui.theme.ThemeManager
import com.br444n.constructionmaterialtrack.ui.theme.ProvideThemeState
import com.br444n.constructionmaterialtrack.utils.LocaleManager

class MainActivity : ComponentActivity() {
    
    private lateinit var themeManager: ThemeManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Apply saved language before anything else
        applySavedLanguage()
        
        // Initialize ThemeManager
        themeManager = ThemeManager(this)
        
        enableEdgeToEdge()
        
        setContent {
            ProvideThemeState(themeManager = themeManager) {
                ConstructionMaterialTrackTheme(
                    darkTheme = themeManager.themeState.isDarkTheme
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ArchitectProjectNavigationWithViewModels(
                            themeManager = themeManager
                        )
                    }
                }
            }
        }
    }
    
    private fun applySavedLanguage() {
        val savedLanguage = LocaleManager.getSavedLanguage(this)
        LocaleManager.setLocale(this, savedLanguage)
    }
    
    override fun attachBaseContext(newBase: Context?) {
        val savedLanguage = newBase?.let { LocaleManager.getSavedLanguage(it) } ?: "en"
        val context = newBase?.let { LocaleManager.setLocale(it, savedLanguage) }
        super.attachBaseContext(context ?: newBase)
    }
}


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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.br444n.constructionmaterialtrack.presentation.navigation.ArchitectProjectNavigationWithViewModels
import com.br444n.constructionmaterialtrack.ui.theme.AppTheme
import com.br444n.constructionmaterialtrack.ui.theme.ThemeManager
import com.br444n.constructionmaterialtrack.utils.LocaleManager

class MainActivity : ComponentActivity() {
    
    private lateinit var themeManager: ThemeManager
    private var shortcutAction by mutableStateOf<String?>(null)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        // Apply saved language before anything else
        applySavedLanguage()
        
        // Initialize ThemeManager
        themeManager = ThemeManager(this)
        
        // Check if launched from shortcut
        shortcutAction = intent?.getStringExtra("shortcut_action")
        
        enableEdgeToEdge()
        
        setContent {
            AppTheme(themeManager = themeManager) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ArchitectProjectNavigationWithViewModels(
                        themeManager = themeManager,
                        shortcutAction = shortcutAction
                    )
                }
            }
        }
    }
    
    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        // Handle shortcut when app is already running
        shortcutAction = intent.getStringExtra("shortcut_action")
    }
    
    private fun applySavedLanguage() {
        val savedLanguage = LocaleManager.getSavedLanguage(this)
        LocaleManager.setLocale(this, savedLanguage)
    }
    
    override fun attachBaseContext(newBase: Context?) {
        if (newBase != null) {
            val savedLanguage = LocaleManager.getSavedLanguage(newBase)
            val context = LocaleManager.setLocale(newBase, savedLanguage)
            super.attachBaseContext(context)
        } else {
            super.attachBaseContext(newBase)
        }
    }
}


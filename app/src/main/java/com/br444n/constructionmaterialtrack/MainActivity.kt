package com.br444n.constructionmaterialtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.br444n.constructionmaterialtrack.presentation.navigation.ArchitectProjectNavigationWithViewModels
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme
import com.br444n.constructionmaterialtrack.ui.theme.ThemeManager
import com.br444n.constructionmaterialtrack.ui.theme.ProvideThemeState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeManager = remember { ThemeManager(this@MainActivity) }
            
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
}


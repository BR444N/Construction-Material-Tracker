package com.br444n.constructionmaterialtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.br444n.constructionmaterialtrack.presentation.navigation.ArchitectProjectNavigationWithViewModels
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConstructionMaterialTrackTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ArchitectProjectNavigationWithViewModels()
                }
            }
        }
    }
}


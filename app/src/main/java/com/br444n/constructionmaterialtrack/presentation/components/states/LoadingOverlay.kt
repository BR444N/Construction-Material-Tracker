package com.br444n.constructionmaterialtrack.presentation.components.states

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.br444n.constructionmaterialtrack.R
import kotlinx.coroutines.delay

@Composable
fun LoadingOverlay(
    message: String,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    minDurationMs: Long = 3000L, // 3 segundos por defecto
    onMinDurationComplete: (() -> Unit)? = null
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.spinner)
    )
    
    // Estado para controlar si ha pasado el tiempo mínimo
    var hasMinTimeElapsed by remember { mutableStateOf(false) }
    
    // Efecto para establecer el tiempo mínimo
    LaunchedEffect(isVisible) {
        if (isVisible) {
            hasMinTimeElapsed = false
            delay(minDurationMs)
            hasMinTimeElapsed = true
            onMinDurationComplete?.invoke()
        }
    }
    
    if (isVisible) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(32.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(message)
                }
            }
        }
    }
}

// Versión simplificada para compatibilidad hacia atrás
@Composable
fun LoadingOverlay(
    message: String,
    modifier: Modifier = Modifier
) {
    LoadingOverlay(
        message = message,
        modifier = modifier,
        isVisible = true,
        minDurationMs = 3000L
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewLoadingOverlay(modifier: Modifier = Modifier) {
    LoadingOverlay(
        message = "Loading...",
        modifier = modifier
    )
}
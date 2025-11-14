package com.br444n.constructionmaterialtrack.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BlueLight,
    onPrimary = Black,
    secondary = SecondaryLight,
    background = BackgroundDark,
    surface = SurfaceDark,
    onBackground = SurfaceLight, // Texto blanco en modo oscuro
    onSurface = SurfaceLight,    // Texto blanco en superficies oscuras
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = onSurfaceVariant,
    secondary = Secondary,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = Black,        // Texto negro en modo claro
    onSurface = Black,          // Texto negro en superficies claras
)

@Composable
fun ConstructionMaterialTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun AppTheme(
    themeManager: ThemeManager,
    content: @Composable () -> Unit
) {
    val themeState = themeManager.themeState
    
    ProvideThemeState(themeManager = themeManager) {
        ConstructionMaterialTrackTheme(
            darkTheme = themeState.isDarkTheme,
            content = content
        )
    }
}
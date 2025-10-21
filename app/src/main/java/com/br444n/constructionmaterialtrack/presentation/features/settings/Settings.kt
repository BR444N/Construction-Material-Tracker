package com.br444n.constructionmaterialtrack.presentation.features.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.constructionmaterialtrack.presentation.components.navigation.CustomTopAppBar
import com.br444n.constructionmaterialtrack.presentation.components.settings.LanguageSelectionDialog
import com.br444n.constructionmaterialtrack.presentation.components.settings.SettingsItem
import com.br444n.constructionmaterialtrack.presentation.components.settings.SettingsSwitch
import com.br444n.constructionmaterialtrack.ui.theme.BlueLight
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onThemeChanged: (Boolean) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Notify parent about theme changes
    LaunchedEffect(uiState.isDarkTheme) {
        onThemeChanged(uiState.isDarkTheme)
    }
    
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = stringResource(R.string.settings),
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                // Theme Section
                Text(
                    text = stringResource(R.string.appearance),
                    style = MaterialTheme.typography.titleMedium,
                    color = BluePrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                SettingsSwitch(
                    title = stringResource(R.string.dark_theme),
                    subtitle = if (uiState.isDarkTheme) {
                        stringResource(R.string.dark_mode_enabled)
                    } else {
                        stringResource(R.string.light_mode_enabled)
                    },
                    icon = Icons.Default.DarkMode,
                    checked = uiState.isDarkTheme,
                    onCheckedChange = { viewModel.toggleTheme() }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    thickness = DividerDefaults.Thickness, color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            item {
                // Language Section
                Text(
                    text = stringResource(R.string.language),
                    style = MaterialTheme.typography.titleMedium,
                    color = BluePrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                SettingsItem(
                    title = stringResource(R.string.language),
                    subtitle = viewModel.getLanguageDisplayName(uiState.currentLanguage),
                    icon = Icons.Default.Translate,
                    onClick = { viewModel.showLanguageDialog() }
                )
            }
        }
        
        // Language Selection Dialog
        if (uiState.showLanguageDialog) {
            LanguageSelectionDialog(
                currentLanguage = uiState.currentLanguage,
                onLanguageSelected = { viewModel.selectLanguage(it) },
                onDismiss = { viewModel.hideLanguageDialog() }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Light Theme")
@Composable
fun SettingsScreenLightPreview() {
    ConstructionMaterialTrackTheme(darkTheme = false) {
        SettingsScreenContent(
            isDarkTheme = false,
            currentLanguage = "en",
            showLanguageDialog = false
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Dark Theme")
@Composable
fun SettingsScreenDarkPreview() {
    ConstructionMaterialTrackTheme(darkTheme = true) {
        SettingsScreenContent(
            isDarkTheme = true,
            currentLanguage = "es",
            showLanguageDialog = false
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "With Language Dialog")
@Composable
fun SettingsScreenWithDialogPreview() {
    ConstructionMaterialTrackTheme {
        SettingsScreenContent(
            isDarkTheme = false,
            currentLanguage = "en",
            showLanguageDialog = true
        )
    }
}

@Composable
private fun SettingsScreenContent(
    isDarkTheme: Boolean,
    currentLanguage: String,
    showLanguageDialog: Boolean
) {
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = "Settings", // Preview uses hardcoded string
                onBackClick = {}
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                // Theme Section
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                SettingsSwitch(
                    title = "Dark Theme",
                    subtitle = if (isDarkTheme) "Dark mode enabled" else "Light mode enabled",
                    icon = Icons.Default.DarkMode,
                    checked = isDarkTheme,
                    onCheckedChange = {}
                )
                
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
            
            item {
                // Language Section
                Text(
                    text = "Language",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                SettingsItem(
                    title = "Language",
                    subtitle = when (currentLanguage) {
                        "en" -> "English"
                        "es" -> "Español"
                        "fr" -> "Français"
                        else -> "English"
                    },
                    icon = Icons.Default.Language,
                    onClick = {}
                )
            }
        }
        
        // Language Selection Dialog
        if (showLanguageDialog) {
            LanguageSelectionDialog(
                currentLanguage = currentLanguage,
                onLanguageSelected = {},
                onDismiss = {}
            )
        }
    }
}
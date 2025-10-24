package com.br444n.constructionmaterialtrack.presentation.components.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.br444n.constructionmaterialtrack.R
import com.br444n.constructionmaterialtrack.domain.model.LanguageConstants
import com.br444n.constructionmaterialtrack.ui.theme.*

@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val languages = LanguageConstants.SUPPORTED_LANGUAGES
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = BluePrimary)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.select_language),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                
                Column(
                    modifier = Modifier.selectableGroup()
                ) {
                    languages.forEach { language ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = currentLanguage == language.code,
                                    onClick = { onLanguageSelected(language.code) },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentLanguage == language.code,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colorScheme.onSurface,
                                    unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column {
                                Text(
                                    text = language.nativeName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (currentLanguage == language.code) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = language.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "English Selected")
@Composable
private fun LanguageSelectionDialogEnglishPreview() {
    ConstructionMaterialTrackTheme {
        LanguageSelectionDialog(
            currentLanguage = "en",
            onLanguageSelected = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true, name = "Spanish Selected")
@Composable
private fun LanguageSelectionDialogSpanishPreview() {
    ConstructionMaterialTrackTheme {
        LanguageSelectionDialog(
            currentLanguage = "es",
            onLanguageSelected = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true, name = "French Selected")
@Composable
private fun LanguageSelectionDialogFrenchPreview() {
    ConstructionMaterialTrackTheme {
        LanguageSelectionDialog(
            currentLanguage = "fr",
            onLanguageSelected = {},
            onDismiss = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Theme", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LanguageSelectionDialogDarkPreview() {
    ConstructionMaterialTrackTheme {
        LanguageSelectionDialog(
            currentLanguage = "es",
            onLanguageSelected = {},
            onDismiss = {}
        )
    }
}
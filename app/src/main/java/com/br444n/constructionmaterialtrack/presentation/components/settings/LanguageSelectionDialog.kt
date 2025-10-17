package com.br444n.constructionmaterialtrack.presentation.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.br444n.constructionmaterialtrack.R
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

data class Language(
    val code: String,
    val name: String,
    val nativeName: String
)

@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val languages = listOf(
        Language("en", "English", stringResource(R.string.language_english)),
        Language("es", "Spanish", stringResource(R.string.language_spanish)),
        Language("fr", "French", stringResource(R.string.language_french))
    )
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.select_language),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 16.dp)
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
                                onClick = null
                            )
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column {
                                Text(
                                    text = language.nativeName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = language.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
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
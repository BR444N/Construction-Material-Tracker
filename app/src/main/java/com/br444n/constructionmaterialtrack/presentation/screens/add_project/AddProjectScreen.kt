package com.br444n.constructionmaterialtrack.presentation.screens.add_project

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.constructionmaterialtrack.presentation.components.forms.SecureTextField
import com.br444n.constructionmaterialtrack.presentation.components.forms.SecureTextFieldConfig
import com.br444n.constructionmaterialtrack.presentation.components.navigation.CustomTopAppBar
import com.br444n.constructionmaterialtrack.presentation.components.states.ErrorMessage
import com.br444n.constructionmaterialtrack.presentation.components.images.SecureImagePicker
import com.br444n.constructionmaterialtrack.presentation.hooks.ValidationType
import com.br444n.constructionmaterialtrack.presentation.components.buttons.SaveButton
import com.br444n.constructionmaterialtrack.presentation.components.buttons.SaveButtonConfig
import com.br444n.constructionmaterialtrack.presentation.components.buttons.SecondaryButton
import com.br444n.constructionmaterialtrack.presentation.components.buttons.SecondaryButtonConfig
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectScreen(
    viewModel: AddProjectViewModel,
    onBackClick: () -> Unit = {},
    onAddMaterialsClick: (String) -> Unit = {},
    onProjectSaved: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var imageValidationError by remember { mutableStateOf("") }
    
    // Handle project saved
    LaunchedEffect(uiState.projectSaved) {
        if (uiState.projectSaved != null) {
            onProjectSaved()
            viewModel.resetSavedState()
        }
    }
    
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = stringResource(R.string.new_project),
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Secure Image Picker Section
            SecureImagePicker(
                selectedImageUri = uiState.selectedImageUri?.toUri(),
                onImageSelected = { uri -> 
                    viewModel.setSelectedImageUri(uri?.toString())
                    imageValidationError = ""
                },
                onValidationError = { error ->
                    imageValidationError = error
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            // Secure Project Name Field
            SecureTextField(
                value = uiState.projectName,
                onValueChange = { viewModel.updateProjectName(it) },
                label = stringResource(R.string.project_name),
                modifier = Modifier.fillMaxWidth(),
                config = SecureTextFieldConfig(
                    validationType = ValidationType.PROJECT_NAME,
                    leadingIcon = Icons.Default.Badge
                )
            )
            
            // Secure Project Description Field
            SecureTextField(
                value = uiState.projectDescription,
                onValueChange = { viewModel.updateProjectDescription(it) },
                label = stringResource(R.string.project_description),
                modifier = Modifier.fillMaxWidth(),
                config = SecureTextFieldConfig(
                    validationType = ValidationType.DESCRIPTION,
                    leadingIcon = Icons.Default.Description,
                    singleLine = false,
                    maxLines = 5,
                    minLines = 3,
                    isRequired = false
                )
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Error messages
            if (uiState.errorMessage != null) {
                ErrorMessage(
                    message = uiState.errorMessage ?: "",
                    onDismiss = { viewModel.clearError() }
                )
            }
            
            if (imageValidationError.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "Image Error: $imageValidationError",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            
            // Action Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SecondaryButton(
                    text = stringResource(R.string.add_materials),
                    onClick = { 
                        uiState.projectSaved?.let { projectId ->
                            onAddMaterialsClick(projectId)
                        }
                    },
                    config = SecondaryButtonConfig(
                        enabled = uiState.projectSaved != null && !uiState.isSaving
                    )
                )
                
                SaveButton(
                    text = stringResource(R.string.save_project),
                    onClick = { viewModel.saveProject() },
                    config = SaveButtonConfig(
                        enabled = uiState.isFormValid && imageValidationError.isEmpty(),
                        isLoading = uiState.isSaving
                    )
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
private fun AddProjectScreenPreview() {
    ConstructionMaterialTrackTheme {
        AddProjectScreen(
            viewModel = viewModel()
        )
    }
}
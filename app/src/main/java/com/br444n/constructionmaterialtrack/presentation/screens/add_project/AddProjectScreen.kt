package com.br444n.constructionmaterialtrack.presentation.screens.add_project

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.constructionmaterialtrack.presentation.components.forms.CustomTextField
import com.br444n.constructionmaterialtrack.presentation.components.navigation.CustomTopAppBar
import com.br444n.constructionmaterialtrack.presentation.components.states.ErrorMessage
import com.br444n.constructionmaterialtrack.presentation.components.images.ImagePicker
import com.br444n.constructionmaterialtrack.presentation.components.forms.MultilineTextField
import com.br444n.constructionmaterialtrack.presentation.components.buttons.SaveButton
import com.br444n.constructionmaterialtrack.presentation.components.buttons.SecondaryButton
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProjectScreen(
    viewModel: AddProjectViewModel,
    onBackClick: () -> Unit = {},
    onAddMaterialsClick: (String) -> Unit = {},
    onProjectSaved: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
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
            // Image Picker Section
            ImagePicker(
                selectedImageUri = uiState.selectedImageUri?.let { Uri.parse(it) },
                onImageSelected = { uri -> viewModel.setSelectedImageUri(uri?.toString()) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            // Project Name Field
            CustomTextField(
                value = uiState.projectName,
                onValueChange = { viewModel.updateProjectName(it) },
                label = stringResource(R.string.project_name),
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.projectName.isBlank() && uiState.projectName.isNotEmpty()
            )
            
            // Project Description Field
            MultilineTextField(
                value = uiState.projectDescription,
                onValueChange = { viewModel.updateProjectDescription(it) },
                label = stringResource(R.string.project_description),
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Error message
            if (uiState.errorMessage != null) {
                ErrorMessage(
                    message = uiState.errorMessage ?: "",
                    onDismiss = { viewModel.clearError() }
                )
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
                    enabled = uiState.projectSaved != null && !uiState.isSaving
                )
                
                SaveButton(
                    text = stringResource(R.string.save_project),
                    onClick = { viewModel.saveProject() },
                    enabled = uiState.isFormValid,
                    isLoading = uiState.isSaving
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
package com.br444n.constructionmaterialtrack.presentation.screens.add_project

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.constructionmaterialtrack.presentation.components.ImagePicker
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
            TopAppBar(
                title = {
                    Text(
                        text = "New Project",
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
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
                onImageSelected = { uri -> viewModel.setSelectedImageUri(uri?.toString()) }
            )
            
            // Project Name Field
            OutlinedTextField(
                value = uiState.projectName,
                onValueChange = { viewModel.updateProjectName(it) },
                label = { Text("Project Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = uiState.projectName.isBlank() && uiState.projectName.isNotEmpty()
            )
            
            // Project Description Field
            OutlinedTextField(
                value = uiState.projectDescription,
                onValueChange = { viewModel.updateProjectDescription(it) },
                label = { Text("Project Description") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 3,
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Error message
            if (uiState.errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                }
            }
            
            // Action Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { 
                        uiState.projectSaved?.let { projectId ->
                            onAddMaterialsClick(projectId)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    enabled = uiState.projectSaved != null && !uiState.isSaving
                ) {
                    Text(
                        text = "Add Materials",
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                
                Button(
                    onClick = { viewModel.saveProject() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = uiState.isFormValid && !uiState.isSaving
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = "Save Project",
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
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
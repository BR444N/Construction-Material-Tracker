package com.br444n.constructionmaterialtrack.presentation.screens.project_details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.br444n.constructionmaterialtrack.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.constructionmaterialtrack.presentation.components.ActionButton
import com.br444n.constructionmaterialtrack.presentation.components.EditableProjectCard
import com.br444n.constructionmaterialtrack.presentation.components.ErrorContent
import com.br444n.constructionmaterialtrack.presentation.components.LoadingIndicator
import com.br444n.constructionmaterialtrack.presentation.components.MaterialItemRow
import com.br444n.constructionmaterialtrack.presentation.components.ProjectInfoCard
import com.br444n.constructionmaterialtrack.presentation.components.SectionHeader
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme
import com.br444n.constructionmaterialtrack.ui.theme.SurfaceLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailsScreen(
    viewModel: ProjectDetailsViewModel,
    projectId: String,
    onBackClick: () -> Unit = {},
    onAddMaterial: (String) -> Unit = {},
    onExportToPdf: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Load project when screen opens
    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }
    
    // Handle PDF exported
    LaunchedEffect(uiState.pdfExported) {
        if (uiState.pdfExported) {
            // Show success message or handle PDF export completion
            viewModel.clearPdfExported()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isEditMode) "Edit Project" else (uiState.project?.name ?: "Project Details"),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = SurfaceLight
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.isEditMode) {
                            viewModel.exitEditMode()
                        } else {
                            onBackClick()
                        }
                    }) {
                        Icon(
                            imageVector = if (uiState.isEditMode) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (uiState.isEditMode) "Cancel" else "Back",
                            tint = SurfaceLight
                        )
                    }
                },
                actions = {
                    if (!uiState.isEditMode && uiState.project != null) {
                        IconButton(onClick = { viewModel.enterEditMode() }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Project",
                                tint = SurfaceLight
                            )
                        }
                    } else if (uiState.isEditMode) {
                        IconButton(
                            onClick = { viewModel.saveProjectChanges() },
                            enabled = !uiState.isSavingProject && uiState.editProjectName.isNotBlank()
                        ) {
                            if (uiState.isSavingProject) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Save Changes"
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BluePrimary
            ))
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    text = "Loading project..."
                )
            }
            uiState.errorMessage != null -> {
                ErrorContent(
                    errorMessage = uiState.errorMessage ?: "",
                    onRetry = { viewModel.loadProject(projectId) },
                    onDismiss = { viewModel.clearError() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Project Header
                    uiState.project?.let { project ->
                        item {
                            if (uiState.isEditMode) {
                                EditableProjectCard(
                                    projectName = uiState.editProjectName,
                                    projectDescription = uiState.editProjectDescription,
                                    selectedImageUri = uiState.editSelectedImageUri,
                                    onNameChange = viewModel::updateEditProjectName,
                                    onDescriptionChange = viewModel::updateEditProjectDescription,
                                    onImageSelected = viewModel::updateEditSelectedImage
                                )
                            } else {
                                ProjectInfoCard(project = project)
                            }
                        }
                    }
                    
                    // Materials Section Header
                    item {
                        SectionHeader(title = "Materials")
                    }
                    
                    if (uiState.materials.isEmpty()) {
                        item {
                            EmptyMaterialsContent()
                        }
                    } else {
                        items(uiState.materials) { material ->
                            MaterialItemRow(
                                material = material,
                                onCheckedChange = { isChecked ->
                                    viewModel.updateMaterialStatus(material, isChecked)
                                }
                            )
                        }
                    }
                    
                    // Add Materials Button (only show when in edit mode)
                    if (uiState.isEditMode) {
                        item {
                            ActionButton(
                                text = "Add Materials",
                                icon = Icons.Default.Add,
                                onClick = { 
                                    uiState.project?.let { project ->
                                        onAddMaterial(project.id)
                                    }
                                },
                                isOutlined = true
                            )
                        }
                    }
                    
                    // Export Button (only show when not in edit mode)
                    if (!uiState.isEditMode) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            ActionButton(
                                text = "Export to PDF",
                                iconPainter = painterResource(id = R.drawable.export_pdf),
                                onClick = { 
                                    uiState.project?.let { project ->
                                        onExportToPdf(project.id)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BluePrimary
                                ),
                                textColor = SurfaceLight,
                                preserveIconColor = true,
                                iconSize = 24.dp
                            )
                        }
                    }
                }
            }
        }
    }
}





@Composable
private fun EmptyMaterialsContent(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No materials added yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
private fun ProjectDetailsScreenPreview() {
    ConstructionMaterialTrackTheme {
        ProjectDetailsScreen(
            viewModel = viewModel(),
            projectId = "1"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyProjectDetailsScreenPreview() {
    ConstructionMaterialTrackTheme {
        ProjectDetailsScreen(
            viewModel = viewModel(),
            projectId = "1"
        )
    }
}
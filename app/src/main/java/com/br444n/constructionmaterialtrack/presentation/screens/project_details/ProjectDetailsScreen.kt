package com.br444n.constructionmaterialtrack.presentation.screens.project_details

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.br444n.constructionmaterialtrack.R
import com.br444n.constructionmaterialtrack.domain.model.Material
import com.br444n.constructionmaterialtrack.domain.model.Project
import com.br444n.constructionmaterialtrack.presentation.components.ImagePicker
import com.br444n.constructionmaterialtrack.presentation.components.MaterialItemRow
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailsScreen(
    viewModel: ProjectDetailsViewModel,
    projectId: String,
    onBackClick: () -> Unit = {}
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
                        overflow = TextOverflow.Ellipsis
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
                            contentDescription = if (uiState.isEditMode) "Cancel" else "Back"
                        )
                    }
                },
                actions = {
                    if (!uiState.isEditMode && uiState.project != null) {
                        IconButton(onClick = { viewModel.enterEditMode() }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Project"
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
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
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
                                EditProjectHeader(
                                    projectName = uiState.editProjectName,
                                    projectDescription = uiState.editProjectDescription,
                                    selectedImageUri = uiState.editSelectedImageUri,
                                    onNameChange = viewModel::updateEditProjectName,
                                    onDescriptionChange = viewModel::updateEditProjectDescription,
                                    onImageSelected = viewModel::updateEditSelectedImage
                                )
                            } else {
                                ProjectHeader(project = project)
                            }
                        }
                    }
                    
                    // Materials Section
                    item {
                        Text(
                            text = "Materials",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )
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
                    
                    // Export Button (only show when not in edit mode)
                    if (!uiState.isEditMode) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.exportToPdf() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ),
                                enabled = !uiState.isExportingPdf
                            ) {
                                if (uiState.isExportingPdf) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onSecondary
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.PictureAsPdf,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (uiState.isExportingPdf) "Exporting..." else "Export to PDF",
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditProjectHeader(
    projectName: String,
    projectDescription: String,
    selectedImageUri: Uri?,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onImageSelected: (Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Image Picker
            ImagePicker(
                selectedImageUri = selectedImageUri,
                onImageSelected = onImageSelected,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Project Name Field
            OutlinedTextField(
                value = projectName,
                onValueChange = onNameChange,
                label = { Text("Project Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = projectName.isBlank()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Project Description Field
            OutlinedTextField(
                value = projectDescription,
                onValueChange = onDescriptionChange,
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
        }
    }
}

@Composable
private fun ProjectHeader(
    project: Project,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Project Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                when {
                    project.imageUri != null -> {
                        AsyncImage(
                            model = Uri.parse(project.imageUri),
                            contentDescription = "Project Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    project.imageRes != null -> {
                        Image(
                            painter = painterResource(id = project.imageRes),
                            contentDescription = "Project Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    else -> {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Default Project Image",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Project Info
            Text(
                text = project.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            if (project.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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

@Composable
private fun ErrorContent(
    errorMessage: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.PictureAsPdf,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Error loading project",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onDismiss) {
                Text("Dismiss")
            }
            Button(onClick = onRetry) {
                Text("Retry")
            }
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
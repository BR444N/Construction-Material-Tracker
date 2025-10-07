package com.br444n.constructionmaterialtrack.presentation.screens.project_list


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.constructionmaterialtrack.domain.model.Project
import com.br444n.constructionmaterialtrack.presentation.components.AppTopAppBar
import com.br444n.constructionmaterialtrack.presentation.components.ConfirmationDialog
import com.br444n.constructionmaterialtrack.presentation.components.EmptyState
import com.br444n.constructionmaterialtrack.presentation.components.ErrorContent
import com.br444n.constructionmaterialtrack.presentation.components.LoadingIndicator
import com.br444n.constructionmaterialtrack.presentation.components.LoadingOverlay
import com.br444n.constructionmaterialtrack.presentation.components.ProjectCard
import com.br444n.constructionmaterialtrack.presentation.components.SelectionTopAppBar
import com.br444n.constructionmaterialtrack.ui.theme.BlueDark
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme
import com.br444n.constructionmaterialtrack.ui.theme.SurfaceLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListScreen(
    viewModel: ProjectListViewModel,
    onAddProject: () -> Unit = {},
    onProjectClick: (Project) -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            if (uiState.isSelectionMode) {
                SelectionTopAppBar(
                    selectedCount = uiState.selectedProjects.size,
                    onExitSelection = { viewModel.exitSelectionMode() },
                    onSelectAll = { viewModel.selectAllProjects() },
                    onDelete = { viewModel.showDeleteDialog() }
                )
            } else {
                AppTopAppBar(
                    onSettingsClick = onSettingsClick
                )
            }
        },
        floatingActionButton = {
            if (!uiState.isSelectionMode) {
                FloatingActionButton(
                    onClick = onAddProject,
                    shape = CircleShape,
                    containerColor = BlueDark
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Project",
                        tint = SurfaceLight
                    )
                }
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    text = "Loading projects..."
                )
            }
            uiState.errorMessage != null -> {
                ErrorContent(
                    errorMessage = uiState.errorMessage ?: "",
                    onRetry = { viewModel.refreshProjects() },
                    onDismiss = { viewModel.clearError() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            uiState.projects.isEmpty() -> {
                EmptyState(
                    title = "No projects yet",
                    message = "Click the + button to add your first project.",
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.projects) { project ->
                        ProjectCard(
                            project = project,
                            onClick = {
                                if (uiState.isSelectionMode) {
                                    viewModel.toggleProjectSelection(project.id)
                                } else {
                                    onProjectClick(project)
                                }
                            },
                            onLongClick = {
                                if (!uiState.isSelectionMode) {
                                    viewModel.enterSelectionMode(project.id)
                                }
                            },
                            isSelected = uiState.selectedProjects.contains(project.id),
                            isSelectionMode = uiState.isSelectionMode
                        )
                    }
                }
            }
        }
        
        // Delete Confirmation Dialog
        if (uiState.showDeleteDialog) {
            val projectCount = uiState.selectedProjects.size
            ConfirmationDialog(
                title = "Delete Project${if (projectCount > 1) "s" else ""}?",
                message = if (projectCount == 1) {
                    "Are you sure you want to delete this project? This action cannot be undone."
                } else {
                    "Are you sure you want to delete these $projectCount projects? This action cannot be undone."
                },
                icon = Icons.Default.Delete,
                confirmText = "Delete",
                onConfirm = { viewModel.deleteSelectedProjects() },
                onDismiss = { viewModel.hideDeleteDialog() },
                isDestructive = true
            )
        }
        
        // Loading overlay for deletion
        if (uiState.isDeleting) {
            LoadingOverlay(message = "Deleting projects...")
        }
    }
}





@Preview(showBackground = true)
@Composable
private fun ProjectListScreenPreview() {
    ConstructionMaterialTrackTheme {
        ProjectListScreen(
            viewModel = viewModel()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyProjectListScreenPreview() {
    ConstructionMaterialTrackTheme {
        ProjectListScreen(
            viewModel = viewModel()
        )
    }
}
package com.br444n.constructionmaterialtrack.presentation.screens.project_list

import com.br444n.constructionmaterialtrack.R
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.constructionmaterialtrack.domain.model.Project
import com.br444n.constructionmaterialtrack.presentation.components.navigation.AppTopAppBar
import com.br444n.constructionmaterialtrack.presentation.components.dialogs.ConfirmationDialog
import com.br444n.constructionmaterialtrack.presentation.model.ConfirmationDialogConfig
import com.br444n.constructionmaterialtrack.presentation.components.states.EmptyState
import com.br444n.constructionmaterialtrack.presentation.components.states.ErrorContent
import com.br444n.constructionmaterialtrack.presentation.components.states.LoadingIndicator
import com.br444n.constructionmaterialtrack.presentation.components.cards.ProjectCard
import com.br444n.constructionmaterialtrack.presentation.components.navigation.SelectionTopAppBar
import com.br444n.constructionmaterialtrack.ui.theme.Black
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme


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
            ProjectListTopBar(
                uiState = uiState,
                viewModel = viewModel,
                onSettingsClick = onSettingsClick
            )
        },
        floatingActionButton = {
            if (!uiState.isSelectionMode) {
                AddProjectFab(onAddProject = onAddProject)
            }
        }
    ) { paddingValues ->
        ProjectListContent(
            uiState = uiState,
            viewModel = viewModel,
            onProjectClick = onProjectClick,
            paddingValues = paddingValues
        )
        
        ProjectListDialogs(
            uiState = uiState,
            viewModel = viewModel
        )
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
// Helper composables to reduce cognitive complexity
@Composable
private fun ProjectListTopBar(
    uiState: ProjectListUiState,
    viewModel: ProjectListViewModel,
    onSettingsClick: () -> Unit
) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddProjectFab(onAddProject: () -> Unit) {
    val density = LocalDensity.current
    
    TooltipBox(
        positionProvider = object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset {
                val spacingPx = with(density) { 8.dp.toPx().toInt() }
                val x = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
                val y = anchorBounds.top - popupContentSize.height - spacingPx
                val adjustedX = x.coerceIn(0, windowSize.width - popupContentSize.width)
                val adjustedY = y.coerceAtLeast(0)
                return IntOffset(adjustedX, adjustedY)
            }
        },
        tooltip = {
            PlainTooltip {
                Text(stringResource(R.string.add_project_tooltip), color = Black)
            }
        },
        state = remember { TooltipState() }
    ) {
        FloatingActionButton(
            onClick = onAddProject,
            shape = CircleShape,
            containerColor = BluePrimary
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_project_tooltip),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ProjectListContent(
    uiState: ProjectListUiState,
    viewModel: ProjectListViewModel,
    onProjectClick: (Project) -> Unit,
    paddingValues: PaddingValues
) {
    when {
        uiState.isLoading -> {
            LoadingState(paddingValues)
        }
        uiState.errorMessage != null -> {
            ErrorState(
                errorMessage = uiState.errorMessage,
                viewModel = viewModel,
                paddingValues = paddingValues
            )
        }
        uiState.projects.isEmpty() -> {
            EmptyProjectsState(paddingValues)
        }
        else -> {
            ProjectsList(
                uiState = uiState,
                viewModel = viewModel,
                onProjectClick = onProjectClick,
                paddingValues = paddingValues
            )
        }
    }
}

@Composable
private fun LoadingState(paddingValues: PaddingValues) {
    LoadingIndicator(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        text = stringResource(R.string.loading_projects)
    )
}

@Composable
private fun ErrorState(
    errorMessage: String?,
    viewModel: ProjectListViewModel,
    paddingValues: PaddingValues
) {
    ErrorContent(
        errorMessage = errorMessage ?: "",
        onRetry = { viewModel.refreshProjects() },
        onDismiss = { viewModel.clearError() },
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    )
}

@Composable
private fun EmptyProjectsState(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        EmptyState(
            title = stringResource(R.string.no_projects),
            message = stringResource(R.string.no_projects_message),
            drawableRes = R.drawable.state_image,
            imageSize = 350.dp,
            showCard = false,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
private fun ProjectsList(
    uiState: ProjectListUiState,
    viewModel: ProjectListViewModel,
    onProjectClick: (Project) -> Unit,
    paddingValues: PaddingValues
) {
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
                    handleProjectClick(
                        project = project,
                        isSelectionMode = uiState.isSelectionMode,
                        onProjectClick = onProjectClick,
                        onToggleSelection = { viewModel.toggleProjectSelection(project.id) }
                    )
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

private fun handleProjectClick(
    project: Project,
    isSelectionMode: Boolean,
    onProjectClick: (Project) -> Unit,
    onToggleSelection: () -> Unit
) {
    if (isSelectionMode) {
        onToggleSelection()
    } else {
        onProjectClick(project)
    }
}

@Composable
private fun ProjectListDialogs(
    uiState: ProjectListUiState,
    viewModel: ProjectListViewModel
) {
    // Delete Confirmation Dialog
    if (uiState.showDeleteDialog) {
        DeleteConfirmationDialog(
            selectedCount = uiState.selectedProjects.size,
            onConfirm = { viewModel.deleteSelectedProjects() },
            onDismiss = { viewModel.hideDeleteDialog() }
        )
    }
    
    // Loading overlay for deletion
    if (uiState.isDeleting) {
        DeletingOverlay()
    }
}

@Composable
private fun DeleteConfirmationDialog(
    selectedCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    ConfirmationDialog(
        config = ConfirmationDialogConfig.destructive(
            title = if (selectedCount > 1) {
                stringResource(R.string.delete_projects) + "?"
            } else {
                stringResource(R.string.delete_project) + "?"
            },
            message = if (selectedCount == 1) {
                stringResource(R.string.delete_project_confirmation)
            } else {
                stringResource(R.string.delete_projects_confirmation, selectedCount)
            },
            icon = Icons.Default.Delete,
            confirmText = stringResource(R.string.delete),
            dismissText = stringResource(R.string.cancel)
        ),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

@Composable
private fun DeletingOverlay() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LoadingIndicator(
            text = stringResource(R.string.deleting_projects)
        )
    }
}
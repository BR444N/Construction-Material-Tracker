package com.br444n.constructionmaterialtrack.widget.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br444n.constructionmaterialtrack.R
import com.br444n.constructionmaterialtrack.domain.model.Project
import com.br444n.constructionmaterialtrack.presentation.components.buttons.SaveButton
import com.br444n.constructionmaterialtrack.presentation.components.buttons.SaveButtonConfig
import com.br444n.constructionmaterialtrack.presentation.components.cards.ProjectCard
import com.br444n.constructionmaterialtrack.presentation.components.states.EmptyState
import com.br444n.constructionmaterialtrack.presentation.components.states.ErrorContent
import com.br444n.constructionmaterialtrack.presentation.components.states.LoadingIndicator
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectWidgetConfigScreen(
    viewModel: ProjectWidgetConfigViewModel,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.select_project_widget),
                        fontWeight = FontWeight.Medium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BluePrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                ProjectWidgetConfigContent(
                    uiState = uiState,
                    viewModel = viewModel
                )
            }
            
            // Action Buttons
            ActionButtons(
                uiState = uiState,
                onSaveClick = onSaveClick,
                onCancelClick = onCancelClick
            )
        }
    }
}

@Composable
private fun ProjectWidgetConfigContent(
    uiState: ProjectWidgetConfigUiState,
    viewModel: ProjectWidgetConfigViewModel
) {
    when {
        uiState.isLoading -> {
            LoadingIndicator(
                modifier = Modifier.fillMaxSize(),
                text = stringResource(R.string.loading_projects)
            )
        }
        uiState.errorMessage != null -> {
            ErrorContent(
                errorMessage = uiState.errorMessage,
                onRetry = { viewModel.refreshProjects() },
                onDismiss = { viewModel.clearError() },
                modifier = Modifier.fillMaxSize()
            )
        }
        uiState.projects.isEmpty() -> {
            EmptyState(PaddingValues(16.dp))
        }
        else -> {
            ProjectSelectionList(
                projects = uiState.projects,
                selectedProjectId = uiState.selectedProjectId,
                onProjectSelect = { viewModel.selectProject(it.id) }
            )
        }
    }
}

@Composable
private fun ProjectSelectionList(
    projects: List<Project>,
    selectedProjectId: String?,
    onProjectSelect: (Project) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Instructions
        Text(
            text = stringResource(R.string.select_project_widget_description),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        // Project List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(projects) { project ->
                ProjectCard(
                    project = project,
                    onClick = { onProjectSelect(project) },
                    isSelected = project.id == selectedProjectId,
                    isSelectionMode = true
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    uiState: ProjectWidgetConfigUiState,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SaveButton(
            text = stringResource(R.string.add_widget),
            onClick = onSaveClick,
            config = SaveButtonConfig(
                enabled = uiState.selectedProjectId != null && !uiState.isLoading
            )
        )
        
        TextButton(
            onClick = onCancelClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.cancel),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
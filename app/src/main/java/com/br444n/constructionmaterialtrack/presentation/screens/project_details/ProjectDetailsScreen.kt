package com.br444n.constructionmaterialtrack.presentation.screens.project_details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider
import com.br444n.constructionmaterialtrack.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.presentation.components.cards.EditableProjectCard
import com.br444n.constructionmaterialtrack.presentation.components.cards.EditableProjectCardCallbacks
import com.br444n.constructionmaterialtrack.presentation.components.cards.EditableProjectCardState
import com.br444n.constructionmaterialtrack.presentation.components.states.EmptyMaterialsState
import com.br444n.constructionmaterialtrack.presentation.components.states.ErrorContent
import com.br444n.constructionmaterialtrack.presentation.components.states.LoadingIndicator
import com.br444n.constructionmaterialtrack.presentation.components.states.SmallCircularProgressIndicator
import com.br444n.constructionmaterialtrack.presentation.components.lists.MaterialItemRow
import com.br444n.constructionmaterialtrack.presentation.components.lists.EditableMaterialItemRow
import com.br444n.constructionmaterialtrack.presentation.components.cards.ProjectInfoCard
import com.br444n.constructionmaterialtrack.presentation.components.buttons.SecondaryButton
import com.br444n.constructionmaterialtrack.presentation.components.buttons.SecondaryButtonConfig
import com.br444n.constructionmaterialtrack.presentation.components.ui.SectionHeader
import com.br444n.constructionmaterialtrack.presentation.components.progress.GlassLinearProgressBar
import com.br444n.constructionmaterialtrack.presentation.components.dialogs.ConfirmationDialog
import com.br444n.constructionmaterialtrack.presentation.model.ConfirmationDialogConfig
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailsScreen(
    viewModel: ProjectDetailsViewModel,
    projectId: String,
    onBackClick: () -> Unit = {},
    onAddMaterial: (String) -> Unit = {},
    onEditMaterial: (String, String) -> Unit = { _, _ -> }, // projectId, materialId
    onExportToPdf: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Load project when screen opens
    LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
    }
    
    Scaffold(
        topBar = {
            ProjectDetailsTopBar(
                uiState = uiState,
                viewModel = viewModel,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        ProjectDetailsContent(
            uiState = uiState,
            viewModel = viewModel,
            projectId = projectId,
            onAddMaterial = onAddMaterial,
            onEditMaterial = onEditMaterial,
            onExportToPdf = onExportToPdf,
            paddingValues = paddingValues
        )
        
        // Delete confirmation dialog
        if (uiState.showDeleteConfirmation && uiState.materialToDelete != null) {
            ConfirmationDialog(
                config = ConfirmationDialogConfig.destructive(
                    title = stringResource(R.string.delete_material),
                    message = "Are you sure you want to delete \"${uiState.materialToDelete!!.name}\"? This action cannot be undone.",
                    icon = Icons.Default.Delete,
                    confirmText = stringResource(R.string.delete),
                    dismissText = stringResource(R.string.cancel)
                ),
                onConfirm = {
                    viewModel.deleteMaterial()
                },
                onDismiss = {
                    viewModel.hideDeleteConfirmation()
                }
            )
        }
    }
}

// Helper compo-sables to reduce cognitive complexity
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectDetailsTopBar(
    uiState: ProjectDetailsUiState,
    viewModel: ProjectDetailsViewModel,
    onBackClick: () -> Unit
) {
    val density = LocalDensity.current
    
    TopAppBar(
        title = {
            TopBarTitle(uiState = uiState)
        },
        navigationIcon = {
            NavigationIcon(
                uiState = uiState,
                viewModel = viewModel,
                onBackClick = onBackClick,
                density = density
            )
        },
        actions = {
            TopBarActions(
                uiState = uiState,
                viewModel = viewModel,
                density = density
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BluePrimary
        )
    )
}

@Composable
private fun TopBarTitle(uiState: ProjectDetailsUiState) {
    Text(
        text = if (uiState.isEditMode) {
            stringResource(R.string.edit_project)
        } else {
            uiState.project?.name ?: stringResource(R.string.project_details)
        },
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NavigationIcon(
    uiState: ProjectDetailsUiState,
    viewModel: ProjectDetailsViewModel,
    onBackClick: () -> Unit,
    density: androidx.compose.ui.unit.Density
) {
    Box {
        TooltipBox(
            positionProvider = createTooltipPositionProvider(density),
            tooltip = {
                PlainTooltip {
                    Text(
                        text = if (uiState.isEditMode) {
                            stringResource(R.string.cancel_tooltip)
                        } else {
                            stringResource(R.string.go_back_tooltip)
                        }
                    )
                }
            },
            state = remember { TooltipState() }
        ) {
            IconButton(
                onClick = {
                    handleNavigationClick(
                        isEditMode = uiState.isEditMode,
                        viewModel = viewModel,
                        onBackClick = onBackClick
                    )
                }
            ) {
                NavigationIconContent(isEditMode = uiState.isEditMode)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarActions(
    uiState: ProjectDetailsUiState,
    viewModel: ProjectDetailsViewModel,
    density: androidx.compose.ui.unit.Density
) {
    when {
        !uiState.isEditMode && uiState.project != null -> {
            EditModeAction(viewModel = viewModel, density = density)
        }
        uiState.isEditMode -> {
            SaveAction(uiState = uiState, viewModel = viewModel, density = density)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditModeAction(
    viewModel: ProjectDetailsViewModel,
    density: androidx.compose.ui.unit.Density
) {
    Box {
        TooltipBox(
            positionProvider = createTooltipPositionProvider(density),
            tooltip = {
                PlainTooltip {
                    Text(stringResource(R.string.edit_project_tooltip))
                }
            },
            state = remember { TooltipState() }
        ) {
            IconButton(onClick = { viewModel.enterEditMode() }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.edit_project_tooltip),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SaveAction(
    uiState: ProjectDetailsUiState,
    viewModel: ProjectDetailsViewModel,
    density: androidx.compose.ui.unit.Density
) {
    Box {
        TooltipBox(
            positionProvider = createTooltipPositionProvider(density),
            tooltip = {
                PlainTooltip {
                    Text(stringResource(R.string.save_changes_tooltip))
                }
            },
            state = remember { TooltipState() }
        ) {
            IconButton(
                onClick = { viewModel.saveProjectChanges() },
                enabled = !uiState.isSavingProject && uiState.editProjectName.isNotBlank()
            ) {
                if (uiState.isSavingProject) {
                    SmallCircularProgressIndicator()
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.save_changes_tooltip),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectDetailsContent(
    uiState: ProjectDetailsUiState,
    viewModel: ProjectDetailsViewModel,
    projectId: String,
    onAddMaterial: (String) -> Unit,
    onEditMaterial: (String, String) -> Unit,
    onExportToPdf: (String) -> Unit,
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
                projectId = projectId,
                paddingValues = paddingValues
            )
        }
        else -> {
            ProjectDetailsLazyColumn(
                uiState = uiState,
                viewModel = viewModel,
                projectId = projectId,
                onAddMaterial = onAddMaterial,
                onEditMaterial = onEditMaterial,
                onExportToPdf = onExportToPdf,
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
        text = stringResource(R.string.loading_project)
    )
}

@Composable
private fun ErrorState(
    errorMessage: String?,
    viewModel: ProjectDetailsViewModel,
    projectId: String,
    paddingValues: PaddingValues
) {
    ErrorContent(
        errorMessage = errorMessage ?: "",
        onRetry = { viewModel.loadProject(projectId) },
        onDismiss = { viewModel.clearError() },
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    )
}

@Composable
private fun ProjectDetailsLazyColumn(
    uiState: ProjectDetailsUiState,
    viewModel: ProjectDetailsViewModel,
    projectId: String,
    onAddMaterial: (String) -> Unit,
    onEditMaterial: (String, String) -> Unit,
    onExportToPdf: (String) -> Unit,
    paddingValues: PaddingValues
) {
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
                ProjectHeader(
                    uiState = uiState,
                    viewModel = viewModel,
                    project = project
                )
            }
        }
        
        // Materials Section Header
        item {
            SectionHeader(title = stringResource(R.string.materials))
        }
        
        // Materials Content
        if (uiState.materials.isEmpty()) {
            item {
                EmptyMaterialsState(
                    title = stringResource(R.string.no_materials),
                    message = stringResource(R.string.add_materials_message)
                )
            }
        } else {
            items(uiState.materials) { material ->
                if (uiState.isEditMode) {
                    EditableMaterialItemRow(
                        material = material,
                        onCheckedChange = { isChecked ->
                            viewModel.updateMaterialStatus(material, isChecked)
                        },
                        onEditClick = {
                            onEditMaterial(projectId, material.id)
                        },
                        onDeleteClick = {
                            viewModel.showDeleteConfirmation(material)
                        }
                    )
                } else {
                    MaterialItemRow(
                        material = material,
                        onCheckedChange = { isChecked ->
                            viewModel.updateMaterialStatus(material, isChecked)
                        }
                    )
                }
            }
        }
        
        // Action Buttons
        item {
            ActionButtons(
                uiState = uiState,
                onAddMaterial = onAddMaterial,
                onExportToPdf = onExportToPdf
            )
        }
    }
}

@Composable
private fun ProjectHeader(
    uiState: ProjectDetailsUiState,
    viewModel: ProjectDetailsViewModel,
    project: com.br444n.constructionmaterialtrack.domain.model.Project
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress Bar - Solo mostrar si hay materiales
        if (uiState.totalMaterials > 0) {
            GlassLinearProgressBar(
                progress = uiState.progress,
                totalMaterials = uiState.totalMaterials,
                completedMaterials = uiState.completedMaterials,
                modifier = Modifier.fillMaxWidth(),
                height = 28.dp
            )
        }
        
        // Project Card
        if (uiState.isEditMode) {
            EditableProjectCard(
                state = EditableProjectCardState(
                    projectName = uiState.editProjectName,
                    projectDescription = uiState.editProjectDescription,
                    selectedImageUri = uiState.editSelectedImageUri
                ),
                callbacks = EditableProjectCardCallbacks(
                    onNameChange = viewModel::updateEditProjectName,
                    onDescriptionChange = viewModel::updateEditProjectDescription,
                    onImageSelected = viewModel::updateEditSelectedImage
                )
            )
        } else {
            ProjectInfoCard(project = project)
        }
    }
}

@Composable
private fun ActionButtons(
    uiState: ProjectDetailsUiState,
    onAddMaterial: (String) -> Unit,
    onExportToPdf: (String) -> Unit
) {
    Column {
        if (!uiState.isEditMode) {
            AddMaterialButton(
                project = uiState.project,
                onAddMaterial = onAddMaterial
            )
        }
        
        ExportToPdfButton(
            project = uiState.project,
            onExportToPdf = onExportToPdf
        )
    }
}

@Composable
private fun AddMaterialButton(
    project: com.br444n.constructionmaterialtrack.domain.model.Project?,
    onAddMaterial: (String) -> Unit
) {
    SecondaryButton(
        text = stringResource(R.string.add_materials_button),
        onClick = { 
            project?.let { onAddMaterial(it.id) }
        },
        config = SecondaryButtonConfig(
            vectorIcon = Icons.Default.Add
        )
    )
}

@Composable
private fun ExportToPdfButton(
    project: com.br444n.constructionmaterialtrack.domain.model.Project?,
    onExportToPdf: (String) -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(16.dp))
        SecondaryButton(
            text = stringResource(R.string.export_to_pdf),
            onClick = { 
                project?.let { onExportToPdf(it.id) }
            },
            config = SecondaryButtonConfig(
                icon = painterResource(id = R.drawable.export_pdf)
            )
        )
    }
}



@Composable
private fun NavigationIconContent(isEditMode: Boolean) {
    Icon(
        imageVector = if (isEditMode) {
            Icons.Default.Close
        } else {
            Icons.AutoMirrored.Filled.ArrowBack
        },
        contentDescription = if (isEditMode) {
            stringResource(R.string.cancel_tooltip)
        } else {
            stringResource(R.string.go_back_tooltip)
        },
        tint = MaterialTheme.colorScheme.onSurface
    )
}

private fun handleNavigationClick(
    isEditMode: Boolean,
    viewModel: ProjectDetailsViewModel,
    onBackClick: () -> Unit
) {
    if (isEditMode) {
        viewModel.exitEditMode()
    } else {
        onBackClick()
    }
}

private fun createTooltipPositionProvider(density: androidx.compose.ui.unit.Density) = object : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        val spacingPx = with(density) { 4.dp.toPx().toInt() }
        val x = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
        val y = anchorBounds.bottom + spacingPx
        val adjustedX = x.coerceIn(0, windowSize.width - popupContentSize.width)
        val adjustedY = y.coerceAtMost(windowSize.height - popupContentSize.height)
        return IntOffset(adjustedX, adjustedY)
    }
}

package com.br444n.constructionmaterialtrack.presentation.screens.project_details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import com.br444n.constructionmaterialtrack.presentation.components.states.EmptyMaterialsState
import com.br444n.constructionmaterialtrack.presentation.components.states.ErrorContent
import com.br444n.constructionmaterialtrack.presentation.components.states.LoadingIndicator
import com.br444n.constructionmaterialtrack.presentation.components.lists.MaterialItemRow
import com.br444n.constructionmaterialtrack.presentation.components.cards.ProjectInfoCard
import com.br444n.constructionmaterialtrack.presentation.components.buttons.SecondaryButton
import com.br444n.constructionmaterialtrack.presentation.components.ui.SectionHeader
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary

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
    val density = LocalDensity.current
    
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
                },
                navigationIcon = {
                    Box {
                        TooltipBox(
                            positionProvider = object : PopupPositionProvider {
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
                            },
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
                            IconButton(onClick = {
                                if (uiState.isEditMode) {
                                    viewModel.exitEditMode()
                                } else {
                                    onBackClick()
                                }
                            }) {
                                Icon(
                                    imageVector = if (uiState.isEditMode)
                                        Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = if (uiState.isEditMode) {
                                        stringResource(R.string.cancel_tooltip)
                                    } else {
                                        stringResource(R.string.go_back_tooltip)
                                    },
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                },
                actions = {
                    if (!uiState.isEditMode && uiState.project != null) {
                        Box {
                            TooltipBox(
                                positionProvider = object : PopupPositionProvider {
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
                                },
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
                    } else if (uiState.isEditMode) {
                        Box {
                            TooltipBox(
                                positionProvider = object : PopupPositionProvider {
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
                                },
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
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp
                                        )
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
                    text = stringResource(R.string.loading_project)
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
                        SectionHeader(
                            title = stringResource(R.string.materials))
                    }
                    
                    if (uiState.materials.isEmpty()) {
                        item {
                            EmptyMaterialsState(
                                title = stringResource(R.string.no_materials),
                                message = stringResource(R.string.add_materials_message)
                            )
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
                            SecondaryButton(
                                text = stringResource(R.string.add_materials_button),
                                vectorIcon = Icons.Default.Add,
                                onClick = { 
                                    uiState.project?.let { project ->
                                        onAddMaterial(project.id)
                                    }
                                }
                            )
                        }
                    }
                    
                    // Export Button (only show when not in edit mode)
                    if (!uiState.isEditMode) {
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            SecondaryButton(
                                text = stringResource(R.string.export_to_pdf),
                                icon = painterResource(id = R.drawable.export_pdf),
                                onClick = { 
                                    uiState.project?.let { project ->
                                        onExportToPdf(project.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}



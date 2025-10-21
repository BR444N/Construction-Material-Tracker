package com.br444n.constructionmaterialtrack.presentation.screens.project_details

import android.net.Uri
import com.br444n.constructionmaterialtrack.domain.model.Material
import com.br444n.constructionmaterialtrack.domain.model.Project

data class ProjectDetailsUiState(
    val project: Project? = null,
    val materials: List<Material> = emptyList(),
    val isLoading: Boolean = false,
    val isUpdatingMaterial: Boolean = false,
    val errorMessage: String? = null,
    // Edit mode states
    val isEditMode: Boolean = false,
    val editProjectName: String = "",
    val editProjectDescription: String = "",
    val editSelectedImageUri: Uri? = null,
    val isSavingProject: Boolean = false
)
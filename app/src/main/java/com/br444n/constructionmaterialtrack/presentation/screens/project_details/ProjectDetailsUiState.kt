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
    val isSavingProject: Boolean = false,
    // Delete confirmation dialog
    val showDeleteConfirmation: Boolean = false,
    val materialToDelete: Material? = null
) {
    // Calculated progress properties
    val totalMaterials: Int = materials.size
    val completedMaterials: Int = materials.count { it.isPurchased }
    val progress: Float = if (totalMaterials > 0) {
        completedMaterials.toFloat() / totalMaterials.toFloat()
    } else {
        0f
    }
}
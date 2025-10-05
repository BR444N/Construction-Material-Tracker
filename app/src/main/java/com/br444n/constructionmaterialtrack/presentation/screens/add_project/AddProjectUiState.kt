package com.br444n.constructionmaterialtrack.presentation.screens.add_project

data class AddProjectUiState(
    val projectName: String = "",
    val projectDescription: String = "",
    val selectedImageUri: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val projectSaved: String? = null, // Project ID when saved
    val isFormValid: Boolean = false
)
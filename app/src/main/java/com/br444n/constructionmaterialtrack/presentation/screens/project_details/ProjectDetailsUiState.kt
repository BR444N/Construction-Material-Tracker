package com.br444n.constructionmaterialtrack.presentation.screens.project_details

import com.br444n.constructionmaterialtrack.domain.model.Material
import com.br444n.constructionmaterialtrack.domain.model.Project

data class ProjectDetailsUiState(
    val project: Project? = null,
    val materials: List<Material> = emptyList(),
    val isLoading: Boolean = false,
    val isUpdatingMaterial: Boolean = false,
    val isExportingPdf: Boolean = false,
    val errorMessage: String? = null,
    val pdfExported: Boolean = false
)
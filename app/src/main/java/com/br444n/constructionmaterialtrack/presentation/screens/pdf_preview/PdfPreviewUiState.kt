package com.br444n.constructionmaterialtrack.presentation.screens.pdf_preview

import com.br444n.constructionmaterialtrack.domain.model.Material
import com.br444n.constructionmaterialtrack.domain.model.Project
import java.io.File

data class PdfPreviewUiState(
    val project: Project? = null,
    val materials: List<Material> = emptyList(),
    val isLoading: Boolean = false,
    val isGeneratingPdf: Boolean = false,
    val pdfGenerated: Boolean = false,
    val generatedPdfFile: File? = null,
    val errorMessage: String? = null
)
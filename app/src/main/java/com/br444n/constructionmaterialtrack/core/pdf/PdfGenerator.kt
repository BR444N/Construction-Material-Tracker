package com.br444n.constructionmaterialtrack.core.pdf

import com.br444n.constructionmaterialtrack.core.pdf.models.PdfGenerationResult
import com.br444n.constructionmaterialtrack.domain.model.Material
import com.br444n.constructionmaterialtrack.domain.model.Project

interface PdfGenerator {
    suspend fun generateProjectPdf(
        project: Project,
        materials: List<Material>
    ): PdfGenerationResult
}
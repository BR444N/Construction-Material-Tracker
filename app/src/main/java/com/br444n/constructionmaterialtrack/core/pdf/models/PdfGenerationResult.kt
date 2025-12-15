package com.br444n.constructionmaterialtrack.core.pdf.models

import java.io.File

sealed class PdfGenerationResult {
    data class Success(val file: File) : PdfGenerationResult()
    data class Error(val message: String, val exception: Throwable? = null) : PdfGenerationResult()
}
package com.br444n.constructionmaterialtrack.core.pdf

import android.content.ContentResolver
import android.os.Environment
import com.br444n.constructionmaterialtrack.core.pdf.components.PdfImageHandler
import com.br444n.constructionmaterialtrack.core.pdf.components.PdfStyleHelper
import com.br444n.constructionmaterialtrack.core.pdf.components.PdfTableBuilder
import com.br444n.constructionmaterialtrack.core.pdf.models.PdfGenerationResult
import com.br444n.constructionmaterialtrack.domain.model.Material
import com.br444n.constructionmaterialtrack.domain.model.Project
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Interface for providing coroutine dispatchers
 */
interface DispatcherProvider {
    val io: CoroutineDispatcher
}

/**
 * Default implementation of DispatcherProvider
 */
class DefaultDispatcherProvider : DispatcherProvider {
    override val io: CoroutineDispatcher = Dispatchers.IO
}

class PdfGeneratorImpl(
    contentResolver: ContentResolver,
    private val dispatcherProvider: DispatcherProvider = DefaultDispatcherProvider()
) : PdfGenerator {
    
    companion object {
        private const val TAG = "PdfGeneratorImpl"
        private const val PDF_MARGIN = 50f
    }
    
    private val imageHandler = PdfImageHandler(contentResolver)
    private val tableBuilder = PdfTableBuilder()
    
    override suspend fun generateProjectPdf(
        project: Project,
        materials: List<Material>
    ): PdfGenerationResult = withContext(dispatcherProvider.io) {
        try {
            val file = createPdfFile(project, materials)
            PdfGenerationResult.Success(file)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to generate PDF: ${e.message}", e)
            PdfGenerationResult.Error("Failed to generate PDF: ${e.message}", e)
        }
    }
    
    private fun createPdfFile(project: Project, materials: List<Material>): File {
        val file = createOutputFile(project.name)
        val writer = PdfWriter(FileOutputStream(file))
        val pdfDocument = PdfDocument(writer)
        val document = Document(pdfDocument, PageSize.A4)
        
        // Set margins
        document.setMargins(PDF_MARGIN, PDF_MARGIN, PDF_MARGIN, PDF_MARGIN)
        
        try {
            buildPdfContent(document, project, materials)
        } finally {
            document.close()
        }
        
        return file
    }
    
    private fun buildPdfContent(document: Document, project: Project, materials: List<Material>) {
        // Add project image
        imageHandler.createProjectImage(project.imageUri)?.let { image ->
            document.add(image)
        }
        
        // Add project name
        document.add(PdfStyleHelper.createProjectTitle(project.name))
        
        // Add project description
        if (project.description.isNotBlank()) {
            document.add(PdfStyleHelper.createProjectDescription(project.description))
        }
        
        // Add materials section
        addMaterialsSection(document, materials)
    }
    
    private fun addMaterialsSection(document: Document, materials: List<Material>) {
        // Materials header
        document.add(PdfStyleHelper.createSectionHeader("Materials"))
        
        if (materials.isEmpty()) {
            document.add(PdfStyleHelper.createEmptyMessage("No materials added yet"))
            return
        }
        
        // Create and add materials table
        val table = tableBuilder.createMaterialsTable(materials)
        document.add(table)
        
        // Add total cost
        val totalCost = tableBuilder.calculateTotalCost(materials)
        document.add(PdfStyleHelper.createTotalCost(totalCost))
    }
    
    @Suppress("DEPRECATION")
    private fun createOutputFile(projectName: String): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_msys", Locale.US).format(Date())
        val fileName = "${projectName.replace(" ", "_")}_$timestamp.pdf"
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        return File(downloadsDir, fileName)
    }
}
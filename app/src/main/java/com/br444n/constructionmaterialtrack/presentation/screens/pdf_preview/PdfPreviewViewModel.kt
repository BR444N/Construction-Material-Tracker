package com.br444n.constructionmaterialtrack.presentation.screens.pdf_preview

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.br444n.constructionmaterialtrack.data.local.database.ConstructionDatabase
import com.br444n.constructionmaterialtrack.data.repository.MaterialRepositoryImpl
import com.br444n.constructionmaterialtrack.data.repository.ProjectRepositoryImpl
import com.br444n.constructionmaterialtrack.domain.repository.MaterialRepository
import com.br444n.constructionmaterialtrack.domain.repository.ProjectRepository
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.DeviceGray
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.net.toUri

class PdfPreviewViewModel(application: Application) : AndroidViewModel(application) {
    
    private val projectRepository: ProjectRepository
    private val materialRepository: MaterialRepository
    private val context: Context = application
    
    private val _uiState = MutableStateFlow(PdfPreviewUiState())
    val uiState: StateFlow<PdfPreviewUiState> = _uiState.asStateFlow()
    
    init {
        val database = ConstructionDatabase.getDatabase(application)
        projectRepository = ProjectRepositoryImpl(database.projectDao())
        materialRepository = MaterialRepositoryImpl(database.materialDao())
    }
    
    fun loadProjectData(projectId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            // Delay mínimo para mostrar la animación Lottie
            delay(2500L)
            
            try {
                // Load project
                val project = projectRepository.getProjectById(projectId)
                _uiState.value = _uiState.value.copy(project = project)
                
                // Load materials
                materialRepository.getMaterialsByProjectId(projectId)
                    .catch { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Failed to load materials"
                        )
                    }
                    .collect { materialList ->
                        _uiState.value = _uiState.value.copy(
                            materials = materialList,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load project"
                )
            }
        }
    }
    
    fun generatePdf() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingPdf = true, errorMessage = null)
            
            // Delay mínimo para mostrar la animación Lottie
            delay(2500L)
            
            try {
                withContext(Dispatchers.IO) {
                    val project = _uiState.value.project
                    val materials = _uiState.value.materials
                    
                    if (project != null) {
                        val generatedFile = createPdfDocument(project, materials)
                        
                        _uiState.value = _uiState.value.copy(
                            isGeneratingPdf = false,
                            pdfGenerated = true,
                            generatedPdfFile = generatedFile
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isGeneratingPdf = false,
                            errorMessage = "No project data to export"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isGeneratingPdf = false,
                    errorMessage = e.message ?: "Failed to generate PDF"
                )
            }
        }
    }
    
    private fun createPdfDocument(project: com.br444n.constructionmaterialtrack.domain.model.Project, materials: List<com.br444n.constructionmaterialtrack.domain.model.Material>): File {
        // Create file path
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "${project.name.replace(" ", "_")}_$timestamp.pdf"
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        
        // Create PDF document using iText
        val writer = PdfWriter(FileOutputStream(file))
        val pdfDocument = PdfDocument(writer)
        val document = Document(pdfDocument, PageSize.A4)
        
        // Set margins
        document.setMargins(50f, 50f, 50f, 50f)
        
        try {
            // Add project image (circular)
            addProjectImage(document, project)
            
            // Add project name (bold, centered)
            val projectName = Paragraph(project.name)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(24f)
                .setMarginTop(20f)
                .setMarginBottom(10f)
            document.add(projectName)
            
            // Add project description (normal, centered)
            if (project.description.isNotBlank()) {
                val projectDescription = Paragraph(project.description)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(16f)
                    .setMarginBottom(30f)
                document.add(projectDescription)
            }
            
            // Add materials section
            addMaterialsSection(document, materials)
            
        } finally {
            document.close()
        }
        
        return file
    }
    
    private fun addProjectImage(document: Document, project: com.br444n.constructionmaterialtrack.domain.model.Project) {
        try {
            project.imageUri?.let { imageUri ->
                val inputStream = context.contentResolver.openInputStream(imageUri.toUri())
                inputStream?.use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                    bitmap?.let {
                        // Convert bitmap to byte array
                        val outputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                        val imageBytes = outputStream.toByteArray()
                        
                        // Create iText image
                        val imageData = ImageDataFactory.create(imageBytes)
                        val image = Image(imageData)
                        
                        // Set image properties (circular effect through size and positioning)
                        image.setWidth(120f)
                        image.setHeight(120f)
                        image.setHorizontalAlignment(HorizontalAlignment.CENTER)
                        image.setMarginBottom(20f)
                        
                        document.add(image)
                    }
                }
            }
        } catch (e: Exception) {
            // If image loading fails, continue without image
            e.printStackTrace()
        }
    }
    
    private fun addMaterialsSection(document: Document, materials: List<com.br444n.constructionmaterialtrack.domain.model.Material>) {
        // Materials header
        val materialsHeader = Paragraph("Materials")
            .setFontSize(18f)
            .setMarginBottom(15f)
        document.add(materialsHeader)
        
        if (materials.isEmpty()) {
            val noMaterials = Paragraph("No materials added yet")
                .setFontSize(12f)
                .setFontColor(DeviceGray(0.5f))
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20f)
            document.add(noMaterials)
            return
        }
        
        // Create materials table
        val table = Table(UnitValue.createPercentArray(floatArrayOf(8f, 35f, 12f, 8f, 15f, 22f)))
            .setWidth(UnitValue.createPercentValue(100f))
        
        // Table headers
        table.addHeaderCell(createHeaderCell("Status"))
        table.addHeaderCell(createHeaderCell("Material"))
        table.addHeaderCell(createHeaderCell("Quantity"))
        table.addHeaderCell(createHeaderCell("Unit"))
        table.addHeaderCell(createHeaderCell("Price"))
        table.addHeaderCell(createHeaderCell("Description"))
        
        // Add material rows
        materials.forEach { material ->
            // Status (checkbox)
            val statusCell = Cell().add(
                Paragraph(if (material.isPurchased) "☑" else "☐")
                    .setFontSize(12f)
                    .setTextAlignment(TextAlignment.CENTER)
            )
            table.addCell(statusCell)
            
            // Material name
            val nameCell = Cell().add(
                Paragraph(material.name)
                    .setFontSize(12f)
            )
            table.addCell(nameCell)
            
            // Quantity
            val quantityCell = Cell().add(
                Paragraph(material.quantity.toString())
                    .setFontSize(12f)
                    .setTextAlignment(TextAlignment.CENTER)
            )
            table.addCell(quantityCell)
            
            // Unit
            val unitCell = Cell().add(
                Paragraph(material.unit)
                    .setFontSize(12f)
                    .setTextAlignment(TextAlignment.CENTER)
            )
            table.addCell(unitCell)
            
            // Price
            val priceText = try {
                val priceValue = material.price.toDoubleOrNull() ?: 0.0
                "$${String.format("%.2f", priceValue)}"
            } catch (e: Exception) {
                "$${material.price}"
            }
            val priceCell = Cell().add(
                Paragraph(priceText)
                    .setFontSize(12f)
                    .setTextAlignment(TextAlignment.RIGHT)
            )
            table.addCell(priceCell)
            
            // Description
            val descriptionCell = Cell().add(
                Paragraph(material.description.ifBlank { "-" })
                    .setFontSize(10f)
            )
            table.addCell(descriptionCell)
        }
        
        document.add(table)
        
        // Add total cost
        var totalCost = 0.0
        materials.forEach { material ->
            try {
                val price = material.price.toDoubleOrNull() ?: 0.0
                val quantity = material.quantity.toIntOrNull() ?: 0
                totalCost += price * quantity
            } catch (e: Exception) {
                // Skip materials with invalid price/quantity
            }
        }
        val totalParagraph = Paragraph("Total Estimated Cost: $${String.format("%.2f", totalCost)}")
            .setFontSize(14f)

            .setTextAlignment(TextAlignment.RIGHT)
            .setMarginTop(20f)
        document.add(totalParagraph)
    }
    
    private fun createHeaderCell(text: String): Cell {
        return Cell().add(
            Paragraph(text)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
        ).setBackgroundColor(DeviceGray(0.8f))
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun clearPdfGenerated() {
        _uiState.value = _uiState.value.copy(pdfGenerated = false, generatedPdfFile = null)
    }
}
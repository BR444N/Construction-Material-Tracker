package com.br444n.constructionmaterialtrack.presentation.screens.pdf_preview

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.br444n.constructionmaterialtrack.core.pdf.PdfGenerator
import com.br444n.constructionmaterialtrack.core.pdf.PdfGeneratorImpl
import com.br444n.constructionmaterialtrack.core.pdf.models.PdfGenerationResult
import com.br444n.constructionmaterialtrack.data.local.database.ConstructionDatabase
import com.br444n.constructionmaterialtrack.data.repository.MaterialRepositoryImpl
import com.br444n.constructionmaterialtrack.data.repository.ProjectRepositoryImpl
import com.br444n.constructionmaterialtrack.domain.repository.MaterialRepository
import com.br444n.constructionmaterialtrack.domain.repository.ProjectRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PdfPreviewViewModel(
    application: Application
) : AndroidViewModel(application) {
    
    private val projectRepository: ProjectRepository
    private val materialRepository: MaterialRepository
    private val pdfGenerator: PdfGenerator
    
    private val _uiState = MutableStateFlow(PdfPreviewUiState())
    val uiState: StateFlow<PdfPreviewUiState> = _uiState.asStateFlow()
    
    init {
        val database = ConstructionDatabase.getDatabase(application)
        projectRepository = ProjectRepositoryImpl(database.projectDao())
        materialRepository = MaterialRepositoryImpl(database.materialDao())
        pdfGenerator = PdfGeneratorImpl(application.contentResolver)
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
                try {
                    materialRepository.getMaterialsByProjectId(projectId)
                        .collect { materialList ->
                            _uiState.value = _uiState.value.copy(
                                materials = materialList,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                } catch (materialException: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = materialException.message ?: "Failed to load materials"
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
            
            val project = _uiState.value.project
            val materials = _uiState.value.materials
            
            if (project == null) {
                _uiState.value = _uiState.value.copy(
                    isGeneratingPdf = false,
                    errorMessage = "No project data to export"
                )
                return@launch
            }
            
            when (val result = pdfGenerator.generateProjectPdf(project, materials)) {
                is PdfGenerationResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isGeneratingPdf = false,
                        pdfGenerated = true,
                        generatedPdfFile = result.file
                    )
                }
                is PdfGenerationResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isGeneratingPdf = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun clearPdfGenerated() {
        _uiState.value = _uiState.value.copy(pdfGenerated = false, generatedPdfFile = null)
    }
}
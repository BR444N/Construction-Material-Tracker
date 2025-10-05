package com.br444n.constructionmaterialtrack.presentation.screens.project_details

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.br444n.constructionmaterialtrack.data.local.database.ConstructionDatabase
import com.br444n.constructionmaterialtrack.data.repository.MaterialRepositoryImpl
import com.br444n.constructionmaterialtrack.data.repository.ProjectRepositoryImpl
import com.br444n.constructionmaterialtrack.domain.model.Material
import com.br444n.constructionmaterialtrack.domain.repository.MaterialRepository
import com.br444n.constructionmaterialtrack.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProjectDetailsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val projectRepository: ProjectRepository
    private val materialRepository: MaterialRepository
    
    private val _uiState = MutableStateFlow(ProjectDetailsUiState())
    val uiState: StateFlow<ProjectDetailsUiState> = _uiState.asStateFlow()
    
    init {
        val database = ConstructionDatabase.getDatabase(application)
        projectRepository = ProjectRepositoryImpl(database.projectDao())
        materialRepository = MaterialRepositoryImpl(database.materialDao())
    }
    
    fun loadProject(projectId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
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
    
    fun updateMaterialStatus(material: Material, isPurchased: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdatingMaterial = true)
            
            try {
                val updatedMaterial = material.copy(isPurchased = isPurchased)
                materialRepository.updateMaterial(updatedMaterial)
                
                // Update local state
                val updatedMaterials = _uiState.value.materials.map {
                    if (it.id == material.id) updatedMaterial else it
                }
                _uiState.value = _uiState.value.copy(
                    materials = updatedMaterials,
                    isUpdatingMaterial = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUpdatingMaterial = false,
                    errorMessage = e.message ?: "Failed to update material"
                )
            }
        }
    }
    
    fun exportToPdf() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExportingPdf = true, errorMessage = null)
            
            try {
                // TODO: Implement PDF export
                val project = _uiState.value.project
                val materials = _uiState.value.materials
                
                if (project != null) {
                    // Simulate PDF export for now
                    kotlinx.coroutines.delay(2000) // Simulate processing time
                    
                    _uiState.value = _uiState.value.copy(
                        isExportingPdf = false,
                        pdfExported = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isExportingPdf = false,
                        errorMessage = "No project data to export"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isExportingPdf = false,
                    errorMessage = e.message ?: "Failed to export PDF"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun clearPdfExported() {
        _uiState.value = _uiState.value.copy(pdfExported = false)
    }
}
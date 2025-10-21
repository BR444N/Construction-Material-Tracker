package com.br444n.constructionmaterialtrack.presentation.screens.project_details

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.br444n.constructionmaterialtrack.data.local.database.ConstructionDatabase
import com.br444n.constructionmaterialtrack.data.repository.MaterialRepositoryImpl
import com.br444n.constructionmaterialtrack.data.repository.ProjectRepositoryImpl
import com.br444n.constructionmaterialtrack.domain.model.Material
import com.br444n.constructionmaterialtrack.domain.repository.MaterialRepository
import com.br444n.constructionmaterialtrack.domain.repository.ProjectRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.launch
import androidx.core.net.toUri

class ProjectDetailsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val projectRepository: ProjectRepository
    private val materialRepository: MaterialRepository
    
    private val _uiState = MutableStateFlow(ProjectDetailsUiState())
    val uiState: StateFlow<ProjectDetailsUiState> = _uiState.asStateFlow()
    
    companion object {
        private const val ERROR_FAILED_TO_LOAD_PROJECT = "Failed to load project"
        private const val ERROR_FAILED_TO_UPDATE_MATERIAL = "Failed to update material"
        private const val ERROR_NO_PROJECT_TO_UPDATE = "No project to update"
        private const val ERROR_FAILED_TO_SAVE_CHANGES = "Failed to save project changes"
    }
    
    init {
        val database = ConstructionDatabase.getDatabase(application)
        projectRepository = ProjectRepositoryImpl(database.projectDao())
        materialRepository = MaterialRepositoryImpl(database.materialDao())
    }
    
    fun loadProject(projectId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            // Delay mínimo para mostrar la animación Lottie
            delay(2500L)
            
            try {
                // Load project
                val project = projectRepository.getProjectById(projectId)
                _uiState.value = _uiState.value.copy(
                    project = project,
                    editProjectName = project?.name ?: "",
                    editProjectDescription = project?.description ?: "",
                    editSelectedImageUri = project?.imageUri?.toUri()
                )
                
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
                        errorMessage = materialException.message ?: ERROR_FAILED_TO_LOAD_PROJECT
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: ERROR_FAILED_TO_LOAD_PROJECT
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
                    errorMessage = e.message ?: ERROR_FAILED_TO_UPDATE_MATERIAL
                )
            }
        }
    }
    

    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    

    
    // Edit mode functions
    fun enterEditMode() {
        val project = _uiState.value.project
        _uiState.value = _uiState.value.copy(
            isEditMode = true,
            editProjectName = project?.name ?: "",
            editProjectDescription = project?.description ?: "",
            editSelectedImageUri = project?.imageUri?.toUri()
        )
    }
    
    fun exitEditMode() {
        _uiState.value = _uiState.value.copy(isEditMode = false)
    }
    
    fun updateEditProjectName(name: String) {
        _uiState.value = _uiState.value.copy(editProjectName = name)
    }
    
    fun updateEditProjectDescription(description: String) {
        _uiState.value = _uiState.value.copy(editProjectDescription = description)
    }
    
    fun updateEditSelectedImage(uri: Uri?) {
        _uiState.value = _uiState.value.copy(editSelectedImageUri = uri)
    }
    
    fun saveProjectChanges() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingProject = true, errorMessage = null)
            
            // Delay mínimo para mostrar la animación Lottie
            delay(2500L)
            
            try {
                val currentProject = _uiState.value.project
                if (currentProject != null) {
                    val updatedProject = currentProject.copy(
                        name = _uiState.value.editProjectName.trim(),
                        description = _uiState.value.editProjectDescription.trim(),
                        imageUri = _uiState.value.editSelectedImageUri?.toString()
                    )
                    
                    projectRepository.updateProject(updatedProject)
                    
                    _uiState.value = _uiState.value.copy(
                        project = updatedProject,
                        isEditMode = false,
                        isSavingProject = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSavingProject = false,
                        errorMessage = ERROR_NO_PROJECT_TO_UPDATE
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSavingProject = false,
                    errorMessage = e.message ?: ERROR_FAILED_TO_SAVE_CHANGES
                )
            }
        }
    }
}
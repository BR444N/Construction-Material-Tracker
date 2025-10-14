package com.br444n.constructionmaterialtrack.presentation.screens.add_project

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.br444n.constructionmaterialtrack.data.local.database.ConstructionDatabase
import com.br444n.constructionmaterialtrack.data.repository.ProjectRepositoryImpl
import com.br444n.constructionmaterialtrack.domain.model.Project
import com.br444n.constructionmaterialtrack.domain.repository.ProjectRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddProjectViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ProjectRepository
    
    private val _uiState = MutableStateFlow(AddProjectUiState())
    val uiState: StateFlow<AddProjectUiState> = _uiState.asStateFlow()
    
    init {
        val database = ConstructionDatabase.getDatabase(application)
        repository = ProjectRepositoryImpl(database.projectDao())
    }
    
    fun updateProjectName(name: String) {
        _uiState.value = _uiState.value.copy(
            projectName = name,
            isFormValid = name.isNotBlank()
        )
    }
    
    fun updateProjectDescription(description: String) {
        _uiState.value = _uiState.value.copy(projectDescription = description)
    }
    
    fun setSelectedImageUri(uri: String?) {
        _uiState.value = _uiState.value.copy(selectedImageUri = uri)
    }
    
    fun saveProject() {
        val currentState = _uiState.value
        if (!currentState.isFormValid) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
            
            // Delay mínimo para mostrar la animación Lottie
            delay(2500L)
            
            try {
                val project = Project(
                    id = "", // Repository will generate ID
                    name = currentState.projectName,
                    description = currentState.projectDescription,
                    imageUri = currentState.selectedImageUri
                )
                val projectId = repository.insertProject(project)
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    projectSaved = projectId,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "Failed to save project"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun resetSavedState() {
        _uiState.value = _uiState.value.copy(projectSaved = null)
    }
    
    fun resetForm() {
        _uiState.value = AddProjectUiState()
    }
}
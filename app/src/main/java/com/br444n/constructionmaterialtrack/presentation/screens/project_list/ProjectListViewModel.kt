package com.br444n.constructionmaterialtrack.presentation.screens.project_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.br444n.constructionmaterialtrack.data.local.database.ConstructionDatabase
import com.br444n.constructionmaterialtrack.data.repository.ProjectRepositoryImpl
import com.br444n.constructionmaterialtrack.domain.repository.ProjectRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProjectListViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ProjectRepository
    
    private val _uiState = MutableStateFlow(ProjectListUiState())
    val uiState: StateFlow<ProjectListUiState> = _uiState.asStateFlow()
    
    init {
        val database = ConstructionDatabase.getDatabase(application)
        repository = ProjectRepositoryImpl(database.projectDao())
        loadProjects()
    }
    
    private fun loadProjects() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            // Delay mínimo para mostrar la animación Lottie
            delay(2500L)
            
            repository.getAllProjects()
                .catch { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Unknown error occurred"
                    )
                }
                .collect { projectList ->
                    _uiState.value = _uiState.value.copy(
                        projects = projectList,
                        isLoading = false,
                        errorMessage = null
                    )
                }
        }
    }
    
    fun refreshProjects() {
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        viewModelScope.launch {
            try {
                repository.getAllProjects().collect { projectList ->
                    _uiState.value = _uiState.value.copy(
                        projects = projectList,
                        isRefreshing = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    errorMessage = e.message ?: "Refresh failed"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    // Selection mode functions
    fun enterSelectionMode(projectId: String) {
        _uiState.value = _uiState.value.copy(
            isSelectionMode = true,
            selectedProjects = setOf(projectId)
        )
    }
    
    fun exitSelectionMode() {
        _uiState.value = _uiState.value.copy(
            isSelectionMode = false,
            selectedProjects = emptySet()
        )
    }
    
    fun toggleProjectSelection(projectId: String) {
        val currentSelection = _uiState.value.selectedProjects
        val newSelection = if (currentSelection.contains(projectId)) {
            currentSelection - projectId
        } else {
            currentSelection + projectId
        }
        
        _uiState.value = _uiState.value.copy(
            selectedProjects = newSelection,
            isSelectionMode = newSelection.isNotEmpty()
        )
    }
    
    fun selectAllProjects() {
        val allProjectIds = _uiState.value.projects.map { it.id }.toSet()
        _uiState.value = _uiState.value.copy(selectedProjects = allProjectIds)
    }
    
    fun showDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = true)
    }
    
    fun hideDeleteDialog() {
        _uiState.value = _uiState.value.copy(showDeleteDialog = false)
    }
    
    fun deleteSelectedProjects() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeleting = true, showDeleteDialog = false)
            
            // Delay mínimo para mostrar la animación Lottie
            delay(2500L)
            
            try {
                val selectedIds = _uiState.value.selectedProjects
                val projectsToDelete = _uiState.value.projects.filter { project ->
                    selectedIds.contains(project.id)
                }
                
                projectsToDelete.forEach { project ->
                    repository.deleteProject(project)
                }
                
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    isSelectionMode = false,
                    selectedProjects = emptySet()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    errorMessage = e.message ?: "Failed to delete projects"
                )
            }
        }
    }
}
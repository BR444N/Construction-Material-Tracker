package com.br444n.constructionmaterialtrack.presentation.screens.project_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.br444n.constructionmaterialtrack.core.shortcuts.DynamicShortcutManager
import com.br444n.constructionmaterialtrack.data.local.database.ConstructionDatabase
import com.br444n.constructionmaterialtrack.data.repository.ProjectRepositoryImpl
import com.br444n.constructionmaterialtrack.domain.model.Project
import com.br444n.constructionmaterialtrack.domain.repository.ProjectRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            
            try {
                repository.getAllProjects().collect { projectList ->
                    _uiState.value = _uiState.value.copy(
                        projects = projectList,
                        isLoading = false,
                        errorMessage = null
                    )
                    
                    // Update dynamic shortcuts with recent projects (optimized)
                    updateShortcuts(projectList)
                }
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Unknown error occurred"
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
                    
                    // Update dynamic shortcuts with recent projects (optimized)
                    updateShortcuts(projectList)
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
                    // Send specific broadcast for this deleted project
                    com.br444n.constructionmaterialtrack.widget.ProjectWidgetUpdateReceiver.sendProjectDeletedBroadcast(getApplication(), project.id)
                    
                    // Delete the project
                    repository.deleteProject(project)
                    
                    android.util.Log.d("ProjectListViewModel", "Deleted project ${project.id} and sent widget cleanup broadcast")
                }
                
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    isSelectionMode = false,
                    selectedProjects = emptySet()
                )
                
                // Update shortcuts after deletion (optimized)
                updateShortcuts(_uiState.value.projects)
                
                // Additional force refresh to ensure all widgets are updated
                delay(1000) // Wait for project deletion broadcasts to process
                com.br444n.constructionmaterialtrack.widget.ProjectWidgetUpdateReceiver.sendForceRefreshBroadcast(getApplication())
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    errorMessage = e.message ?: "Failed to delete projects"
                )
            }
        }
    }
    
    /**
     * Updates dynamic shortcuts with the most recent projects
     * Now optimized with background processing and caching
     */
    private fun updateShortcuts(projects: List<Project>) {
        try {
            // Take first 2 projects (assuming they're already sorted by repository)
            val recentProjects = projects.take(2)
            
            // DynamicShortcutManager handles all background processing internally
            DynamicShortcutManager.updateProjectShortcuts(
                getApplication(),
                recentProjects
            )
        } catch (e: Exception) {
            // Silently fail - shortcuts are not critical
            e.printStackTrace()
        }
    }
}
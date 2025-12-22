package com.br444n.constructionmaterialtrack.widget.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.br444n.constructionmaterialtrack.data.local.database.ConstructionDatabase
import com.br444n.constructionmaterialtrack.data.preferences.WidgetPreferences
import com.br444n.constructionmaterialtrack.data.repository.ProjectRepositoryImpl
import com.br444n.constructionmaterialtrack.domain.model.Project
import com.br444n.constructionmaterialtrack.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProjectWidgetConfigUiState(
    val projects: List<Project> = emptyList(),
    val selectedProjectId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ProjectWidgetConfigViewModel(
    application: Application
) : AndroidViewModel(application) {
    
    private val projectRepository: ProjectRepository
    private val widgetPreferences: WidgetPreferences
    
    private val _uiState = MutableStateFlow(ProjectWidgetConfigUiState())
    val uiState: StateFlow<ProjectWidgetConfigUiState> = _uiState.asStateFlow()
    
    init {
        val database = ConstructionDatabase.getDatabase(application)
        projectRepository = ProjectRepositoryImpl(database.projectDao())
        widgetPreferences = WidgetPreferences(application)
        
        loadProjects()
    }
    
    private fun loadProjects() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                projectRepository.getAllProjects().collect { projectList ->
                    _uiState.value = _uiState.value.copy(
                        projects = projectList,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load projects"
                )
            }
        }
    }
    
    fun selectProject(projectId: String) {
        _uiState.value = _uiState.value.copy(selectedProjectId = projectId)
    }
    
    fun saveWidgetConfiguration(widgetId: Int): Boolean {
        val projectId = _uiState.value.selectedProjectId
        
        android.util.Log.d("ProjectWidgetConfig", "=== Saving widget configuration ===")
        android.util.Log.d("ProjectWidgetConfig", "WidgetId: $widgetId")
        android.util.Log.d("ProjectWidgetConfig", "ProjectId: $projectId")
        android.util.Log.d("ProjectWidgetConfig", "Available projects: ${_uiState.value.projects.map { "${it.id}:${it.name}" }}")
        
        return if (projectId != null) {
            try {
                // Save to preferences
                widgetPreferences.saveProjectIdForWidget(widgetId, projectId)
                android.util.Log.d("ProjectWidgetConfig", "Preferences saved successfully")
                
                // Verify it was saved
                val savedProjectId = widgetPreferences.getProjectIdForWidget(widgetId)
                android.util.Log.d("ProjectWidgetConfig", "Verification - Saved ProjectId: '$savedProjectId'")
                
                val hasConfig = widgetPreferences.hasWidgetConfiguration(widgetId)
                android.util.Log.d("ProjectWidgetConfig", "Widget has configuration: $hasConfig")
                
                if (savedProjectId == projectId) {
                    android.util.Log.d("ProjectWidgetConfig", "Configuration saved and verified successfully")
                    
                    // Also try to pre-cache the widget data
                    try {
                        val selectedProject = _uiState.value.projects.find { it.id == projectId }
                        if (selectedProject != null) {
                            android.util.Log.d("ProjectWidgetConfig", "Pre-caching widget data for: ${selectedProject.name}")
                            // Cache with default values - will be updated when widget loads
                            widgetPreferences.cacheWidgetData(
                                widgetId,
                                selectedProject.name,
                                0f, // Will be calculated when widget loads
                                0,  // Will be calculated when widget loads
                                0   // Will be calculated when widget loads
                            )
                            android.util.Log.d("ProjectWidgetConfig", "Pre-cache completed")
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("ProjectWidgetConfig", "Error pre-caching data", e)
                        // Don't fail the save operation for this
                    }
                    
                    true
                } else {
                    android.util.Log.e("ProjectWidgetConfig", "Configuration verification failed - expected: '$projectId', got: '$savedProjectId'")
                    false
                }
            } catch (e: Exception) {
                android.util.Log.e("ProjectWidgetConfig", "Error saving widget configuration", e)
                false
            }
        } else {
            android.util.Log.w("ProjectWidgetConfig", "No project selected, cannot save configuration")
            false
        }
    }
    
    fun refreshProjects() {
        loadProjects()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
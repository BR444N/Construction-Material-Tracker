package com.br444n.constructionmaterialtrack.presentation.screens.project_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.br444n.constructionmaterialtrack.data.local.database.ConstructionDatabase
import com.br444n.constructionmaterialtrack.data.repository.ProjectRepositoryImpl
import com.br444n.constructionmaterialtrack.domain.repository.ProjectRepository
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
}
package com.br444n.constructionmaterialtrack.presentation.screens.project_list

import com.br444n.constructionmaterialtrack.domain.model.Project

data class ProjectListUiState(
    val projects: List<Project> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
)
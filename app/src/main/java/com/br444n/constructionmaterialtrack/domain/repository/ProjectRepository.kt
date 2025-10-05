package com.br444n.constructionmaterialtrack.domain.repository

import com.br444n.constructionmaterialtrack.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getAllProjects(): Flow<List<Project>>
    suspend fun getProjectById(projectId: String): Project?
    suspend fun insertProject(project: Project): String
    suspend fun updateProject(project: Project)
    suspend fun deleteProject(project: Project)
}
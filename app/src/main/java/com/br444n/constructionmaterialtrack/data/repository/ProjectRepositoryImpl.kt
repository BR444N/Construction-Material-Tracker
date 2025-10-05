package com.br444n.constructionmaterialtrack.data.repository

import com.br444n.constructionmaterialtrack.data.local.dao.ProjectDao
import com.br444n.constructionmaterialtrack.data.mapper.toDomain
import com.br444n.constructionmaterialtrack.data.mapper.toEntity
import com.br444n.constructionmaterialtrack.domain.model.Project
import com.br444n.constructionmaterialtrack.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectRepositoryImpl(
    private val projectDao: ProjectDao
) : ProjectRepository {
    
    override fun getAllProjects(): Flow<List<Project>> {
        return projectDao.getAllProjects().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getProjectById(projectId: String): Project? {
        return projectDao.getProjectById(projectId)?.toDomain()
    }
    
    override suspend fun insertProject(project: Project): String {
        val projectId = if (project.id.isEmpty()) {
            System.currentTimeMillis().toString()
        } else {
            project.id
        }
        
        val projectWithId = project.copy(id = projectId)
        projectDao.insertProject(projectWithId.toEntity())
        return projectId
    }
    
    override suspend fun updateProject(project: Project) {
        projectDao.updateProject(project.toEntity())
    }
    
    override suspend fun deleteProject(project: Project) {
        projectDao.deleteProject(project.toEntity())
    }
}
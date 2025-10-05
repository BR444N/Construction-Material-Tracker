package com.br444n.constructionmaterialtrack.data.repository

import com.br444n.constructionmaterialtrack.data.local.dao.MaterialDao
import com.br444n.constructionmaterialtrack.data.mapper.toDomain
import com.br444n.constructionmaterialtrack.data.mapper.toEntity
import com.br444n.constructionmaterialtrack.domain.model.Material
import com.br444n.constructionmaterialtrack.domain.repository.MaterialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MaterialRepositoryImpl(
    private val materialDao: MaterialDao
) : MaterialRepository {
    
    override fun getMaterialsByProjectId(projectId: String): Flow<List<Material>> {
        return materialDao.getMaterialsByProjectId(projectId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getMaterialById(materialId: String): Material? {
        return materialDao.getMaterialById(materialId)?.toDomain()
    }
    
    override suspend fun insertMaterial(material: Material, projectId: String) {
        materialDao.insertMaterial(material.toEntity(projectId))
    }
    
    override suspend fun updateMaterial(material: Material) {
        // We need the projectId for the entity, so we'll get it from the existing material
        val existingMaterial = materialDao.getMaterialById(material.id)
        if (existingMaterial != null) {
            materialDao.updateMaterial(material.toEntity(existingMaterial.projectId))
        }
    }
    
    override suspend fun deleteMaterial(material: Material) {
        val existingMaterial = materialDao.getMaterialById(material.id)
        if (existingMaterial != null) {
            materialDao.deleteMaterial(existingMaterial)
        }
    }
}
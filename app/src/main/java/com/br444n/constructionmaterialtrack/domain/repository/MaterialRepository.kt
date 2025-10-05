package com.br444n.constructionmaterialtrack.domain.repository

import com.br444n.constructionmaterialtrack.domain.model.Material
import kotlinx.coroutines.flow.Flow

interface MaterialRepository {
    fun getMaterialsByProjectId(projectId: String): Flow<List<Material>>
    suspend fun getMaterialById(materialId: String): Material?
    suspend fun insertMaterial(material: Material, projectId: String)
    suspend fun updateMaterial(material: Material)
    suspend fun deleteMaterial(material: Material)
}
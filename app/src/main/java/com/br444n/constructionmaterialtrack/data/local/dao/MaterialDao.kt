package com.br444n.constructionmaterialtrack.data.local.dao

import androidx.room.*
import com.br444n.constructionmaterialtrack.data.local.entity.MaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {
    
    @Query("SELECT * FROM materials WHERE projectId = :projectId ORDER BY createdAt ASC")
    fun getMaterialsByProjectId(projectId: String): Flow<List<MaterialEntity>>
    
    @Query("SELECT * FROM materials WHERE id = :materialId")
    suspend fun getMaterialById(materialId: String): MaterialEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialEntity)
    
    @Update
    suspend fun updateMaterial(material: MaterialEntity)
    
    @Delete
    suspend fun deleteMaterial(material: MaterialEntity)
    
    @Query("DELETE FROM materials WHERE projectId = :projectId")
    suspend fun deleteMaterialsByProjectId(projectId: String)
}
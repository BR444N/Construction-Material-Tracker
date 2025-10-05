package com.br444n.constructionmaterialtrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val imageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
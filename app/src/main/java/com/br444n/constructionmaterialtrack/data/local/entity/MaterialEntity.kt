package com.br444n.constructionmaterialtrack.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "materials",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MaterialEntity(
    @PrimaryKey
    val id: String,
    val projectId: String,
    val name: String,
    val quantity: String,
    val price: String,
    val description: String = "",
    val isPurchased: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
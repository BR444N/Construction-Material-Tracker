package com.br444n.constructionmaterialtrack.data.mapper

import com.br444n.constructionmaterialtrack.data.local.entity.ProjectEntity
import com.br444n.constructionmaterialtrack.domain.model.Project

fun ProjectEntity.toDomain(): Project {
    return Project(
        id = id,
        name = name,
        description = description,
        imageRes = null,
        imageUri = imageUri
    )
}

fun Project.toEntity(): ProjectEntity {
    return ProjectEntity(
        id = id,
        name = name,
        description = description,
        imageUri = imageUri
    )
}
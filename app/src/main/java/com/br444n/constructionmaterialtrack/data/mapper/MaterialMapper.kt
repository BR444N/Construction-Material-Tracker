package com.br444n.constructionmaterialtrack.data.mapper

import com.br444n.constructionmaterialtrack.data.local.entity.MaterialEntity
import com.br444n.constructionmaterialtrack.domain.model.Material

fun MaterialEntity.toDomain(): Material {
    return Material(
        id = id,
        name = name,
        quantity = quantity,
        price = price,
        description = description,
        isPurchased = isPurchased
    )
}

fun Material.toEntity(projectId: String): MaterialEntity {
    return MaterialEntity(
        id = id.ifEmpty { System.currentTimeMillis().toString() },
        projectId = projectId,
        name = name,
        quantity = quantity,
        price = price,
        description = description,
        isPurchased = isPurchased
    )
}
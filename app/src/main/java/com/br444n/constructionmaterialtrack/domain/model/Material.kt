package com.br444n.constructionmaterialtrack.domain.model

data class Material(
    val id: String = "",
    val name: String,
    val quantity: String,
    val unit: String = "pcs", // Default to pieces
    val price: String,
    val description: String = "",
    val isPurchased: Boolean = false
)

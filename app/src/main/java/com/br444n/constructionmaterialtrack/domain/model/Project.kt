package com.br444n.constructionmaterialtrack.domain.model

data class Project(
    val id: String,
    val name: String,
    val description: String,
    val imageRes: Int? = null,
    val imageUri: String? = null
)
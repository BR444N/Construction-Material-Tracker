package com.br444n.constructionmaterialtrack.presentation.screens.add_material

import com.br444n.constructionmaterialtrack.domain.model.MaterialUnit

data class AddMaterialUiState(
    val materialName: String = "",
    val quantity: String = "",
    val selectedUnit: MaterialUnit = MaterialUnit.PIECES,
    val price: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val materialSaved: Boolean = false,
    val isFormValid: Boolean = false,
    val isEditMode: Boolean = false,
    val editingMaterialId: String? = null
)
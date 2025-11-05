package com.br444n.constructionmaterialtrack.presentation.screens.add_material

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.br444n.constructionmaterialtrack.data.local.database.ConstructionDatabase
import com.br444n.constructionmaterialtrack.data.repository.MaterialRepositoryImpl
import com.br444n.constructionmaterialtrack.domain.model.Material
import com.br444n.constructionmaterialtrack.domain.repository.MaterialRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddMaterialViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: MaterialRepository
    
    private val _uiState = MutableStateFlow(AddMaterialUiState())
    val uiState: StateFlow<AddMaterialUiState> = _uiState.asStateFlow()
    
    init {
        val database = ConstructionDatabase.getDatabase(application)
        repository = MaterialRepositoryImpl(database.materialDao())
    }
    
    fun updateMaterialName(name: String) {
        _uiState.value = _uiState.value.copy(
            materialName = name,
            isFormValid = validateForm(name, _uiState.value.quantity, _uiState.value.price)
        )
    }
    
    fun updateQuantity(quantity: String) {
        _uiState.value = _uiState.value.copy(
            quantity = quantity,
            isFormValid = validateForm(_uiState.value.materialName, quantity, _uiState.value.price)
        )
    }
    
    fun updatePrice(price: String) {
        _uiState.value = _uiState.value.copy(
            price = price,
            isFormValid = validateForm(_uiState.value.materialName, _uiState.value.quantity, price)
        )
    }
    
    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }
    
    fun updateSelectedUnit(unit: com.br444n.constructionmaterialtrack.domain.model.MaterialUnit) {
        _uiState.value = _uiState.value.copy(selectedUnit = unit)
    }
    
    private fun validateForm(name: String, quantity: String, price: String): Boolean {
        return name.isNotBlank() && quantity.isNotBlank() && price.isNotBlank()
    }
    
    fun saveMaterial(projectId: String) {
        val currentState = _uiState.value
        if (!currentState.isFormValid) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
            
            // Delay mínimo para mostrar la animación Lottie
            delay(2500L)
            
            try {
                if (currentState.isEditMode && currentState.editingMaterialId != null) {
                    // Update existing material
                    val updatedMaterial = Material(
                        id = currentState.editingMaterialId,
                        name = currentState.materialName,
                        quantity = currentState.quantity,
                        unit = currentState.selectedUnit.shortName,
                        price = currentState.price,
                        description = currentState.description,
                        isPurchased = originalMaterial?.isPurchased ?: false // Keep original purchase status
                    )
                    repository.updateMaterial(updatedMaterial)
                } else {
                    // Create new material
                    val material = Material(
                        id = "", // Repository will generate ID
                        name = currentState.materialName,
                        quantity = currentState.quantity,
                        unit = currentState.selectedUnit.shortName,
                        price = currentState.price,
                        description = currentState.description,
                        isPurchased = false
                    )
                    repository.insertMaterial(material, projectId)
                }
                
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    materialSaved = true,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "Failed to save material"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun resetSavedState() {
        _uiState.value = _uiState.value.copy(materialSaved = false)
    }
    
    fun initializeForEdit(material: Material) {
        _uiState.value = _uiState.value.copy(
            materialName = material.name,
            quantity = material.quantity,
            selectedUnit = com.br444n.constructionmaterialtrack.domain.model.MaterialUnit.fromShortName(material.unit),
            price = material.price,
            description = material.description,
            isEditMode = true,
            editingMaterialId = material.id,
            isFormValid = validateForm(material.name, material.quantity, material.price)
        )
    }
    
    private var originalMaterial: Material? = null
    
    fun resetToAddMode() {
        _uiState.value = AddMaterialUiState()
    }
    
    fun loadMaterialForEdit(materialId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val material = repository.getMaterialById(materialId)
                if (material != null) {
                    originalMaterial = material
                    initializeForEdit(material)
                }
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load material"
                )
            }
        }
    }
}
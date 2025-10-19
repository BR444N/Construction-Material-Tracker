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
                val material = Material(
                    id = "", // Repository will generate ID
                    name = currentState.materialName,
                    quantity = currentState.quantity,
                    price = currentState.price,
                    description = currentState.description,
                    isPurchased = false
                )
                
                repository.insertMaterial(material, projectId)
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
}
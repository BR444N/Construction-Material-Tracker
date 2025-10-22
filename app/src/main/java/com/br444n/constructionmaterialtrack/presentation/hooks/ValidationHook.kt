package com.br444n.constructionmaterialtrack.presentation.hooks

import androidx.compose.runtime.*
import com.br444n.constructionmaterialtrack.core.security.InputValidator

/**
 * Composable hook for form validation state management
 */
@Composable
fun rememberValidationState(): ValidationState {
    return remember { ValidationState() }
}

/**
 * Validation state manager for forms
 */
@Stable
class ValidationState {
    private val _fieldErrors = mutableStateMapOf<String, String>()
    val fieldErrors: Map<String, String> = _fieldErrors
    
    private val _isValidating = mutableStateOf(false)
    val isValidating: Boolean by _isValidating
    
    val hasErrors: Boolean
        get() = _fieldErrors.isNotEmpty()
    
    val isValid: Boolean
        get() = !hasErrors && !isValidating
    
    /**
     * Validate a single field
     */
    fun validateField(
        fieldName: String,
        value: String,
        validationType: ValidationType
    ): Boolean {
        val result = when (validationType) {
            ValidationType.PROJECT_NAME -> InputValidator.validateProjectName(value)
            ValidationType.MATERIAL_NAME -> InputValidator.validateMaterialName(value)
            ValidationType.DESCRIPTION -> InputValidator.validateDescription(value)
            ValidationType.PRICE -> InputValidator.validatePrice(value)
            ValidationType.QUANTITY -> InputValidator.validateQuantity(value)
            ValidationType.TEXT -> InputValidator.ValidationResult(true, value) // Basic text, no validation
        }
        
        if (result.isValid) {
            _fieldErrors.remove(fieldName)
        } else {
            _fieldErrors[fieldName] = result.errorMessage
        }
        
        return result.isValid
    }
    
    /**
     * Validate project form
     */
    fun validateProject(name: String, description: String): Boolean {
        _isValidating.value = true
        
        val results = InputValidator.validateProjectData(name, description)
        
        _fieldErrors.clear()
        results.forEach { (field, result) ->
            if (!result.isValid) {
                _fieldErrors[field] = result.errorMessage
            }
        }
        
        _isValidating.value = false
        return !hasErrors
    }
    
    /**
     * Validate material form
     */
    fun validateMaterial(
        name: String,
        quantity: String,
        price: String,
        description: String
    ): Boolean {
        _isValidating.value = true
        
        val results = InputValidator.validateMaterialData(name, quantity, price, description)
        
        _fieldErrors.clear()
        results.forEach { (field, result) ->
            if (!result.isValid) {
                _fieldErrors[field] = result.errorMessage
            }
        }
        
        _isValidating.value = false
        return !hasErrors
    }
    
    /**
     * Clear all validation errors
     */
    fun clearErrors() {
        _fieldErrors.clear()
    }
    
    /**
     * Clear error for specific field
     */
    fun clearFieldError(fieldName: String) {
        _fieldErrors.remove(fieldName)
    }
    
    /**
     * Get error message for specific field
     */
    fun getFieldError(fieldName: String): String? {
        return _fieldErrors[fieldName]
    }
    
    /**
     * Set custom error for field
     */
    fun setFieldError(fieldName: String, error: String) {
        _fieldErrors[fieldName] = error
    }
}

/**
 * Validation types for different fields
 */
enum class ValidationType {
    PROJECT_NAME,
    MATERIAL_NAME,
    DESCRIPTION,
    PRICE,
    QUANTITY,
    TEXT
}
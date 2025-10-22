package com.br444n.constructionmaterialtrack.core.security

import android.net.Uri
import android.webkit.MimeTypeMap
import java.util.regex.Pattern

/**
 * Input validation and sanitization utilities for security
 */
object InputValidator {
    
    // Maximum lengths for different input types
    private const val MAX_PROJECT_NAME_LENGTH = 100
    private const val MAX_MATERIAL_NAME_LENGTH = 100
    private const val MAX_DESCRIPTION_LENGTH = 500
    private const val MAX_PRICE_LENGTH = 10
    private const val MAX_QUANTITY_LENGTH = 8
    
    // Allowed characters patterns
    private val ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s\\-_.,()]+$")
    private val NUMERIC_PATTERN = Pattern.compile("^[0-9.]+$")
    private val DESCRIPTION_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s\\-_.,()!?@#%&*+=\\n\\r]+$")
    
    // SQL injection patterns to detect
    private val SQL_INJECTION_PATTERNS = listOf(
        Pattern.compile("('|(\\-\\-)|(;)|(\\|)|(\\*)|(%))", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(union|select|insert|update|delete|drop|create|alter|exec|execute)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(script|javascript|vbscript|onload|onerror)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(<|>|&lt;|&gt;)", Pattern.CASE_INSENSITIVE)
    )
    
    // Allowed image MIME types
    private val ALLOWED_IMAGE_TYPES = setOf(
        "image/jpeg",
        "image/jpg", 
        "image/png",
        "image/webp"
    )
    
    // Maximum file size (5MB)
    private const val MAX_FILE_SIZE = 5 * 1024 * 1024L
    
    /**
     * Validation result with sanitized input
     */
    data class ValidationResult(
        val isValid: Boolean,
        val sanitizedInput: String = "",
        val errorMessage: String = ""
    )
    
    /**
     * Image validation result
     */
    data class ImageValidationResult(
        val isValid: Boolean,
        val errorMessage: String = ""
    )
    
    /**
     * Validate and sanitize project name
     */
    fun validateProjectName(input: String): ValidationResult {
        val trimmed = input.trim()
        
        if (trimmed.isEmpty()) {
            return ValidationResult(false, "", "Project name cannot be empty")
        }
        
        if (trimmed.length > MAX_PROJECT_NAME_LENGTH) {
            return ValidationResult(false, "", "Project name too long (max $MAX_PROJECT_NAME_LENGTH characters)")
        }
        
        if (containsSqlInjection(trimmed)) {
            return ValidationResult(false, "", "Invalid characters detected")
        }
        
        if (!ALPHANUMERIC_PATTERN.matcher(trimmed).matches()) {
            return ValidationResult(false, "", "Only letters, numbers, spaces and basic punctuation allowed")
        }
        
        return ValidationResult(true, sanitizeInput(trimmed))
    }
    
    /**
     * Validate and sanitize material name
     */
    fun validateMaterialName(input: String): ValidationResult {
        val trimmed = input.trim()
        
        if (trimmed.isEmpty()) {
            return ValidationResult(false, "", "Material name cannot be empty")
        }
        
        if (trimmed.length > MAX_MATERIAL_NAME_LENGTH) {
            return ValidationResult(false, "", "Material name too long (max $MAX_MATERIAL_NAME_LENGTH characters)")
        }
        
        if (containsSqlInjection(trimmed)) {
            return ValidationResult(false, "", "Invalid characters detected")
        }
        
        if (!ALPHANUMERIC_PATTERN.matcher(trimmed).matches()) {
            return ValidationResult(false, "", "Only letters, numbers, spaces and basic punctuation allowed")
        }
        
        return ValidationResult(true, sanitizeInput(trimmed))
    }
    
    /**
     * Validate and sanitize description
     */
    fun validateDescription(input: String): ValidationResult {
        val trimmed = input.trim()
        
        if (trimmed.length > MAX_DESCRIPTION_LENGTH) {
            return ValidationResult(false, "", "Description too long (max $MAX_DESCRIPTION_LENGTH characters)")
        }
        
        if (containsSqlInjection(trimmed)) {
            return ValidationResult(false, "", "Invalid characters detected")
        }
        
        if (trimmed.isNotEmpty() && !DESCRIPTION_PATTERN.matcher(trimmed).matches()) {
            return ValidationResult(false, "", "Contains invalid characters")
        }
        
        return ValidationResult(true, sanitizeInput(trimmed))
    }
    
    /**
     * Validate numeric input (price, quantity)
     */
    fun validateNumericInput(input: String, fieldName: String, maxLength: Int): ValidationResult {
        val trimmed = input.trim()
        
        if (trimmed.isEmpty()) {
            return ValidationResult(false, "", "$fieldName cannot be empty")
        }
        
        if (trimmed.length > maxLength) {
            return ValidationResult(false, "", "$fieldName too long")
        }
        
        if (!NUMERIC_PATTERN.matcher(trimmed).matches()) {
            return ValidationResult(false, "", "$fieldName must contain only numbers and decimal point")
        }
        
        // Check for valid decimal format
        val decimalCount = trimmed.count { it == '.' }
        if (decimalCount > 1) {
            return ValidationResult(false, "", "Invalid decimal format")
        }
        
        // Validate numeric range
        try {
            val value = trimmed.toDouble()
            if (value < 0) {
                return ValidationResult(false, "", "$fieldName cannot be negative")
            }
            if (value > 999999.99) {
                return ValidationResult(false, "", "$fieldName value too large")
            }
        } catch (e: NumberFormatException) {
            return ValidationResult(false, "", "Invalid number format")
        }
        
        return ValidationResult(true, trimmed)
    }
    
    /**
     * Validate price input
     */
    fun validatePrice(input: String): ValidationResult {
        return validateNumericInput(input, "Price", MAX_PRICE_LENGTH)
    }
    
    /**
     * Validate quantity input
     */
    fun validateQuantity(input: String): ValidationResult {
        return validateNumericInput(input, "Quantity", MAX_QUANTITY_LENGTH)
    }
    
    /**
     * Validate image URI and file properties
     */
    fun validateImageUri(uri: Uri?, context: android.content.Context): ImageValidationResult {
        if (uri == null) {
            return ImageValidationResult(true) // Null URI is valid (no image selected)
        }
        
        try {
            // Check if URI is accessible
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
                ?: return ImageValidationResult(false, "Cannot access selected image")
            
            // Check file size
            val fileSize = inputStream.available().toLong()
            inputStream.close()
            
            if (fileSize > MAX_FILE_SIZE) {
                return ImageValidationResult(false, "Image file too large (max 5MB)")
            }
            
            // Check MIME type
            val mimeType = contentResolver.getType(uri)
            if (mimeType == null || !ALLOWED_IMAGE_TYPES.contains(mimeType.lowercase())) {
                return ImageValidationResult(false, "Unsupported image format. Use JPEG, PNG, or WebP")
            }
            
            // Additional security check - verify file extension matches MIME type
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
            if (extension == null) {
                return ImageValidationResult(false, "Invalid image file")
            }
            
            return ImageValidationResult(true)
            
        } catch (e: SecurityException) {
            return ImageValidationResult(false, "Permission denied to access image")
        } catch (e: Exception) {
            return ImageValidationResult(false, "Error validating image: ${e.message}")
        }
    }
    
    /**
     * Check for SQL injection patterns
     */
    private fun containsSqlInjection(input: String): Boolean {
        return SQL_INJECTION_PATTERNS.any { pattern ->
            pattern.matcher(input).find()
        }
    }
    
    /**
     * Sanitize input by removing potentially dangerous characters
     */
    private fun sanitizeInput(input: String): String {
        return input
            .replace(Regex("[<>\"'&]"), "") // Remove HTML/XML characters
            .replace(Regex("\\s+"), " ") // Normalize whitespace
            .trim()
    }
    
    /**
     * Validate all project fields at once
     */
    fun validateProjectData(
        name: String,
        description: String
    ): Map<String, ValidationResult> {
        return mapOf(
            "name" to validateProjectName(name),
            "description" to validateDescription(description)
        )
    }
    
    /**
     * Validate all material fields at once
     */
    fun validateMaterialData(
        name: String,
        quantity: String,
        price: String,
        description: String
    ): Map<String, ValidationResult> {
        return mapOf(
            "name" to validateMaterialName(name),
            "quantity" to validateQuantity(quantity),
            "price" to validatePrice(price),
            "description" to validateDescription(description)
        )
    }
}
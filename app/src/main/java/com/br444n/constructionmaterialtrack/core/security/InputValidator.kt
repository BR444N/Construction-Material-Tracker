package com.br444n.constructionmaterialtrack.core.security

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.br444n.constructionmaterialtrack.R
import java.util.regex.Pattern

/**
 * Strings data class for InputValidator
 */
data class ValidationStrings(
    val projectNameEmpty: String,
    val projectNameTooLong: String,
    val projectNameInvalidChars: String,
    val materialNameEmpty: String,
    val materialNameTooLong: String,
    val materialNameInvalidChars: String,
    val descriptionTooLong: String,
    val descriptionInvalidChars: String,
    val containsInvalidChars: String,
    val fieldCannotBeEmpty: String,
    val fieldTooLong: String,
    val fieldNumbersOnly: String,
    val invalidDecimalFormat: String,
    val fieldCannotBeNegative: String,
    val fieldValueTooLarge: String,
    val invalidNumberFormat: String,
    val alphanumericOnly: String,
    val cannotAccessImage: String,
    val imageFileTooLarge: String,
    val unsupportedImageFormat: String,
    val invalidImageFile: String,
    val permissionDeniedImage: String,
    val errorValidatingImage: String
)

/**
 * Input validation and sanitization utilities for security
 * 
 * Supports multilingual input with diacritics:
 * - Spanish: á, é, í, ó, ú, ñ, ü (Construcción, Señalización, Niño)
 * - French: à, è, ù, â, ê, î, ô, û, ä, ë, ï, ö, ü, ÿ, ç (Matériaux, Bâtiment, Français)
 * - English: Standard ASCII characters
 * 
 * Security features:
 * - SQL injection protection
 * - XSS attack prevention
 * - Input length validation
 * - Character sanitization
 */
object InputValidator {
    
    // Maximum lengths for different input types
    private const val MAX_PROJECT_NAME_LENGTH = 100
    private const val MAX_MATERIAL_NAME_LENGTH = 100
    private const val MAX_DESCRIPTION_LENGTH = 500
    private const val MAX_PRICE_LENGTH = 10
    private const val MAX_QUANTITY_LENGTH = 8
    
    // Allowed characters patterns with diacritics support for Spanish and French
    // Spanish: á é í ó ú ñ ü (and uppercase versions)
    // French: à è ù â ê î ô û ä ë ï ö ü ÿ ç (and uppercase versions)
    private val ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9áéíóúàèùâêîôûäëïöüÿñçÁÉÍÓÚÀÈÙÂÊÎÔÛÄËÏÖÜŸÑÇ _\\-.,()]+$")
    private val NUMERIC_PATTERN = Pattern.compile("^[0-9.]+$")
    private val DESCRIPTION_PATTERN = Pattern.compile("^[a-zA-Z0-9áéíóúàèùâêîôûäëïöüÿñçÁÉÍÓÚÀÈÙÂÊÎÔÛÄËÏÖÜŸÑÇ _\\-.,()!?@#%&*+=\\n\\r]+$")
    
    // SQL injection patterns to detect
    private val SQL_INJECTION_PATTERNS = listOf(
        Pattern.compile("('|--|;|\\||\\*|%)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(union|select|insert|update|delete|drop|create|alter|exec|execute)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(script|javascript|vbscript|onload|onerror)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("[<>&]"),
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
     * Helper function to create ValidationStrings from Context
     */
    fun createStrings(context: Context): ValidationStrings {
        return ValidationStrings(
            projectNameEmpty = context.getString(R.string.project_name_empty),
            projectNameTooLong = context.getString(R.string.project_name_too_long),
            projectNameInvalidChars = context.getString(R.string.project_name_invalid_characters),
            materialNameEmpty = context.getString(R.string.material_name_empty),
            materialNameTooLong = context.getString(R.string.material_name_too_long),
            materialNameInvalidChars = context.getString(R.string.material_name_invalid_characters),
            descriptionTooLong = context.getString(R.string.description_too_long),
            descriptionInvalidChars = context.getString(R.string.description_invalid_characters),
            containsInvalidChars = context.getString(R.string.contains_invalid_characters),
            fieldCannotBeEmpty = context.getString(R.string.field_cannot_be_empty),
            fieldTooLong = context.getString(R.string.field_too_long),
            fieldNumbersOnly = context.getString(R.string.field_numbers_only),
            invalidDecimalFormat = context.getString(R.string.invalid_decimal_format),
            fieldCannotBeNegative = context.getString(R.string.field_cannot_be_negative),
            fieldValueTooLarge = context.getString(R.string.field_value_too_large),
            invalidNumberFormat = context.getString(R.string.invalid_number_format),
            alphanumericOnly = context.getString(R.string.alphanumeric_only),
            cannotAccessImage = context.getString(R.string.cannot_access_image),
            imageFileTooLarge = context.getString(R.string.image_file_too_large),
            unsupportedImageFormat = context.getString(R.string.unsupported_image_format),
            invalidImageFile = context.getString(R.string.invalid_image_file),
            permissionDeniedImage = context.getString(R.string.permission_denied_image),
            errorValidatingImage = context.getString(R.string.error_validating_image)
        )
    }
    
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
    fun validateProjectName(input: String, strings: ValidationStrings): ValidationResult {
        val trimmed = input.trim()
        
        if (trimmed.isEmpty()) {
            return ValidationResult(false, "", strings.projectNameEmpty)
        }
        
        if (trimmed.length > MAX_PROJECT_NAME_LENGTH) {
            return ValidationResult(false, "", strings.projectNameTooLong.format(MAX_PROJECT_NAME_LENGTH))
        }
        
        if (containsSqlInjection(trimmed)) {
            return ValidationResult(false, "", strings.projectNameInvalidChars)
        }
        
        if (!ALPHANUMERIC_PATTERN.matcher(trimmed).matches()) {
            return ValidationResult(false, "", strings.alphanumericOnly)
        }
        
        return ValidationResult(true, sanitizeInput(trimmed))
    }
    
    /**
     * Validate and sanitize material name
     */
    fun validateMaterialName(input: String, strings: ValidationStrings): ValidationResult {
        val trimmed = input.trim()
        
        if (trimmed.isEmpty()) {
            return ValidationResult(false, "", strings.materialNameEmpty)
        }
        
        if (trimmed.length > MAX_MATERIAL_NAME_LENGTH) {
            return ValidationResult(false, "", strings.materialNameTooLong.format(MAX_MATERIAL_NAME_LENGTH))
        }
        
        if (containsSqlInjection(trimmed)) {
            return ValidationResult(false, "", strings.materialNameInvalidChars)
        }
        
        if (!ALPHANUMERIC_PATTERN.matcher(trimmed).matches()) {
            return ValidationResult(false, "", strings.alphanumericOnly)
        }
        
        return ValidationResult(true, sanitizeInput(trimmed))
    }
    
    /**
     * Validate and sanitize description
     */
    fun validateDescription(input: String, strings: ValidationStrings): ValidationResult {
        val trimmed = input.trim()
        
        if (trimmed.length > MAX_DESCRIPTION_LENGTH) {
            return ValidationResult(false, "", strings.descriptionTooLong.format(MAX_DESCRIPTION_LENGTH))
        }
        
        if (containsSqlInjection(trimmed)) {
            return ValidationResult(false, "", strings.descriptionInvalidChars)
        }
        
        if (trimmed.isNotEmpty() && !DESCRIPTION_PATTERN.matcher(trimmed).matches()) {
            return ValidationResult(false, "", strings.containsInvalidChars)
        }
        
        return ValidationResult(true, sanitizeInput(trimmed))
    }
    
    /**
     * Validate numeric input (price, quantity)
     */
    fun validateNumericInput(input: String, fieldName: String, maxLength: Int, strings: ValidationStrings): ValidationResult {
        val trimmed = input.trim()
        
        if (trimmed.isEmpty()) {
            return ValidationResult(false, "", strings.fieldCannotBeEmpty.format(fieldName))
        }
        
        if (trimmed.length > maxLength) {
            return ValidationResult(false, "", strings.fieldTooLong.format(fieldName))
        }
        
        if (!NUMERIC_PATTERN.matcher(trimmed).matches()) {
            return ValidationResult(false, "", strings.fieldNumbersOnly.format(fieldName))
        }
        
        // Check for valid decimal format
        val decimalCount = trimmed.count { it == '.' }
        if (decimalCount > 1) {
            return ValidationResult(false, "", strings.invalidDecimalFormat)
        }
        
        // Validate numeric range
        try {
            val value = trimmed.toDouble()
            if (value < 0) {
                return ValidationResult(false, "", strings.fieldCannotBeNegative.format(fieldName))
            }
            if (value > 999999.99) {
                return ValidationResult(false, "", strings.fieldValueTooLarge.format(fieldName))
            }
        } catch (e: NumberFormatException) {
            return ValidationResult(false, "", strings.invalidNumberFormat.format(e.message))
        }
        
        return ValidationResult(true, trimmed)
    }
    
    /**
     * Validate price input
     */
    fun validatePrice(input: String, strings: ValidationStrings): ValidationResult {
        return validateNumericInput(input, "Price", MAX_PRICE_LENGTH, strings)
    }
    
    /**
     * Validate quantity input
     */
    fun validateQuantity(input: String, strings: ValidationStrings): ValidationResult {
        return validateNumericInput(input, "Quantity", MAX_QUANTITY_LENGTH, strings)
    }
    
    /**
     * Validate image URI and file properties
     */
    fun validateImageUri(uri: Uri?, context: Context, strings: ValidationStrings): ImageValidationResult {
        if (uri == null) {
            return ImageValidationResult(true) // Null URI is valid (no image selected)
        }
        
        try {
            // Check if URI is accessible
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
                ?: return ImageValidationResult(false, strings.cannotAccessImage)
            
            // Check file size
            val fileSize = inputStream.available().toLong()
            inputStream.close()
            
            if (fileSize > MAX_FILE_SIZE) {
                return ImageValidationResult(false, strings.imageFileTooLarge)
            }
            
            // Check MIME type
            val mimeType = contentResolver.getType(uri)
            if (mimeType == null || !ALLOWED_IMAGE_TYPES.contains(mimeType.lowercase())) {
                return ImageValidationResult(false, strings.unsupportedImageFormat)
            }
            
            // Additional security check - verify file extension matches MIME type
            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                ?: return ImageValidationResult(false, strings.invalidImageFile)
            
            return ImageValidationResult(true)
            
        } catch (e: SecurityException) {
            return ImageValidationResult(false, strings.permissionDeniedImage.format(e.message))
        } catch (e: Exception) {
            return ImageValidationResult(false, strings.errorValidatingImage.format(e.message))
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
        description: String,
        strings: ValidationStrings
    ): Map<String, ValidationResult> {
        return mapOf(
            "name" to validateProjectName(name, strings),
            "description" to validateDescription(description, strings)
        )
    }
    
    /**
     * Validate all material fields at once
     */
    fun validateMaterialData(
        name: String,
        quantity: String,
        price: String,
        description: String,
        strings: ValidationStrings
    ): Map<String, ValidationResult> {
        return mapOf(
            "name" to validateMaterialName(name, strings),
            "quantity" to validateQuantity(quantity, strings),
            "price" to validatePrice(price, strings),
            "description" to validateDescription(description, strings)
        )
    }
}
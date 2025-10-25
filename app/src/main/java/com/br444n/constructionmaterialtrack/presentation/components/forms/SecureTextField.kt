package com.br444n.constructionmaterialtrack.presentation.components.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.br444n.constructionmaterialtrack.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.core.security.InputValidator
import com.br444n.constructionmaterialtrack.presentation.hooks.ValidationType
import com.br444n.constructionmaterialtrack.ui.theme.BlueDark
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme
import com.br444n.constructionmaterialtrack.ui.theme.Red
import com.br444n.constructionmaterialtrack.ui.theme.TextSecondary

/**
 * Configuration data class for SecureTextField
 */
data class SecureTextFieldConfig(
    val validationType: ValidationType = ValidationType.TEXT,
    val keyboardType: KeyboardType = KeyboardType.Text,
    val singleLine: Boolean = true,
    val maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    val minLines: Int = 1,
    val isRequired: Boolean = true,
    val leadingIcon: ImageVector? = null,
    val trailingIcon: ImageVector? = null,
    val onTrailingIconClick: (() -> Unit)? = null
)

/**
 * Get character limit based on validation type
 */
private fun getCharacterLimit(validationType: ValidationType): Int {
    return when (validationType) {
        ValidationType.PROJECT_NAME -> 100
        ValidationType.MATERIAL_NAME -> 100
        ValidationType.DESCRIPTION -> 500
        ValidationType.PRICE -> 10
        ValidationType.QUANTITY -> 8
        ValidationType.TEXT -> 255
    }
}

/**
 * Validates input based on validation type
 */
private fun validateInput(value: String, validationType: ValidationType): InputValidator.ValidationResult {
    return when (validationType) {
        ValidationType.PROJECT_NAME -> InputValidator.validateProjectName(value)
        ValidationType.MATERIAL_NAME -> InputValidator.validateMaterialName(value)
        ValidationType.DESCRIPTION -> InputValidator.validateDescription(value)
        ValidationType.PRICE -> InputValidator.validatePrice(value)
        ValidationType.QUANTITY -> InputValidator.validateQuantity(value)
        ValidationType.TEXT -> InputValidator.ValidationResult(true, value)
    }
}

/**
 * Filters input based on validation type
 */
private fun filterInput(newValue: String, validationType: ValidationType): String {
    return when (validationType) {
        ValidationType.PRICE, ValidationType.QUANTITY -> {
            newValue.filter { it.isDigit() || it == '.' }
        }
        else -> newValue
    }
}

/**
 * Applies character limit to input
 */
private fun applyCharacterLimit(value: String, validationType: ValidationType): String {
    val characterLimit = getCharacterLimit(validationType)
    return if (value.length <= characterLimit) {
        value
    } else {
        value.take(characterLimit)
    }
}

/**
 * Secure TextField with built-in validation and sanitization
 */
@Composable
fun SecureTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    config: SecureTextFieldConfig = SecureTextFieldConfig()
) {
    val validationState = rememberValidationState(value, config.validationType)
    
    val handleValueChange = remember(config.validationType) {
        { newValue: String ->
            val processedValue = processInputValue(newValue, config.validationType)
            onValueChange(processedValue)
        }
    }
    
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = handleValueChange,
            label = { 
                Text(if (config.isRequired) "$label *" else label) 
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = config.singleLine,
            maxLines = config.maxLines,
            minLines = config.minLines,
            keyboardOptions = KeyboardOptions(keyboardType = config.keyboardType),
            isError = !validationState.result.isValid && validationState.hasBeenValidated,
            leadingIcon = createLeadingIcon(config.leadingIcon, validationState),
            trailingIcon = {
                TrailingIconContent(
                    value = value,
                    validationResult = validationState.result,
                    hasBeenValidated = validationState.hasBeenValidated,
                    validationType = config.validationType,
                    onClearClick = { onValueChange("") },
                    customIcon = config.trailingIcon,
                    onCustomIconClick = config.onTrailingIconClick
                )
            },
            colors = getSecureTextFieldColors(),
            supportingText = createSupportingText(validationState)
        )
    }
}

/**
 * Validation state holder
 */
data class ValidationState(
    val result: InputValidator.ValidationResult,
    val hasBeenValidated: Boolean
)

/**
 * Remember validation state
 */
@Composable
private fun rememberValidationState(
    value: String, 
    validationType: ValidationType
): ValidationState {
    var validationResult by remember { mutableStateOf(InputValidator.ValidationResult(true)) }
    var hasBeenValidated by remember { mutableStateOf(false) }
    
    LaunchedEffect(value) {
        if (hasBeenValidated || value.isNotEmpty()) {
            validationResult = validateInput(value, validationType)
            hasBeenValidated = true
        }
    }
    
    return ValidationState(validationResult, hasBeenValidated)
}

/**
 * Process input value with filtering and limiting
 */
private fun processInputValue(newValue: String, validationType: ValidationType): String {
    val filteredValue = filterInput(newValue, validationType)
    return applyCharacterLimit(filteredValue, validationType)
}

/**
 * Create leading icon composable
 */
private fun createLeadingIcon(
    leadingIcon: ImageVector?,
    validationState: ValidationState
): (@Composable () -> Unit)? {
    return leadingIcon?.let { icon ->
        {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (!validationState.result.isValid && validationState.hasBeenValidated) Red else BlueDark
            )
        }
    }
}

/**
 * Create supporting text composable
 */
private fun createSupportingText(validationState: ValidationState): (@Composable () -> Unit)? {
    return if (!validationState.result.isValid && validationState.hasBeenValidated) {
        {
            Text(
                text = validationState.result.errorMessage,
                color = Red,
                style = MaterialTheme.typography.bodySmall
            )
        }
    } else null
}





@Composable
private fun getSecureTextFieldColors() = OutlinedTextFieldDefaults.colors(
    // Bordes
    focusedBorderColor = BlueDark,
    unfocusedBorderColor = BluePrimary,
    errorBorderColor = Red,
    disabledBorderColor = BluePrimary.copy(0.3f),

    // Labels
    focusedLabelColor = BlueDark,
    unfocusedLabelColor = BluePrimary,
    errorLabelColor = Red,
    disabledLabelColor = BluePrimary,

    // Texto
    focusedTextColor = BlueDark,
    unfocusedTextColor = BlueDark,
    errorTextColor = Red,
    disabledTextColor = TextSecondary.copy(alpha = 0.3f),

    // Cursor
    cursorColor = BlueDark,
    errorCursorColor = Red,

    // Fondo
    focusedContainerColor = BluePrimary.copy(0.1f),
    unfocusedContainerColor = BluePrimary.copy(0.05f),
    errorContainerColor = Red.copy(0.1f),
    disabledContainerColor = TextSecondary.copy(alpha = 0.05f),

    // Placeholder
    focusedPlaceholderColor = TextSecondary.copy(alpha = 0.6f),
    unfocusedPlaceholderColor = TextSecondary.copy(alpha = 0.6f),
    errorPlaceholderColor = Red.copy(alpha = 0.6f),
    disabledPlaceholderColor = TextSecondary.copy(alpha = 0.3f),

    // Supporting text
    focusedSupportingTextColor = TextSecondary,
    unfocusedSupportingTextColor = TextSecondary,
    errorSupportingTextColor = Red,
    disabledSupportingTextColor = TextSecondary.copy(alpha = 0.3f)
)

@Composable
private fun TrailingIconContent(
    value: String,
    validationResult: InputValidator.ValidationResult,
    hasBeenValidated: Boolean,
    validationType: ValidationType,
    onClearClick: () -> Unit,
    customIcon: ImageVector?,
    onCustomIconClick: (() -> Unit)?
) {
    val characterLimit = getCharacterLimit(validationType)
    val currentLength = value.length
    val isOverLimit = currentLength > characterLimit
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Clear button (show only if there's text)
        if (value.isNotEmpty()) {
            IconButton(
                onClick = onClearClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Cancel,
                    contentDescription = stringResource(R.string.clear_text),
                    tint = BluePrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        // Character counter
        Text(
            text = "$currentLength/$characterLimit",
            style = MaterialTheme.typography.bodySmall,
            color = when {
                isOverLimit -> Red
                currentLength > characterLimit * 0.8 -> BlueDark
                else -> BluePrimary
            },
            modifier = Modifier.padding(end = 4.dp)
        )
        
        // Error icon (highest priority)
        if (!validationResult.isValid && hasBeenValidated) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Error",
                tint = Red,
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 4.dp)
            )
        }
        // Custom icon (if no error and provided)
        else if (customIcon != null) {
            IconButton(
                onClick = { onCustomIconClick?.invoke() },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = customIcon,
                    contentDescription = null,
                    tint = BlueDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Valid Inputs with Counter")
@Composable
private fun SecureTextFieldValidPreview() {
    ConstructionMaterialTrackTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SecureTextField(
                value = "Valid Project Name",
                onValueChange = {},
                label = stringResource(R.string.project_name),
                config = SecureTextFieldConfig(
                    validationType = ValidationType.PROJECT_NAME,
                    keyboardType = KeyboardType.Text
                )
            )
            
            SecureTextField(
                value = "123.45",
                onValueChange = {},
                label = "Price",
                config = SecureTextFieldConfig(
                    validationType = ValidationType.PRICE,
                    keyboardType = KeyboardType.Decimal
                )
            )
            
            SecureTextField(
                value = "100",
                onValueChange = {},
                label = "Quantity",
                config = SecureTextFieldConfig(
                    validationType = ValidationType.QUANTITY,
                    keyboardType = KeyboardType.Number
                )
            )
            
            SecureTextField(
                value = "This is a very long description that shows how the character counter works in real time",
                onValueChange = {},
                label = "Description",
                config = SecureTextFieldConfig(
                    validationType = ValidationType.DESCRIPTION,
                    keyboardType = KeyboardType.Text,
                    singleLine = false,
                    maxLines = 3
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "Security Errors")
@Composable
private fun SecureTextFieldErrorPreview() {
    ConstructionMaterialTrackTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Simular campo con SQL injection
            Column {
                OutlinedTextField(
                    value = "'; DROP TABLE users; --",
                    onValueChange = {},
                    label = {stringResource(R.string.project_name)},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = Red
                        )
                    },
                    colors = getSecureTextFieldColors(),
                    supportingText = {
                        Text(
                            text = "Invalid characters detected now",
                            color = Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                )
            }
            
            // Simular campo con XSS
            Column {
                OutlinedTextField(
                    value = "<script>alert('XSS')</script>",
                    onValueChange = {},
                    label = { Text("Description *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = Red
                        )
                    },
                    colors = getSecureTextFieldColors(),
                    supportingText = {
                        Text(
                            text = "Invalid characters detected here",
                            color = Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                )
            }
            
            // Simular campo con precio inválido
            Column {
                OutlinedTextField(
                    value = "abc123",
                    onValueChange = {},
                    label = { Text("Price *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = Red
                        )
                    },
                    colors = getSecureTextFieldColors(),
                    supportingText = {
                        Text(
                            text = "Price must contain only numbers and decimal point",
                            color = Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Length Limit Errors")
@Composable
private fun SecureTextFieldLengthErrorPreview() {
    ConstructionMaterialTrackTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Simular nombre muy largo
            Column {
                OutlinedTextField(
                    value = "A".repeat(105) + "...",
                    onValueChange = {},
                    label = { Text("Project Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = Red
                        )
                    },
                    colors = getSecureTextFieldColors(),
                    supportingText = {
                        Text(
                            text = "Project name too long (max 100 characters)",
                            color = Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                )
            }
            
            // Simular descripción muy larga
            Column {
                OutlinedTextField(
                    value = "Very long description that exceeds the maximum allowed length...",
                    onValueChange = {},
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 3,
                    isError = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = Red
                        )
                    },
                    colors = getSecureTextFieldColors(),
                    supportingText = {
                        Text(
                            text = "Description too long (max 500 characters)",
                            color = Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Character Counter States")
@Composable
private fun SecureTextFieldCounterPreview() {
    ConstructionMaterialTrackTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Normal state
            SecureTextField(
                value = "Short text",
                onValueChange = {},
                label = "Project Name",
                config = SecureTextFieldConfig(
                    validationType = ValidationType.PROJECT_NAME
                )
            )
            
            // Near limit (80% of 100 chars = 80 chars)
            SecureTextField(
                value = "A".repeat(85),
                onValueChange = {},
                label = "Project Name (Near Limit)",
                config = SecureTextFieldConfig(
                    validationType = ValidationType.PROJECT_NAME
                )
            )
            
            // At limit
            SecureTextField(
                value = "A".repeat(100),
                onValueChange = {},
                label = "Project Name (At Limit)",
                config = SecureTextFieldConfig(
                    validationType = ValidationType.PROJECT_NAME
                )
            )
            
            // Price with counter
            SecureTextField(
                value = "1234.56",
                onValueChange = {},
                label = "Price",
                config = SecureTextFieldConfig(
                    validationType = ValidationType.PRICE,
                    keyboardType = KeyboardType.Decimal
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "Security Comparison")
@Composable
private fun SecureTextFieldComparisonPreview() {
    ConstructionMaterialTrackTheme {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "✅ SECURE TextField",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Try typing: '; DROP TABLE users; --",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SecureTextField(
                            value = "",
                            onValueChange = {},
                            label = "Project Name",
                            config = SecureTextFieldConfig(
                                validationType = ValidationType.PROJECT_NAME
                            )
                        )
                    }
                }
            }
            
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "❌ BLOCKED: SQL Injection",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = "'; DROP TABLE users; --",
                            onValueChange = {},
                            label = { Text("Project Name *") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            isError = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Error",
                                    tint = Red
                                )
                            },
                            colors = getSecureTextFieldColors(),
                            supportingText = {
                                Text(
                                    text = "Invalid characters detected",
                                    color = Red,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        )
                    }
                }
            }
            
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "❌ BLOCKED: XSS Attack",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = "<script>alert('XSS')</script>",
                            onValueChange = {},
                            label = { Text("Description *") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            isError = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Error",
                                    tint = Red
                                )
                            },
                            colors = getSecureTextFieldColors(),
                            supportingText = {
                                Text(
                                    text = "Invalid characters detected",
                                    color = Red,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
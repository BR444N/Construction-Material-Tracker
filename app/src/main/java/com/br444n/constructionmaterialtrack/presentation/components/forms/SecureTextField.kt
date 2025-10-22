package com.br444n.constructionmaterialtrack.presentation.components.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
 * Secure TextField with built-in validation and sanitization
 */
@Composable
fun SecureTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    validationType: ValidationType = ValidationType.TEXT,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    isRequired: Boolean = true
) {
    var validationResult by remember { mutableStateOf(InputValidator.ValidationResult(true)) }
    var hasBeenValidated by remember { mutableStateOf(false) }
    
    // Validate input when value changes
    LaunchedEffect(value) {
        if (hasBeenValidated || value.isNotEmpty()) {
            validationResult = when (validationType) {
                ValidationType.PROJECT_NAME -> InputValidator.validateProjectName(value)
                ValidationType.MATERIAL_NAME -> InputValidator.validateMaterialName(value)
                ValidationType.DESCRIPTION -> InputValidator.validateDescription(value)
                ValidationType.PRICE -> InputValidator.validatePrice(value)
                ValidationType.QUANTITY -> InputValidator.validateQuantity(value)
                ValidationType.TEXT -> InputValidator.ValidationResult(true, value)
            }
            hasBeenValidated = true
        }
    }
    
    // Handle value changes with validation
    fun handleValueChange(newValue: String) {
        // Real-time character filtering for numeric inputs
        val filteredValue = when (validationType) {
            ValidationType.PRICE, ValidationType.QUANTITY -> {
                newValue.filter { it.isDigit() || it == '.' }
            }
            else -> newValue
        }
        
        onValueChange(filteredValue)
    }
    
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = ::handleValueChange,
            label = { 
                Text(if (isRequired) "$label *" else label) 
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = singleLine,
            maxLines = maxLines,
            minLines = minLines,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = !validationResult.isValid && hasBeenValidated,
            leadingIcon = leadingIcon?.let { icon ->
                {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (!validationResult.isValid && hasBeenValidated) Red else BlueDark
                    )
                }
            },
            trailingIcon = if (!validationResult.isValid && hasBeenValidated) {
                {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = Red
                    )
                }
            } else trailingIcon?.let { icon ->
                {
                    IconButton(onClick = { onTrailingIconClick?.invoke() }) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = BlueDark
                        )
                    }
                }
            },
            colors = getSecureTextFieldColors(),
            supportingText = if (!validationResult.isValid && hasBeenValidated) {
                {
                    Text(
                        text = validationResult.errorMessage,
                        color = Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else null
        )
    }
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

@Preview(showBackground = true, name = "Valid Inputs")
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
                label = "Project Name",
                validationType = ValidationType.PROJECT_NAME,
                keyboardType = KeyboardType.Text
            )
            
            SecureTextField(
                value = "123.45",
                onValueChange = {},
                label = "Price",
                validationType = ValidationType.PRICE,
                keyboardType = KeyboardType.Decimal
            )
            
            SecureTextField(
                value = "100",
                onValueChange = {},
                label = "Quantity",
                validationType = ValidationType.QUANTITY,
                keyboardType = KeyboardType.Number
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
                            text = "Invalid characters detected",
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
                            validationType = ValidationType.PROJECT_NAME
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
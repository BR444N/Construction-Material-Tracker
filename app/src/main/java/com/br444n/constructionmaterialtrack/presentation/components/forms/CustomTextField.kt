package com.br444n.constructionmaterialtrack.presentation.components.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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


@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1,
    maxLines: Int = 1,
    leadingIcon: ImageVector? = Icons.Default.Badge,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    validationType: ValidationType? = null,
    enableSecurity: Boolean = false
) {
    var validationResult by remember { mutableStateOf(InputValidator.ValidationResult(true)) }
    var hasBeenValidated by remember { mutableStateOf(false) }
    
    // Apply security validation if enabled
    LaunchedEffect(value) {
        if (enableSecurity && validationType != null && (hasBeenValidated || value.isNotEmpty())) {
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
    
    // Handle secure value changes
    fun handleValueChange(newValue: String) {
        val filteredValue = if (enableSecurity && validationType != null) {
            when (validationType) {
                ValidationType.PRICE, ValidationType.QUANTITY -> {
                    newValue.filter { it.isDigit() || it == '.' }
                }
                else -> newValue
            }
        } else {
            newValue
        }
        onValueChange(filteredValue)
    }
    
    val finalIsError = if (enableSecurity) {
        isError || (!validationResult.isValid && hasBeenValidated)
    } else {
        isError
    }
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = ::handleValueChange,
        label = { 
            Text(if (enableSecurity && validationType != ValidationType.DESCRIPTION) "$label *" else label) 
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = finalIsError,
        minLines = minLines,
        maxLines = maxLines,
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = if (finalIsError) Red else BlueDark
                )
            }
        },
        trailingIcon = if (enableSecurity && !validationResult.isValid && hasBeenValidated) {
            {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Error",
                    tint = Red
                )
            }
        } else trailingIcon?.let {
            {
                IconButton(
                    onClick = { onTrailingIconClick?.invoke() }
                ) {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (finalIsError) Red else BlueDark
                    )
                }
            }
        },
        supportingText = if (enableSecurity && !validationResult.isValid && hasBeenValidated) {
            {
                Text(
                    text = validationResult.errorMessage,
                    color = Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        } else null,        
colors = OutlinedTextFieldDefaults.colors(
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

            // Iconos de soporte (si los usas)
            focusedSupportingTextColor = TextSecondary,
            unfocusedSupportingTextColor = TextSecondary,
            errorSupportingTextColor = Red,
            disabledSupportingTextColor = TextSecondary.copy(alpha = 0.3f)
        )
    )
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomTextFieldPreview() {
    ConstructionMaterialTrackTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Regular TextField (no security)
            CustomTextField(
                value = "",
                onValueChange = {},
                label = "Material Name",
                leadingIcon = Icons.Default.Badge
            )
            
            // Secure TextField with validation
            CustomTextField(
                value = "Test Project",
                onValueChange = {},
                label = "Project Name",
                leadingIcon = Icons.Default.Badge,
                validationType = ValidationType.PROJECT_NAME,
                enableSecurity = true
            )
            
            // Secure TextField with error (invalid characters)
            CustomTextField(
                value = "invalid@#$%",
                onValueChange = {},
                label = "Material Name",
                leadingIcon = Icons.Default.Badge,
                validationType = ValidationType.MATERIAL_NAME,
                enableSecurity = true
            )
            
            // Secure Description Field
            CustomTextField(
                value = "",
                onValueChange = {},
                label = "Description",
                leadingIcon = Icons.Default.Description,
                singleLine = false,
                maxLines = 3,
                validationType = ValidationType.DESCRIPTION,
                enableSecurity = true
            )
        }
    }
}
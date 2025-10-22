package com.br444n.constructionmaterialtrack.presentation.components.forms

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExposurePlus1
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.presentation.components.images.SecureImagePicker
import com.br444n.constructionmaterialtrack.presentation.hooks.ValidationState
import com.br444n.constructionmaterialtrack.presentation.hooks.ValidationType
import com.br444n.constructionmaterialtrack.presentation.hooks.rememberValidationState
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

/**
 * Example of secure form implementation
 */
@Composable
fun SecureFormExample(
    modifier: Modifier = Modifier
) {
    val validationState = rememberValidationState()
    
    var projectName by remember { mutableStateOf("") }
    var materialName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var imageValidationError by remember { mutableStateOf("") }
    
    fun validateAndSubmit() {
        val isProjectValid = validationState.validateProject(projectName, description)
        val isMaterialValid = validationState.validateMaterial(materialName, quantity, price, description)
        val hasImageError = imageValidationError.isNotEmpty()
        
        if (isProjectValid && isMaterialValid && !hasImageError) {
            // Form is valid - proceed with submission
            // In real implementation, call your ViewModel/Repository here
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Secure Form Example",
            style = MaterialTheme.typography.headlineMedium
        )
        
        // Secure Image Picker
        SecureImagePicker(
            selectedImageUri = selectedImageUri,
            onImageSelected = { uri -> 
                selectedImageUri = uri
                imageValidationError = ""
            },
            onValidationError = { error ->
                imageValidationError = error
            }
        )
        
        // Project Name Field
        SecureTextField(
            value = projectName,
            onValueChange = { 
                projectName = it
                validationState.clearFieldError("name")
            },
            label = "Project Name",
            validationType = ValidationType.PROJECT_NAME,
            leadingIcon = Icons.Default.Title,
            keyboardType = KeyboardType.Text
        )
        
        // Material Name Field
        SecureTextField(
            value = materialName,
            onValueChange = { 
                materialName = it
                validationState.clearFieldError("name")
            },
            label = "Material Name",
            validationType = ValidationType.MATERIAL_NAME,
            leadingIcon = Icons.Default.Title,
            keyboardType = KeyboardType.Text
        )
        
        // Quantity and Price Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SecureTextField(
                value = quantity,
                onValueChange = { 
                    quantity = it
                    validationState.clearFieldError("quantity")
                },
                label = "Quantity",
                validationType = ValidationType.QUANTITY,
                leadingIcon = Icons.Default.ExposurePlus1,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
            
            SecureTextField(
                value = price,
                onValueChange = { 
                    price = it
                    validationState.clearFieldError("price")
                },
                label = "Price ($)",
                validationType = ValidationType.PRICE,
                leadingIcon = Icons.Default.AttachMoney,
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Description Field
        SecureTextField(
            value = description,
            onValueChange = { 
                description = it
                validationState.clearFieldError("description")
            },
            label = "Description",
            validationType = ValidationType.DESCRIPTION,
            leadingIcon = Icons.Default.Description,
            keyboardType = KeyboardType.Text,
            singleLine = false,
            maxLines = 3,
            isRequired = false
        )
        
        // Submit Button
        Button(
            onClick = ::validateAndSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = !validationState.isValidating
        ) {
            if (validationState.isValidating) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text("Submit Secure Form")
        }
        
        // Validation Status
        if (validationState.hasErrors || imageValidationError.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Validation Errors:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    
                    validationState.fieldErrors.forEach { (field, error) ->
                        Text(
                            text = "• $field: $error",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    
                    if (imageValidationError.isNotEmpty()) {
                        Text(
                            text = "• Image: $imageValidationError",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SecureFormExamplePreview() {
    ConstructionMaterialTrackTheme {
        SecureFormExample()
    }
}
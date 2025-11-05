package com.br444n.constructionmaterialtrack.presentation.screens.add_material

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExposurePlus1
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.br444n.constructionmaterialtrack.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import com.br444n.constructionmaterialtrack.presentation.components.forms.SecureTextField
import com.br444n.constructionmaterialtrack.presentation.components.forms.SecureTextFieldConfig
import com.br444n.constructionmaterialtrack.presentation.components.forms.UnitDropdown
import com.br444n.constructionmaterialtrack.presentation.components.navigation.CustomTopAppBar
import com.br444n.constructionmaterialtrack.presentation.components.states.ErrorMessage
import com.br444n.constructionmaterialtrack.presentation.hooks.ValidationType
import com.br444n.constructionmaterialtrack.presentation.components.buttons.SaveButton
import com.br444n.constructionmaterialtrack.presentation.components.buttons.SaveButtonConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMaterialScreen(
    viewModel: AddMaterialViewModel,
    projectId: String,
    materialId: String? = null,
    onBackClick: () -> Unit = {},
    onMaterialSaved: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Initialize for edit mode if materialId is provided
    LaunchedEffect(materialId) {
        if (materialId != null) {
            viewModel.loadMaterialForEdit(materialId)
        } else {
            viewModel.resetToAddMode()
        }
    }
    
    // Handle material saved
    LaunchedEffect(uiState.materialSaved) {
        if (uiState.materialSaved) {
            onMaterialSaved()
            viewModel.resetSavedState()
        }
    }
    
    Scaffold(
        topBar = {
            CustomTopAppBar(
                title = if (uiState.isEditMode) {
                    stringResource(R.string.edit_material)
                } else {
                    stringResource(R.string.add_material)
                },
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Secure Material Name Field
            SecureTextField(
                value = uiState.materialName,
                onValueChange = { viewModel.updateMaterialName(it) },
                label = stringResource(R.string.material_name),
                modifier = Modifier.fillMaxWidth(),
                config = SecureTextFieldConfig(
                    validationType = ValidationType.MATERIAL_NAME,
                    leadingIcon = Icons.Default.Badge
                )
            )
            
            // Secure Quantity Field
            SecureTextField(
                value = uiState.quantity,
                onValueChange = { viewModel.updateQuantity(it) },
                label = stringResource(R.string.quantity),
                modifier = Modifier.fillMaxWidth(),
                config = SecureTextFieldConfig(
                    validationType = ValidationType.QUANTITY,
                    leadingIcon = Icons.Default.ExposurePlus1,
                    keyboardType = KeyboardType.Number
                )
            )
            
            // Unit Dropdown
            UnitDropdown(
                selectedUnit = uiState.selectedUnit,
                onUnitSelected = { viewModel.updateSelectedUnit(it) },
                label = stringResource(R.string.unit_of_measurement),
                modifier = Modifier.fillMaxWidth()
            )
            
            // Secure Price Field
            SecureTextField(
                value = uiState.price,
                onValueChange = { viewModel.updatePrice(it) },
                label = stringResource(R.string.price),
                modifier = Modifier.fillMaxWidth(),
                config = SecureTextFieldConfig(
                    validationType = ValidationType.PRICE,
                    leadingIcon = Icons.Default.AttachMoney,
                    keyboardType = KeyboardType.Decimal
                )
            )
            
            // Secure Description Field (Optional)
            SecureTextField(
                value = uiState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = stringResource(R.string.description_optional),
                modifier = Modifier.fillMaxWidth(),
                config = SecureTextFieldConfig(
                    validationType = ValidationType.DESCRIPTION,
                    leadingIcon = Icons.Default.Description,
                    singleLine = false,
                    maxLines = 4,
                    minLines = 2,
                    isRequired = false
                )
            )
            
            // Error message
            if (uiState.errorMessage != null) {
                ErrorMessage(
                    message = uiState.errorMessage ?: "",
                    onDismiss = { viewModel.clearError() }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Save Button
            SaveButton(
                text = if (uiState.isEditMode) {
                    stringResource(R.string.save)
                } else {
                    stringResource(R.string.save_material)
                },
                onClick = { viewModel.saveMaterial(projectId) },
                config = SaveButtonConfig(
                    enabled = uiState.isFormValid,
                    isLoading = uiState.isSaving
                )
            )
        }
    }
}

package com.br444n.constructionmaterialtrack.presentation.screens.add_material

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ExposurePlus1
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.br444n.constructionmaterialtrack.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.constructionmaterialtrack.presentation.components.forms.CustomTextField
import com.br444n.constructionmaterialtrack.presentation.components.navigation.CustomTopAppBar
import com.br444n.constructionmaterialtrack.presentation.components.states.ErrorMessage
import com.br444n.constructionmaterialtrack.presentation.components.forms.MultilineTextField
import com.br444n.constructionmaterialtrack.presentation.components.forms.NumberTextField
import com.br444n.constructionmaterialtrack.presentation.model.NumberTextFieldConfig
import com.br444n.constructionmaterialtrack.presentation.components.buttons.SaveButton
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMaterialScreen(
    viewModel: AddMaterialViewModel,
    projectId: String,
    onBackClick: () -> Unit = {},
    onMaterialSaved: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
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
                title = stringResource(R.string.add_material),
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
            // Material Name Field
            CustomTextField(
                value = uiState.materialName,
                onValueChange = { viewModel.updateMaterialName(it) },
                label = stringResource(R.string.material_name),
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.materialName.isBlank() && uiState.materialName.isNotEmpty()
            )
            
            // Quantity and Price Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NumberTextField(
                    config = NumberTextFieldConfig.quantity(
                        label = stringResource(R.string.quantity),
                        leadingIcon = Icons.Default.ExposurePlus1,
                        isError = uiState.quantity.isBlank() && uiState.quantity.isNotEmpty()
                    ),
                    value = uiState.quantity,
                    onValueChange = { viewModel.updateQuantity(it) },
                    modifier = Modifier.weight(1f)
                )
                
                NumberTextField(
                    config = NumberTextFieldConfig.currency(
                        label = stringResource(R.string.price),
                        leadingIcon = Icons.Default.AttachMoney,
                        isError = uiState.price.isBlank() && uiState.price.isNotEmpty()
                    ),
                    value = uiState.price,
                    onValueChange = { viewModel.updatePrice(it) },
                    modifier = Modifier.weight(1f)
                )
            }
            
            // Description Field (Optional)
            MultilineTextField(
                value = uiState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = stringResource(R.string.description_optional),
                modifier = Modifier.fillMaxWidth()
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
                text = stringResource(R.string.save_material),
                onClick = { viewModel.saveMaterial(projectId) },
                enabled = uiState.isFormValid,
                isLoading = uiState.isSaving
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddMaterialScreenPreview() {
    ConstructionMaterialTrackTheme {
        AddMaterialScreen(
            viewModel = viewModel(),
            projectId = "1"
        )
    }
}
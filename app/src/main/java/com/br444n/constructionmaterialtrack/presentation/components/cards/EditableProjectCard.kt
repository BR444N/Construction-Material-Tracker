package com.br444n.constructionmaterialtrack.presentation.components.cards

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.stringResource
import com.br444n.constructionmaterialtrack.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.presentation.components.images.SecureImagePicker
import com.br444n.constructionmaterialtrack.presentation.components.forms.SecureTextField
import com.br444n.constructionmaterialtrack.presentation.components.forms.SecureTextFieldConfig
import com.br444n.constructionmaterialtrack.presentation.hooks.ValidationType


@Composable
fun EditableProjectCard(
    projectName: String,
    projectDescription: String,
    selectedImageUri: Uri?,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onImageSelected: (Uri?) -> Unit,
    modifier: Modifier = Modifier,
    onValidationError: (String) -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Transparent
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
        ) {
            // Secure Image Picker
            SecureImagePicker(
                selectedImageUri = selectedImageUri,
                onImageSelected = onImageSelected,
                onValidationError = onValidationError,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Secure Project Name Field
            SecureTextField(
                value = projectName,
                onValueChange = onNameChange,
                label = stringResource(R.string.project_name),
                modifier = Modifier.fillMaxWidth(),
                config = SecureTextFieldConfig(
                    validationType = ValidationType.PROJECT_NAME
                )
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Secure Project Description Field
            SecureTextField(
                value = projectDescription,
                onValueChange = onDescriptionChange,
                label = stringResource(R.string.description_optional),
                modifier = Modifier.fillMaxWidth(),
                config = SecureTextFieldConfig(
                    validationType = ValidationType.DESCRIPTION,
                    singleLine = false,
                    maxLines = 5,
                    minLines = 3,
                    isRequired = false
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditableProjectCard(modifier: Modifier = Modifier) {
    EditableProjectCard(
        projectName = "New Project",
        projectDescription = "This is a sample project description.",
        selectedImageUri = null,
        onNameChange = {},
        onDescriptionChange = {},
        onImageSelected = {},
        onValidationError = {},
        modifier = modifier.padding(16.dp)
    )
}
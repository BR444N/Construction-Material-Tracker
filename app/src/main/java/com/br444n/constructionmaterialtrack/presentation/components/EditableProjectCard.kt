package com.br444n.constructionmaterialtrack.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EditableProjectCard(
    projectName: String,
    projectDescription: String,
    selectedImageUri: Uri?,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onImageSelected: (Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Image Picker
            ImagePicker(
                selectedImageUri = selectedImageUri,
                onImageSelected = onImageSelected,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Project Name Field
            CustomTextField(
                value = projectName,
                onValueChange = onNameChange,
                label = "Project Name",
                modifier = Modifier.fillMaxWidth(),
                isError = projectName.isBlank()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Project Description Field
            MultilineTextField(
                value = projectDescription,
                onValueChange = onDescriptionChange,
                label = "Description (Optional)",
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
        }
    }
}
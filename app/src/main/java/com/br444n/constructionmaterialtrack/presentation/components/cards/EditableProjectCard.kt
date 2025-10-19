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
import com.br444n.constructionmaterialtrack.presentation.components.images.ImagePicker
import com.br444n.constructionmaterialtrack.presentation.components.forms.CustomTextField
import com.br444n.constructionmaterialtrack.presentation.components.forms.MultilineTextField


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
            // Image Picker
            ImagePicker(
                selectedImageUri = selectedImageUri,
                onImageSelected = onImageSelected,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Project Name Field
            CustomTextField(
                value = projectName,
                onValueChange = onNameChange,
                label = stringResource(R.string.project_name),
                modifier = Modifier.fillMaxWidth(),
                isError = projectName.isBlank()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Project Description Field
            MultilineTextField(
                value = projectDescription,
                onValueChange = onDescriptionChange,
                label = stringResource(R.string.description_optional),
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
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
        modifier = modifier.padding(16.dp)
    )
}
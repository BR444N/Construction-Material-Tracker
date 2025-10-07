package com.br444n.constructionmaterialtrack.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

@Composable
fun MultilineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    minLines: Int = 3,
    maxLines: Int = 4,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        minLines = minLines,
        maxLines = maxLines,
        isError = isError
    )
}

@Preview(showBackground = true)
@Composable
private fun MultilineTextFieldPreview() {
    ConstructionMaterialTrackTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MultilineTextField(
                value = "",
                onValueChange = {},
                label = "Description (Optional)",
                modifier = Modifier.fillMaxWidth()
            )
            
            MultilineTextField(
                value = "This is a sample description that spans multiple lines to show how the component works with longer text content.",
                onValueChange = {},
                label = "Description",
                modifier = Modifier.fillMaxWidth()
            )
            
            MultilineTextField(
                value = "",
                onValueChange = {},
                label = "Required Description",
                modifier = Modifier.fillMaxWidth(),
                isError = true
            )
        }
    }
}
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
fun SaveButton(
    modifier: Modifier = Modifier,
    text: String = "Save",
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        enabled = enabled && !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(
                text = text,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SaveButtonPreview() {
    ConstructionMaterialTrackTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SaveButton(
                text = "Save Material",
                onClick = {},
                enabled = true,
                isLoading = false
            )
            
            SaveButton(
                text = "Save Material",
                onClick = {},
                enabled = false,
                isLoading = false
            )
            
            SaveButton(
                text = "Save Material",
                onClick = {},
                enabled = true,
                isLoading = true
            )
        }
    }
}
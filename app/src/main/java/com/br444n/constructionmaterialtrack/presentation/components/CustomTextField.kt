package com.br444n.constructionmaterialtrack.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

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
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = isError,
        minLines = minLines,
        maxLines = maxLines


    )
}

@Preview(showBackground = true)
@Composable
private fun CustomTextFieldPreview() {
    ConstructionMaterialTrackTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CustomTextField(
                value = "",
                onValueChange = {},
                label = "Material Name",
                modifier = Modifier.fillMaxWidth()
            )
            
            CustomTextField(
                value = "Sample text",
                onValueChange = {},
                label = "Material Name",
                modifier = Modifier.fillMaxWidth()
            )
            
            CustomTextField(
                value = "",
                onValueChange = {},
                label = "Required Field",
                modifier = Modifier.fillMaxWidth(),
                isError = true
            )
        }
    }
}
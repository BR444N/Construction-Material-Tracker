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
fun NumberTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Number
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = isError
    )
}

@Preview(showBackground = true)
@Composable
private fun NumberTextFieldPreview() {
    ConstructionMaterialTrackTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NumberTextField(
                    value = "",
                    onValueChange = {},
                    label = "Quantity",
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Number
                )
                
                NumberTextField(
                    value = "",
                    onValueChange = {},
                    label = "Price ($)",
                    modifier = Modifier.weight(1f),
                    keyboardType = KeyboardType.Decimal
                )
            }
            
            NumberTextField(
                value = "123",
                onValueChange = {},
                label = "Quantity",
                modifier = Modifier.fillMaxWidth()
            )
            
            NumberTextField(
                value = "",
                onValueChange = {},
                label = "Required Number",
                modifier = Modifier.fillMaxWidth(),
                isError = true
            )
        }
    }
}
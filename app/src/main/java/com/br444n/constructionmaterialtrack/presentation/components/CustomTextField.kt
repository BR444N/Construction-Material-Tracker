package com.br444n.constructionmaterialtrack.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.ui.theme.BlueDark
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme
import com.br444n.constructionmaterialtrack.ui.theme.Red
import com.br444n.constructionmaterialtrack.ui.theme.TextSecondary


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
    maxLines: Int = 1,
    leadingIcon: ImageVector? = Icons.Default.Badge,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null
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
        maxLines = maxLines,
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = if (isError) Red else BlueDark
                )
            }
        },
        trailingIcon = trailingIcon?.let {
            {
                IconButton(
                    onClick = { onTrailingIconClick?.invoke() }
                ) {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (isError) Red else BlueDark
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            // Bordes
            focusedBorderColor = BlueDark,
            unfocusedBorderColor = BluePrimary,
            errorBorderColor = Red,
            disabledBorderColor = BluePrimary.copy(0.3f),

            // Labels
            focusedLabelColor = BlueDark,
            unfocusedLabelColor = BluePrimary,
            errorLabelColor = Red,
            disabledLabelColor = BluePrimary,

            // Texto
            focusedTextColor = BlueDark,
            unfocusedTextColor = BlueDark,
            errorTextColor = Red,
            disabledTextColor = TextSecondary.copy(alpha = 0.3f),

            // Cursor
            cursorColor = BlueDark,
            errorCursorColor = Red,

            // Fondo
            focusedContainerColor = BluePrimary.copy(0.1f),
            unfocusedContainerColor = BluePrimary.copy(0.05f),
            errorContainerColor = Red.copy(0.1f),
            disabledContainerColor = TextSecondary.copy(alpha = 0.05f),

            // Placeholder
            focusedPlaceholderColor = TextSecondary.copy(alpha = 0.6f),
            unfocusedPlaceholderColor = TextSecondary.copy(alpha = 0.6f),
            errorPlaceholderColor = Red.copy(alpha = 0.6f),
            disabledPlaceholderColor = TextSecondary.copy(alpha = 0.3f),

            // Iconos de soporte (si los usas)
            focusedSupportingTextColor = TextSecondary,
            unfocusedSupportingTextColor = TextSecondary,
            errorSupportingTextColor = Red,
            disabledSupportingTextColor = TextSecondary.copy(alpha = 0.3f)
        )
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
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Badge
            )
            
            CustomTextField(
                value = "Sample text",
                onValueChange = {},
                label = "Material Name",
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Badge
            )
            
            CustomTextField(
                value = "",
                onValueChange = {},
                label = "Project Description",
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Description,
                singleLine = false,
                maxLines = 3
            )
            
            CustomTextField(
                value = "",
                onValueChange = {},
                label = "Required Field",
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Default.Badge,
                isError = true
            )
        }
    }
}
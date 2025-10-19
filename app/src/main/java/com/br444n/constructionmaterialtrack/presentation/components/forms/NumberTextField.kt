package com.br444n.constructionmaterialtrack.presentation.components.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ExposurePlus1
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.presentation.model.NumberTextFieldConfig
import com.br444n.constructionmaterialtrack.ui.theme.BlueDark
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme
import com.br444n.constructionmaterialtrack.ui.theme.Red
import com.br444n.constructionmaterialtrack.ui.theme.TextSecondary

@Composable
fun NumberTextField(
    config: NumberTextFieldConfig,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onTrailingIconClick: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(config.label) },
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = config.keyboardType),
        isError = config.isError,
        leadingIcon = config.leadingIcon?.let { icon ->
            { LeadingIcon(icon = icon, isError = config.isError) }
        },
        trailingIcon = config.trailingIcon?.let { icon ->
            { TrailingIcon(icon = icon, isError = config.isError, onClick = onTrailingIconClick) }
        },
        colors = getTextFieldColors()
    )
}

// Helper composables to reduce complexity
@Composable
private fun LeadingIcon(icon: ImageVector, isError: Boolean) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isError) Red else BlueDark
    )
}

@Composable
private fun TrailingIcon(
    icon: ImageVector,
    isError: Boolean,
    onClick: (() -> Unit)?
) {
    IconButton(
        onClick = { onClick?.invoke() }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isError) Red else BlueDark
        )
    }
}

@Composable
private fun getTextFieldColors() = OutlinedTextFieldDefaults.colors(
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

    // Iconos de soporte
    focusedSupportingTextColor = TextSecondary,
    unfocusedSupportingTextColor = TextSecondary,
    errorSupportingTextColor = Red,
    disabledSupportingTextColor = TextSecondary.copy(alpha = 0.3f)
)



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
                    config = NumberTextFieldConfig.quantity(
                        label = "Quantity",
                        leadingIcon = Icons.Default.ExposurePlus1
                    ),
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.weight(1f)
                )
                
                NumberTextField(
                    config = NumberTextFieldConfig.currency(
                        label = "Price ($)",
                        leadingIcon = Icons.Default.AttachMoney
                    ),
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.weight(1f)
                )
            }
            
            NumberTextField(
                config = NumberTextFieldConfig.quantity(
                    label = "Quantity",
                    leadingIcon = Icons.Default.ExposurePlus1
                ),
                value = "123",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth()
            )
            
            NumberTextField(
                config = NumberTextFieldConfig.currency(
                    label = "Required Price",
                    leadingIcon = Icons.Default.AttachMoney,
                    isError = true
                ),
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
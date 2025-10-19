package com.br444n.constructionmaterialtrack.presentation.model

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType

data class NumberTextFieldConfig(
    val label: String,
    val keyboardType: KeyboardType = KeyboardType.Number,
    val leadingIcon: ImageVector? = null,
    val trailingIcon: ImageVector? = null,
    val isError: Boolean = false
) {
    companion object {
        fun quantity(
            label: String = "Quantity",
            leadingIcon: ImageVector? = null,
            isError: Boolean = false
        ) = NumberTextFieldConfig(
            label = label,
            keyboardType = KeyboardType.Number,
            leadingIcon = leadingIcon,
            isError = isError
        )
        
        fun currency(
            label: String = "Price ($)",
            leadingIcon: ImageVector? = null,
            isError: Boolean = false
        ) = NumberTextFieldConfig(
            label = label,
            keyboardType = KeyboardType.Decimal,
            leadingIcon = leadingIcon,
            isError = isError
        )
    }
}
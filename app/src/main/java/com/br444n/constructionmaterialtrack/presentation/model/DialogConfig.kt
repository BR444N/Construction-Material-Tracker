package com.br444n.constructionmaterialtrack.presentation.model

import androidx.compose.ui.graphics.vector.ImageVector

data class ConfirmationDialogConfig(
    val title: String,
    val message: String,
    val icon: ImageVector,
    val confirmText: String,
    val dismissText: String,
    val isDestructive: Boolean = false
) {
    companion object {
        fun destructive(
            title: String,
            message: String,
            icon: ImageVector,
            confirmText: String = "Delete",
            dismissText: String = "Cancel"
        ) = ConfirmationDialogConfig(
            title = title,
            message = message,
            icon = icon,
            confirmText = confirmText,
            dismissText = dismissText,
            isDestructive = true
        )
    }
}
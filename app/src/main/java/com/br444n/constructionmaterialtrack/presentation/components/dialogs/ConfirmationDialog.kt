package com.br444n.constructionmaterialtrack.presentation.components.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.br444n.constructionmaterialtrack.presentation.model.ConfirmationDialogConfig
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.Red
import com.br444n.constructionmaterialtrack.ui.theme.RedLight

@Composable
fun ConfirmationDialog(
    config: ConfirmationDialogConfig,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            DialogIcon(config.icon, config.isDestructive)
        },
        title = {
            DialogTitle(config.title)
        },
        text = {
            DialogMessage(config.message)
        },
        confirmButton = {
            ConfirmButton(
                text = config.confirmText,
                isDestructive = config.isDestructive,
                onClick = onConfirm
            )
        },
        dismissButton = {
            DismissButton(
                text = config.dismissText,
                onClick = onDismiss
            )
        },
        containerColor = BluePrimary
    )
}

// Helper composables to reduce complexity
@Composable
private fun DialogIcon(icon: ImageVector, isDestructive: Boolean) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isDestructive) RedLight else Red
    )
}

@Composable
private fun DialogTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun DialogMessage(message: String) {
    Text(
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Normal,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun ConfirmButton(
    text: String,
    isDestructive: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = if (isDestructive) {
            ButtonDefaults.buttonColors(containerColor = Red)
        } else {
            ButtonDefaults.buttonColors(containerColor = RedLight)
        }
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DismissButton(
    text: String,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}



@Preview(showBackground = true)
@Composable
fun ConfirmationDialogPreview() {
    ConfirmationDialog(
        config = ConfirmationDialogConfig.destructive(
            title = "Delete Item",
            message = "Are you sure you want to delete this item? This action cannot be undone.",
            icon = Icons.Default.Delete,
            confirmText = "Delete",
            dismissText = "Cancel"
        ),
        onConfirm = {},
        onDismiss = {}
    )
}
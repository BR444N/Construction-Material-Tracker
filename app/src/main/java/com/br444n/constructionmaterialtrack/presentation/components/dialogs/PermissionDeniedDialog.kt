package com.br444n.constructionmaterialtrack.presentation.components.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.br444n.constructionmaterialtrack.R
import com.br444n.constructionmaterialtrack.core.utils.PermissionUtils

/**
 * Dialog shown when permission is denied
 */
@Composable
fun PermissionDeniedDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.permission_required)) },
        text = { 
            Text(PermissionUtils.getPermissionExplanation()) 
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

package com.br444n.constructionmaterialtrack.presentation.components.states

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.ui.theme.Black
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme
import com.br444n.constructionmaterialtrack.ui.theme.Red
import com.br444n.constructionmaterialtrack.ui.theme.TextSecondary

@Composable
fun ErrorContent(
    errorMessage: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Error,
    title: String = "Error occurred"
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Red
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = Red
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, BluePrimary)
            ) {
                Text("Dismiss", color = Black)
            }
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(BluePrimary) ) {
                Text("Retry", color = Black)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorContentPreview() {
    ConstructionMaterialTrackTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            ErrorContent(
                errorMessage = "Failed to load project data. Please check your connection and try again.",
                onRetry = {},
                onDismiss = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
            
            ErrorContent(
                errorMessage = "Network timeout occurred.",
                onRetry = {},
                onDismiss = {},
                title = "Connection Error",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    }
}
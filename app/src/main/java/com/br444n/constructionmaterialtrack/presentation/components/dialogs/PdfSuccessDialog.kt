package com.br444n.constructionmaterialtrack.presentation.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.R
import com.br444n.constructionmaterialtrack.ui.theme.BlueDark
import com.br444n.constructionmaterialtrack.ui.theme.BlueLight
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme
import com.br444n.constructionmaterialtrack.ui.theme.SurfaceLight

@Composable
fun PdfSuccessDialog(
    fileName: String,
    onOpenFile: () -> Unit,
    onShareFile: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = null,
                tint = BluePrimary,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = stringResource(R.string.pdf_generated_successfully),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.pdf_saved_to_downloads),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = fileName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = BlueLight,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onShareFile,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = SurfaceLight
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(R.string.share), color = SurfaceLight)
                }
                
                Button(
                    onClick = onOpenFile,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(BlueDark)
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        stringResource(R.string.open),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        },
        containerColor = BlueDark
    )
}

@Preview(showBackground = true)
@Composable
private fun PdfSuccessDialogPreview() {
    ConstructionMaterialTrackTheme {
        PdfSuccessDialog(
            fileName = "Project_Report_2024.pdf",
            onOpenFile = { /* Preview action */ },
            onShareFile = { /* Preview action */ },
            onDismiss = { /* Preview action */ }
        )
    }
}

@Preview(showBackground = true, name = "Long Filename")
@Composable
private fun PdfSuccessDialogLongFilenamePreview() {
    ConstructionMaterialTrackTheme {
        PdfSuccessDialog(
            fileName = "Very_Long_Project_Report_With_Many_Details_2024.pdf",
            onOpenFile = { /* Preview action */ },
            onShareFile = { /* Preview action */ },
            onDismiss = { /* Preview action */ }
        )
    }
}
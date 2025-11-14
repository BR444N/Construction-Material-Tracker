package com.br444n.constructionmaterialtrack.presentation.components.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.R
import com.br444n.constructionmaterialtrack.domain.model.Project
import com.br444n.constructionmaterialtrack.presentation.screens.pdf_preview.PdfPreviewUiState
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfPreviewTopAppBar(
    uiState: PdfPreviewUiState,
    onBackClick: () -> Unit,
    onGeneratePdf: () -> Unit,
    onSharePdf: (File) -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.pdf_preview),
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.return_tooltip),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            // Share button (only visible when PDF is generated)
            val pdfFile = uiState.generatedPdfFile
            if (uiState.pdfGenerated && pdfFile != null) {
                IconButton(onClick = { onSharePdf(pdfFile) }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(R.string.share_pdf),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            // Download button
            IconButton(
                onClick = onGeneratePdf,
                enabled = !uiState.isGeneratingPdf && uiState.project != null
            ) {
                if (uiState.isGeneratingPdf) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = stringResource(R.string.generate_pdf),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BluePrimary
        )
    )
}

@Preview(showBackground = true, name = "Default State")
@Composable
private fun PdfPreviewTopAppBarPreview() {
    ConstructionMaterialTrackTheme {
        Column {
            PdfPreviewTopAppBar(
                uiState = PdfPreviewUiState(
                    project = Project(
                        id = "1",
                        name = stringResource(R.string.project_name),
                        description = stringResource(R.string.project_description)
                    ),
                    isLoading = false,
                    isGeneratingPdf = false,
                    pdfGenerated = false
                ),
                onBackClick = {},
                onGeneratePdf = {},
                onSharePdf = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Generating PDF")
@Composable
private fun PdfPreviewTopAppBarGeneratingPreview() {
    ConstructionMaterialTrackTheme {
        Column {
            PdfPreviewTopAppBar(
                uiState = PdfPreviewUiState(
                    project = Project(
                        id = "1",
                        name = "Sample Project",
                        description = "Sample description"
                    ),
                    isLoading = false,
                    isGeneratingPdf = true,
                    pdfGenerated = false
                ),
                onBackClick = {},
                onGeneratePdf = {},
                onSharePdf = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "PDF Generated - With Share Button")
@Composable
private fun PdfPreviewTopAppBarWithSharePreview() {
    ConstructionMaterialTrackTheme {
        Column {
            PdfPreviewTopAppBar(
                uiState = PdfPreviewUiState(
                    project = Project(
                        id = "1",
                        name = "Sample Project",
                        description = "Sample description"
                    ),
                    isLoading = false,
                    isGeneratingPdf = false,
                    pdfGenerated = true,
                    generatedPdfFile = File("Sample_Project_20231115.pdf")
                ),
                onBackClick = {},
                onGeneratePdf = {},
                onSharePdf = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "No Project Loaded")
@Composable
private fun PdfPreviewTopAppBarNoProjectPreview() {
    ConstructionMaterialTrackTheme {
        Column {
            PdfPreviewTopAppBar(
                uiState = PdfPreviewUiState(
                    project = null,
                    isLoading = false,
                    isGeneratingPdf = false,
                    pdfGenerated = false
                ),
                onBackClick = {},
                onGeneratePdf = {},
                onSharePdf = {}
            )
        }
    }
}

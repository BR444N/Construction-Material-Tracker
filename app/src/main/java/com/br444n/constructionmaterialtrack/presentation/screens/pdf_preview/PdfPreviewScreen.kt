package com.br444n.constructionmaterialtrack.presentation.screens.pdf_preview

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br444n.constructionmaterialtrack.R
import com.br444n.constructionmaterialtrack.presentation.components.cards.PdfProjectHeader
import com.br444n.constructionmaterialtrack.presentation.components.dialogs.PdfSuccessDialog
import com.br444n.constructionmaterialtrack.presentation.components.lists.PdfMaterialItem
import com.br444n.constructionmaterialtrack.presentation.components.navigation.PdfPreviewTopAppBar
import com.br444n.constructionmaterialtrack.presentation.components.states.ErrorContent
import com.br444n.constructionmaterialtrack.presentation.components.states.LoadingIndicator
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfPreviewScreen(
    viewModel: PdfPreviewViewModel,
    projectId: String,
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Load project data when screen opens
    LaunchedEffect(projectId) {
        viewModel.loadProjectData(projectId)
    }
    
    // Handle PDF generated - Show snackbar
    LaunchedEffect(uiState.pdfGenerated) {
        if (uiState.pdfGenerated && uiState.generatedPdfFile != null) {
            snackbarHostState.showSnackbar(
                message = "PDF saved to Downloads: ${uiState.generatedPdfFile!!.name}",
                duration = SnackbarDuration.Short
            )
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            PdfPreviewTopAppBar(
                uiState = uiState,
                onBackClick = onBackClick,
                onGeneratePdf = { viewModel.generatePdf() },
                onSharePdf = { file -> sharePdfFile(context, file) }
            )
        }
    ) { paddingValues ->
        PdfPreviewContent(
            uiState = uiState,
            paddingValues = paddingValues,
            projectId = projectId,
            viewModel = viewModel
        )
        
        // Success Dialog
        PdfSuccessDialogHandler(
            uiState = uiState,
            context = context,
            viewModel = viewModel
        )
    }
}



// Helper functions for PDF actions
private fun openPdfFile(context: android.content.Context, file: java.io.File) {
    try {
        println("DEBUG: Trying to open file: ${file.absolutePath}")
        println("DEBUG: File exists: ${file.exists()}")
        
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        println("DEBUG: Generated URI: $uri")
        
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        
        // Check if there's an app that can handle PDF files
        if (intent.resolveActivity(context.packageManager) != null) {
            println("DEBUG: Found app to handle PDF, starting activity")
            context.startActivity(intent)
        } else {
            println("DEBUG: No app found to handle PDF, opening Downloads folder")
            // Fallback: Open Downloads folder
            openDownloadsFolder(context)
        }
    } catch (e: Exception) {
        println("DEBUG: Error opening PDF: ${e.message}")
        e.printStackTrace()
        // Fallback: Open Downloads folder
        openDownloadsFolder(context)
    }
}

private fun sharePdfFile(context: android.content.Context, file: java.io.File) {
    try {
        println("DEBUG: Trying to share file: ${file.absolutePath}")
        println("DEBUG: File exists: ${file.exists()}")
        
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        println("DEBUG: Generated URI for sharing: $uri")
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Project Report - ${file.nameWithoutExtension}")
            putExtra(Intent.EXTRA_TEXT, "Please find the attached project report.")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        
        // Use Android's native ShareSheet
        val chooserIntent = Intent.createChooser(shareIntent, "Share PDF Report")
        chooserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        
        println("DEBUG: Starting share activity")
        context.startActivity(chooserIntent)
        
    } catch (e: Exception) {
        println("DEBUG: Error sharing PDF: ${e.message}")
        e.printStackTrace()
        // Could show a toast or snackbar with error message
    }
}

private fun openDownloadsFolder(context: android.content.Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(
                "content://com.android.external storage.documents/document/primary%3ADownload".toUri(),
                "resource/folder"
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        // Final fallback: Open file manager
        try {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            // Silently fail - no action needed
        }
    }
}

@Composable
private fun PdfPreviewContent(
    uiState: PdfPreviewUiState,
    paddingValues: PaddingValues,
    projectId: String,
    viewModel: PdfPreviewViewModel
) {
    when {
        uiState.isLoading -> {
            LoadingIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                text = stringResource(R.string.loading_pdf_preview)
            )
        }
        uiState.errorMessage != null -> {
            ErrorContent(
                errorMessage = uiState.errorMessage,
                onRetry = { viewModel.loadProjectData(projectId) },
                onDismiss = { viewModel.clearError() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        }
        else -> {
            PdfPreviewCard(
                uiState = uiState,
                paddingValues = paddingValues
            )
        }
    }
}

@Composable
private fun PdfPreviewCard(
    uiState: PdfPreviewUiState,
    paddingValues: PaddingValues
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            uiState.project?.let { project ->
                item {
                    PdfProjectHeader(project = project)
                }
                
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(R.string.materials),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                items(uiState.materials) { material ->
                    PdfMaterialItem(material = material)
                }
                
                if (uiState.materials.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.no_materials_added_yet),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PdfSuccessDialogHandler(
    uiState: PdfPreviewUiState,
    context: android.content.Context,
    viewModel: PdfPreviewViewModel
) {
    val pdfFile = uiState.generatedPdfFile
    if (!uiState.pdfGenerated || pdfFile == null) return
    
    PdfSuccessDialog(
        fileName = pdfFile.name,
        onOpenFile = {
            openPdfFile(context, pdfFile)
            viewModel.clearPdfGenerated()
        },
        onShareFile = {
            sharePdfFile(context, pdfFile)
            viewModel.clearPdfGenerated()
        },
        onDismiss = viewModel::clearPdfGenerated
    )
}

@Preview(showBackground = true)
@Composable
private fun PdfPreviewScreenPreview() {
    ConstructionMaterialTrackTheme {
        PdfPreviewScreen(
            viewModel = viewModel(),
            projectId = "1"
        )
    }
}
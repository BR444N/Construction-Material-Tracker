package com.br444n.constructionmaterialtrack.presentation.screens.pdf_preview

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.br444n.constructionmaterialtrack.R
import com.br444n.constructionmaterialtrack.domain.model.Material
import com.br444n.constructionmaterialtrack.domain.model.Project
import com.br444n.constructionmaterialtrack.presentation.components.states.ErrorContent
import com.br444n.constructionmaterialtrack.presentation.components.states.LoadingIndicator
import com.br444n.constructionmaterialtrack.ui.theme.BlueDark
import com.br444n.constructionmaterialtrack.ui.theme.BlueLight
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme
import com.br444n.constructionmaterialtrack.ui.theme.SurfaceLight
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfPreviewScreen(
    viewModel: PdfPreviewViewModel,
    projectId: String,
    onBackClick: () -> Unit = {},
    onPdfGenerated: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val density = LocalDensity.current
    
    // Load project data when screen opens
    LaunchedEffect(projectId) {
        viewModel.loadProjectData(projectId)
    }
    
    // Handle PDF generated - Show snackbar with share option
    LaunchedEffect(uiState.pdfGenerated) {
        if (uiState.pdfGenerated && uiState.generatedPdfFile != null) {
            // First show success message
            val result = snackbarHostState.showSnackbar(
                message = "PDF saved to Downloads: ${uiState.generatedPdfFile!!.name}",
                actionLabel = "Actions",
                duration = SnackbarDuration.Long
            )
            
            if (result == SnackbarResult.ActionPerformed) {
                // Show action dialog when user taps "Actions"
                // This will be handled by the dialog below
            }
        }
    }
    
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.pdf_preview),
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    Box {
                        TooltipBox(
                            positionProvider = object : PopupPositionProvider {
                                override fun calculatePosition(
                                    anchorBounds: IntRect,
                                    windowSize: IntSize,
                                    layoutDirection: LayoutDirection,
                                    popupContentSize: IntSize
                                ): IntOffset {
                                    val spacingPx = with(density) { 4.dp.toPx().toInt() }
                                    val x = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
                                    val y = anchorBounds.bottom + spacingPx
                                    val adjustedX = x.coerceIn(0, windowSize.width - popupContentSize.width)
                                    val adjustedY = y.coerceAtMost(windowSize.height - popupContentSize.height)
                                    return IntOffset(adjustedX, adjustedY)
                                }
                            },
                            tooltip = {
                                PlainTooltip {
                                    Text(stringResource(R.string.return_tooltip))
                                }
                            },
                            state = remember { TooltipState() }
                        ) {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.return_tooltip),
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                },
                actions = {
                    // Share button (only visible when PDF is generated)
                    if (uiState.pdfGenerated && uiState.generatedPdfFile != null) {
                        Box {
                            TooltipBox(
                                positionProvider = object : PopupPositionProvider {
                                    override fun calculatePosition(
                                        anchorBounds: IntRect,
                                        windowSize: IntSize,
                                        layoutDirection: LayoutDirection,
                                        popupContentSize: IntSize
                                    ): IntOffset {
                                        val spacingPx = with(density) { 4.dp.toPx().toInt() }
                                        val x = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
                                        val y = anchorBounds.bottom + spacingPx
                                        val adjustedX = x.coerceIn(0, windowSize.width - popupContentSize.width)
                                        val adjustedY = y.coerceAtMost(windowSize.height - popupContentSize.height)
                                        return IntOffset(adjustedX, adjustedY)
                                    }
                                },
                                tooltip = {
                                    PlainTooltip {
                                        Text(stringResource(R.string.share_pdf))
                                    }
                                },
                                state = remember { TooltipState() }
                            ) {
                                IconButton(
                                    onClick = {
                                        sharePdfFile(context, uiState.generatedPdfFile!!)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = stringResource(R.string.share_pdf),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                    
                    // Download button
                    Box {
                        TooltipBox(
                            positionProvider = object : PopupPositionProvider {
                                override fun calculatePosition(
                                    anchorBounds: IntRect,
                                    windowSize: IntSize,
                                    layoutDirection: LayoutDirection,
                                    popupContentSize: IntSize
                                ): IntOffset {
                                    val spacingPx = with(density) { 4.dp.toPx().toInt() }
                                    val x = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
                                    val y = anchorBounds.bottom + spacingPx
                                    val adjustedX = x.coerceIn(0, windowSize.width - popupContentSize.width)
                                    val adjustedY = y.coerceAtMost(windowSize.height - popupContentSize.height)
                                    return IntOffset(adjustedX, adjustedY)
                                }
                            },
                            tooltip = {
                                PlainTooltip {
                                    Text(stringResource(R.string.generate_pdf))
                                }
                            },
                            state = remember { TooltipState() }
                        ) {
                            IconButton(
                                onClick = { viewModel.generatePdf() },
                                enabled = !uiState.isGeneratingPdf && uiState.project != null
                            ) {
                                if (uiState.isGeneratingPdf) {
                                    LottieLoadingIcon()
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = stringResource(R.string.generate_pdf),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BluePrimary
                )
            )
        },
        floatingActionButton = {
            // Show share FAB only when PDF is generated
            if (uiState.pdfGenerated && uiState.generatedPdfFile != null) {
                TooltipBox(
                    positionProvider = object : PopupPositionProvider {
                        override fun calculatePosition(
                            anchorBounds: IntRect,
                            windowSize: IntSize,
                            layoutDirection: LayoutDirection,
                            popupContentSize: IntSize
                        ): IntOffset {
                            val spacingPx = with(density) { 8.dp.toPx().toInt() }
                            val x = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
                            val y = anchorBounds.top - popupContentSize.height - spacingPx
                            val adjustedX = x.coerceIn(0, windowSize.width - popupContentSize.width)
                            val adjustedY = y.coerceAtLeast(0)
                            return IntOffset(adjustedX, adjustedY)
                        }
                    },
                    tooltip = {
                        PlainTooltip {
                            Text(stringResource(R.string.share_pdf))
                        }
                    },
                    state = remember { TooltipState() }
                ) {
                    FloatingActionButton(
                        onClick = {
                            sharePdfFile(context, uiState.generatedPdfFile!!)
                        },
                        containerColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.share_pdf),
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
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
                    errorMessage = uiState.errorMessage ?: "",
                    onRetry = { viewModel.loadProjectData(projectId) },
                    onDismiss = { viewModel.clearError() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            else -> {
                // PDF Preview Content
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
        }
        
        // Success Dialog with Open and Share options
        if (uiState.pdfGenerated && uiState.generatedPdfFile != null) {
            PdfSuccessDialog(
                fileName = uiState.generatedPdfFile!!.name,
                onOpenFile = {
                    openPdfFile(context, uiState.generatedPdfFile!!)
                    viewModel.clearPdfGenerated()
                },
                onShareFile = {
                    sharePdfFile(context, uiState.generatedPdfFile!!)
                    viewModel.clearPdfGenerated()
                },
                onDismiss = {
                    viewModel.clearPdfGenerated()
                }
            )
        }
    }
}

@Composable
private fun PdfSuccessDialog(
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
                    Text(stringResource(R.string.open),
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
    } catch (e: Exception) {
        // Final fallback: Open file manager
        try {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

@Composable
private fun PdfProjectHeader(
    project: Project,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Project Image (Circular)
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(3.dp, BlueLight.copy(0.4f), CircleShape)
                .background(BlueLight.copy(0.3f)),
            contentAlignment = Alignment.Center
        ) {
            when {
                project.imageUri != null -> {
                    AsyncImage(
                        model = project.imageUri.toUri(),
                        contentDescription = "Project Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                project.imageRes != null -> {
                    Image(
                        painter = painterResource(id = project.imageRes),
                        contentDescription = "Project Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    Icon(
                        painter = painterResource(id = R.drawable.pose_def_project),
                        contentDescription = "Default Project Image",
                        modifier = Modifier.size(60.dp),
                        tint = Unspecified
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Project Name (Bold, Centered)
        Text(
            text = project.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Project Description (Normal, Centered)
        if (project.description.isNotBlank()) {
            Text(
                text = project.description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PdfMaterialItem(
    material: Material,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Checkbox
        Icon(
            imageVector = if (material.isPurchased) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
            contentDescription = if (material.isPurchased) "Purchased" else "Not purchased",
            tint = if (material.isPurchased) Color(0xFF4CAF50) else Color.Gray,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Material Details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = material.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = "Qty: ${material.quantity}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "$${material.price}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
            
            if (material.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = material.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(
    errorMessage: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Download,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.error_occurred),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.dismiss))
            }
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
private fun LottieLoadingIcon(
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.spinner)
    )
    
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = modifier.size(20.dp)
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
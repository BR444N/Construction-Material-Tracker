package com.br444n.constructionmaterialtrack.presentation.components.images

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.br444n.constructionmaterialtrack.R
import com.br444n.constructionmaterialtrack.core.security.InputValidator
import com.br444n.constructionmaterialtrack.core.utils.PermissionUtils
import com.br444n.constructionmaterialtrack.presentation.components.dialogs.ImageSourceDialog
import com.br444n.constructionmaterialtrack.presentation.components.dialogs.PermissionDeniedDialog
import com.br444n.constructionmaterialtrack.ui.theme.BlueDark
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme
import com.br444n.constructionmaterialtrack.ui.theme.Red
import androidx.core.net.toUri


/**
 * Secure ImagePicker with validation and security checks
 */
@Composable
fun SecureImagePicker(
    selectedImageUri: Uri?,
    onImageSelected: (Uri?) -> Unit,
    modifier: Modifier = Modifier,
    onValidationError: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var validationState by remember { mutableStateOf(ImageValidationState.VALID) }
    var validationMessage by remember { mutableStateOf("") }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    
    // Validate URI when it changes
    LaunchedEffect(selectedImageUri) {
        val strings = InputValidator.createStrings(context)
        val validationResult = InputValidator.validateImageUri(selectedImageUri, context, strings)
        if (!validationResult.isValid) {
            validationState = ImageValidationState.INVALID
            validationMessage = validationResult.errorMessage
            onValidationError(validationResult.errorMessage)
        } else {
            validationState = ImageValidationState.VALID
            validationMessage = ""
        }
    }
    
    // Gallery picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        handleSecureImageSelection(context, uri, onImageSelected, onValidationError)
    }
    
    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && tempCameraUri != null) {
            handleSecureImageSelection(context, tempCameraUri, onImageSelected, onValidationError)
        }
    }
    
    // Permission launcher for different Android versions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val hasAnyPermission = permissions.values.any { it }
        if (hasAnyPermission || PermissionUtils.hasImagePermissions(context)) {
            showImageSourceDialog = true
        } else {
            showPermissionDialog = true
        }
    }
    
    // Function to show image source options
    fun showImageSourceOptions() {
        if (PermissionUtils.hasImagePermissions(context)) {
            showImageSourceDialog = true
        } else {
            permissionLauncher.launch(PermissionUtils.getImagePermissions())
        }
    }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.project_image),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(BluePrimary.copy(0.05f))
                .border(
                    width = 2.dp,
                    shape = CircleShape,
                    color = when (validationState) {
                        ImageValidationState.INVALID -> Red
                        ImageValidationState.WARNING -> MaterialTheme.colorScheme.tertiary
                        ImageValidationState.VALID -> BlueDark
                    }
                )
                .clickable { showImageSourceOptions() },
            contentAlignment = Alignment.Center
        ) {
            when {
                selectedImageUri != null && validationState == ImageValidationState.VALID -> {
                    SecureImageContent(
                        uri = selectedImageUri,
                        onError = { 
                            validationState = ImageValidationState.INVALID
                            validationMessage = "Failed to load image"
                            onValidationError("Failed to load image")
                        }
                    )
                }
                validationState == ImageValidationState.INVALID -> {
                    ErrorPlaceholderContent()
                }
                else -> {
                    DefaultPlaceholderContent()
                }
            }
        }
        
        // Show validation message if there's an error
        if (validationMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = validationMessage,
                style = MaterialTheme.typography.bodySmall,
                color = when (validationState) {
                    ImageValidationState.INVALID -> Red
                    ImageValidationState.WARNING -> MaterialTheme.colorScheme.tertiary
                    ImageValidationState.VALID -> MaterialTheme.colorScheme.onSurface
                },
                fontWeight = FontWeight.Medium
            )
        }
    }
    
    // Image source selection dialog
    if (showImageSourceDialog) {
        ImageSourceDialog(
            onDismiss = { showImageSourceDialog = false },
            onCameraSelected = {
                showImageSourceDialog = false
                tempCameraUri = createTempImageUri(context)
                tempCameraUri?.let { cameraLauncher.launch(it) }
            },
            onGallerySelected = {
                showImageSourceDialog = false
                galleryLauncher.launch("image/*")
            }
        )
    }
    
    // Permission denied dialog
    if (showPermissionDialog) {
        PermissionDeniedDialog(
            onDismiss = { showPermissionDialog = false }
        )
    }
}

/**
 * Image validation states
 */
enum class ImageValidationState {
    VALID,
    WARNING,
    INVALID
}

// Helper function to create temp URI for camera
private fun createTempImageUri(context: android.content.Context): Uri? {
    return try {
        val timeStamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        val imageFile = java.io.File.createTempFile(imageFileName, ".jpg", storageDir)
        androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    } catch (e: Exception) {
        Log.e("SecureImagePicker", "Error creating temp image file: ${e.message}")
        null
    }
}

// Helper functions for secure image handling
private fun handleSecureImageSelection(
    context: android.content.Context,
    uri: Uri?,
    onImageSelected: (Uri?) -> Unit,
    onValidationError: (String) -> Unit
) {
    if (uri == null) {
        onImageSelected(null)
        return
    }
    
    // Validate the selected image
    val strings = InputValidator.createStrings(context)
    val validationResult = InputValidator.validateImageUri(uri, context, strings)
    if (!validationResult.isValid) {
        onValidationError(validationResult.errorMessage)
        Log.w("SecureImagePicker", "Image validation failed: ${validationResult.errorMessage}")
        return
    }
    
    // Try to take persistent permission
    try {
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        Log.d("SecureImagePicker", "Persistent permission granted for URI: $uri")
    } catch (e: SecurityException) {
        Log.w("SecureImagePicker", "Could not take persistent permission: ${e.message}")
        // Continue anyway - the URI might still be accessible
    } catch (e: Exception) {
        Log.e("SecureImagePicker", "Error taking persistent permission: ${e.message}")
        onValidationError("Error accessing image file")
        return
    }
    
    onImageSelected(uri)
}

@Composable
private fun SecureImageContent(
    uri: Uri,
    onError: () -> Unit
) {
    AsyncImage(
        model = uri,
        contentDescription = stringResource(R.string.selected_project_name),
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        onError = {
            Log.e("SecureImagePicker", "Error loading image: ${it.result.throwable.message}")
            onError()
        }
    )
}

@Composable
private fun DefaultPlaceholderContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.AddAPhoto,
            contentDescription = stringResource(R.string.select_image),
            modifier = Modifier.size(32.dp),
            tint = BluePrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.add_photo),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = BluePrimary
        )
    }
}

@Composable
private fun ErrorPlaceholderContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            modifier = Modifier.size(32.dp),
            tint = Red
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.invalid_image),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            color = Red
        )
    }
}

@Preview(showBackground = false, name = "Default State")
@Composable
fun PreviewSecureImagePickerDefault() {
    ConstructionMaterialTrackTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            SecureImagePicker(
                selectedImageUri = null,
                onImageSelected = {},
                onValidationError = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "With Valid Image")
@Composable
fun PreviewSecureImagePickerWithImage() {
    ConstructionMaterialTrackTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            // Simular una imagen válida
            SecureImagePicker(
                selectedImageUri = "content://media/external/images/media/1".toUri(),
                onImageSelected = {},
                onValidationError = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Invalid Image Error")
@Composable
fun PreviewSecureImagePickerError() {
    ConstructionMaterialTrackTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            // Simular estado de error
            var validationState by remember { mutableStateOf(ImageValidationState.INVALID) }
            var validationMessage by remember { mutableStateOf("Unsupported image format. Use JPEG, PNG, or WebP") }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.project_image),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Transparent)
                        .border(
                            width = 2.dp,
                            shape = CircleShape,
                            color = Red // Error state
                        )
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    ErrorPlaceholderContent()
                }
                
                // Error message
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = validationMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = Red,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "File Too Large Error")
@Composable
fun PreviewSecureImagePickerFileTooLarge() {
    ConstructionMaterialTrackTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.project_image),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Transparent)
                        .border(
                            width = 2.dp,
                            shape = CircleShape,
                            color = Red
                        )
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    ErrorPlaceholderContent()
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Image file too large (max 5MB)",
                    style = MaterialTheme.typography.bodySmall,
                    color = Red,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Permission Denied Error")
@Composable
fun PreviewSecureImagePickerPermissionError() {
    ConstructionMaterialTrackTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.project_image),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Transparent)
                        .border(
                            width = 2.dp,
                            shape = CircleShape,
                            color = Red
                        )
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    ErrorPlaceholderContent()
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Permission denied to access image",
                    style = MaterialTheme.typography.bodySmall,
                    color = Red,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "All States Comparison")
@Composable
fun PreviewSecureImagePickerAllStates() {
    ConstructionMaterialTrackTheme {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✅ Default State",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SecureImagePicker(
                            selectedImageUri = null,
                            onImageSelected = {},
                            onValidationError = {}
                        )
                    }
                }
            }
            
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "❌ Invalid File Type",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.project_image),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(Transparent)
                                    .border(
                                        width = 2.dp,
                                        shape = CircleShape,
                                        color = Red
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                ErrorPlaceholderContent()
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Unsupported image format. Use JPEG, PNG, or WebP",
                                style = MaterialTheme.typography.bodySmall,
                                color = Red,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📏 File Too Large",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.project_image),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(Transparent)
                                    .border(
                                        width = 2.dp,
                                        shape = CircleShape,
                                        color = Red
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                ErrorPlaceholderContent()
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Image file too large (max 5MB)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Red,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
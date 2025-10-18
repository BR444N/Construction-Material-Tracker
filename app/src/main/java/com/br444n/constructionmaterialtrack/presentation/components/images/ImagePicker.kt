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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.br444n.constructionmaterialtrack.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.br444n.constructionmaterialtrack.core.utils.PermissionUtils
import com.br444n.constructionmaterialtrack.ui.theme.BlueDark
import com.br444n.constructionmaterialtrack.ui.theme.BlueLight
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.Red

@Composable
fun ImagePicker(
    selectedImageUri: Uri?,
    onImageSelected: (Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    var isUriValid by remember { mutableStateOf(true) }
    
    // Validate URI when it changes
    LaunchedEffect(selectedImageUri) {
        isUriValid = validateUri(context, selectedImageUri)
    }
    
    // Enhanced image picker launcher with persistent permissions
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        handleImageSelection(context, uri, onImageSelected)
    }
    
    // Permission launcher for different Android versions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        handlePermissionResult(context, permissions, imagePickerLauncher) {
            showPermissionDialog = true
        }
    }
    
    // Function to request appropriate permissions
    fun requestImagePermission() {
        requestPermissionIfNeeded(context, permissionLauncher, imagePickerLauncher)
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
                .background(BlueLight.copy(alpha = 0.3f))
                .border(
                    width = 2.dp,
                    shape = CircleShape, color = BlueDark
                )
                .clickable { requestImagePermission() },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null && isUriValid) {
                ImageContent(
                    uri = selectedImageUri,
                    onError = { isUriValid = false }
                )
            } else {
                PlaceholderContent(
                    hasInvalidUri = selectedImageUri != null
                )
            }
        }
    }
    
    // Permission denied dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text(stringResource(R.string.permission_required)) },
            text = { 
                Text(PermissionUtils.getPermissionExplanation()) 
            },
            confirmButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

// Helper functions to reduce cognitive complexity
private fun validateUri(context: android.content.Context, uri: Uri?): Boolean {
    return uri?.let { 
        val isValid = PermissionUtils.isUriAccessible(context, it)
        if (!isValid) {
            Log.w("ImagePicker", "Selected URI is no longer accessible: $it")
        }
        isValid
    } ?: true
}

private fun handleImageSelection(
    context: android.content.Context,
    uri: Uri?,
    onImageSelected: (Uri?) -> Unit
) {
    uri?.let { selectedUri ->
        try {
            context.contentResolver.takePersistableUriPermission(
                selectedUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            Log.d("ImagePicker", "Persistent permission granted for URI: $selectedUri")
        } catch (e: SecurityException) {
            Log.w("ImagePicker", "Could not take persistent permission: ${e.message}")
        } catch (e: Exception) {
            Log.e("ImagePicker", "Error taking persistent permission: ${e.message}")
        }
    }
    onImageSelected(uri)
}

private fun handlePermissionResult(
    context: android.content.Context,
    permissions: Map<String, Boolean>,
    imagePickerLauncher: androidx.activity.result.ActivityResultLauncher<String>,
    onPermissionDenied: () -> Unit
) {
    val hasAnyPermission = permissions.values.any { it }
    
    if (hasAnyPermission || PermissionUtils.hasImagePermissions(context)) {
        imagePickerLauncher.launch("image/*")
    } else {
        onPermissionDenied()
    }
}

private fun requestPermissionIfNeeded(
    context: android.content.Context,
    permissionLauncher: androidx.activity.result.ActivityResultLauncher<Array<String>>,
    imagePickerLauncher: androidx.activity.result.ActivityResultLauncher<String>
) {
    if (PermissionUtils.hasImagePermissions(context)) {
        imagePickerLauncher.launch("image/*")
    } else {
        permissionLauncher.launch(PermissionUtils.getImagePermissions())
    }
}

@Composable
private fun ImageContent(
    uri: Uri,
    onError: () -> Unit
) {
    AsyncImage(
        model = uri,
        contentDescription = stringResource(R.string.selected_project_name),
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        onError = {
            Log.e("ImagePicker", "Error loading image: ${it.result.throwable.message}")
            onError()
        }
    )
}

@Composable
private fun PlaceholderContent(
    hasInvalidUri: Boolean
) {
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
            text = if (hasInvalidUri) {
                "Image Unavailable\nTap to Select New"
            } else {
                stringResource(R.string.add_photo)
            },
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = if (hasInvalidUri) Red else BluePrimary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewImagePicker(modifier: Modifier = Modifier) {
    ImagePicker(
        selectedImageUri = null,
        onImageSelected = {},
        modifier = modifier
    )
}
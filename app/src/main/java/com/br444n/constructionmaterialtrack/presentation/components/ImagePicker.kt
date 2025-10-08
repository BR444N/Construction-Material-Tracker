package com.br444n.constructionmaterialtrack.presentation.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.br444n.constructionmaterialtrack.core.utils.PermissionUtils
import com.br444n.constructionmaterialtrack.ui.theme.BlueLight
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.Red
import com.br444n.constructionmaterialtrack.ui.theme.SurfaceLight

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
        selectedImageUri?.let { uri ->
            isUriValid = PermissionUtils.isUriAccessible(context, uri)
            if (!isUriValid) {
                Log.w("ImagePicker", "Selected URI is no longer accessible: $uri")
            }
        }
    }
    
    // Enhanced image picker launcher with persistent permissions
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            // Take persistent permission for the URI
            try {
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or 
                           Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                
                context.contentResolver.takePersistableUriPermission(
                    selectedUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                
                Log.d("ImagePicker", "Persistent permission granted for URI: $selectedUri")
            } catch (e: SecurityException) {
                Log.w("ImagePicker", "Could not take persistent permission: ${e.message}")
                // URI might still work for current session
            } catch (e: Exception) {
                Log.e("ImagePicker", "Error taking persistent permission: ${e.message}")
            }
        }
        onImageSelected(uri)
    }
    
    // Permission launcher for different Android versions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val hasAnyPermission = permissions.values.any { it }
        
        if (hasAnyPermission || PermissionUtils.hasImagePermissions(context)) {
            imagePickerLauncher.launch("image/*")
        } else {
            showPermissionDialog = true
        }
    }
    
    // Function to request appropriate permissions
    fun requestImagePermission() {
        if (PermissionUtils.hasImagePermissions(context)) {
            imagePickerLauncher.launch("image/*")
        } else {
            permissionLauncher.launch(PermissionUtils.getImagePermissions())
        }
    }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Project Image",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = SurfaceLight
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(BlueLight.copy(alpha = 0.3f))
                .border(width = 2.dp,
                    shape = CircleShape, color = BlueLight.copy(alpha = 0.3f))
                .clickable { requestImagePermission() },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null && isUriValid) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Selected Project Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    onError = {
                        Log.e("ImagePicker", "Error loading image: ${it.result.throwable.message}")
                        isUriValid = false
                    }
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Select Image",
                        modifier = Modifier.size(32.dp),
                        tint = BluePrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (selectedImageUri != null && !isUriValid) {
                            "Image Unavailable\nTap to Select New"
                        } else {
                            "Add Photo"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selectedImageUri != null && !isUriValid) {
                            Red
                        } else {
                            BluePrimary
                        }
                    )
                }
            }
        }
    }
    
    // Permission denied dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permission Required") },
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

@Preview(showBackground = true)
@Composable
fun PreviewImagePicker(modifier: Modifier = Modifier) {
    ImagePicker(
        selectedImageUri = null,
        onImageSelected = {},
        modifier = modifier
    )
}
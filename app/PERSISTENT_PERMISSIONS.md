# Persistent Permissions Implementation

## Overview

This implementation provides robust persistent permissions for image URIs in the Construction Material Track app, ensuring that selected images remain accessible across app sessions and device reboots.

## Key Features

### 1. Persistent URI Permissions

- **Automatic Permission Taking**: When a user selects an image, the app automatically requests persistent permission for that URI
- **Cross-Session Access**: Images remain accessible even after the app is closed and reopened
- **Device Reboot Survival**: Permissions persist through device reboots

### 2. Permission Validation

- **Real-time Validation**: Checks if URIs are still accessible when loading images
- **Error Handling**: Gracefully handles cases where images become unavailable
- **Visual Feedback**: Shows clear indicators when images are no longer accessible

### 3. Android Version Compatibility

- **Android 14+ (API 34+)**: Uses Selected Photos Access for granular permissions
- **Android 13+ (API 33+)**: Uses READ_MEDIA_IMAGES permission
- **Android 12 and below**: Uses READ_EXTERNAL_STORAGE permission

## Implementation Details

### ImagePicker Component

```kotlin
// Enhanced persistent permission handling
val imagePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
) { uri: Uri? ->
    uri?.let { selectedUri ->
        try {
            context.contentResolver.takePersistableUriPermission(
                selectedUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (e: SecurityException) {
            // Handle cases where persistent permission isn't available
        }
    }
    onImageSelected(uri)
}
```

### Permission Utilities

- `hasUriPermission()`: Check if specific URI has persistent permission
- `getPersistedUriPermissions()`: Get all persisted URI permissions
- `releaseUriPermission()`: Release persistent permission for a URI
- `isUriAccessible()`: Validate if URI is still accessible
- `cleanupInvalidUriPermissions()`: Clean up invalid persisted permissions

## Benefits

### For Users

- **Seamless Experience**: Images remain visible without re-selection
- **Privacy Control**: On Android 14+, users can grant access to specific photos only
- **Reliable Storage**: Images don't disappear after app restarts

### For Developers

- **Robust Error Handling**: Graceful degradation when permissions are lost
- **Memory Efficiency**: Only stores URI references, not actual image data
- **Cross-Platform Compatibility**: Works across different Android versions

## Usage Examples

### Basic Image Selection

```kotlin
ImagePicker(
    selectedImageUri = projectImageUri,
    onImageSelected = { uri ->
        // URI automatically has persistent permission
        viewModel.updateProjectImage(uri)
    }
)
```

### Checking URI Validity

```kotlin
if (PermissionUtils.isUriAccessible(context, imageUri)) {
    // Safe to display image
    AsyncImage(model = imageUri)
} else {
    // Show placeholder or error state
}
```

### Cleanup Invalid Permissions

```kotlin
// Call periodically or on app start
PermissionUtils.cleanupInvalidUriPermissions(context)
```

## Best Practices

1. **Always Check Accessibility**: Validate URIs before displaying images
2. **Handle Errors Gracefully**: Provide fallbacks when images become unavailable
3. **Clean Up Regularly**: Remove invalid permissions to prevent accumulation
4. **User Feedback**: Show clear messages when images need to be re-selected
5. **Logging**: Use appropriate logging levels for debugging permission issues

## Troubleshooting

### Common Issues

- **SecurityException**: Occurs when persistent permission can't be granted
- **FileNotFoundException**: URI no longer points to valid file
- **Permission Denied**: User revoked permissions or file moved

### Solutions

- Implement try-catch blocks around permission operations
- Validate URIs before use
- Provide clear user feedback for permission issues
- Offer re-selection options when images become unavailable

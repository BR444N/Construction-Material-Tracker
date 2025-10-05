# Android 14+ Selected Photos Access

## Overview
Starting with Android 14 (API level 34), Android introduced **Selected Photos Access** to give users more granular control over photo permissions.

## How it Works

### Before Android 14
- Apps requested `READ_EXTERNAL_STORAGE` or `READ_MEDIA_IMAGES`
- Users had to grant access to ALL photos or none

### Android 14+
- Apps can request both `READ_MEDIA_IMAGES` and `READ_MEDIA_VISUAL_USER_SELECTED`
- Users get three options:
  1. **Allow all photos** - Traditional full access
  2. **Select photos** - Choose specific photos to share
  3. **Don't allow** - No access

## Implementation in Our App

### Permissions in AndroidManifest.xml
```xml
<!-- Full media access (Android 13+) -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />

<!-- Selected photos access (Android 14+) -->
<uses-permission android:name="android.permission.READ_MEDIA_VISUAL_USER_SELECTED" />
```

### Permission Handling
Our `PermissionUtils` class handles the different Android versions:

1. **Android 12 and below**: `READ_EXTERNAL_STORAGE`
2. **Android 13**: `READ_MEDIA_IMAGES`
3. **Android 14+**: Both `READ_MEDIA_IMAGES` and `READ_MEDIA_VISUAL_USER_SELECTED`

### User Experience
- When user taps "Add Photo", the system shows appropriate permission dialog
- On Android 14+, users can choose between full access or selected photos
- Our app works with either permission level

### Benefits
- **Better privacy**: Users control exactly which photos apps can access
- **Improved UX**: Users don't have to grant full photo access for simple use cases
- **Future-proof**: App works across all Android versions

## Testing
To test Selected Photos Access:
1. Use Android 14+ device or emulator
2. Install the app
3. Try to add a project image
4. Observe the permission dialog options
5. Test both "Select photos" and "Allow all" options

## Best Practices
- Always request both permissions on Android 14+
- Handle both permission states gracefully
- Provide clear explanation of why photo access is needed
- Test on different Android versions
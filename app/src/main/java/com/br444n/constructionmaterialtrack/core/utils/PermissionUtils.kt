package com.br444n.constructionmaterialtrack.core.utils

import android.Manifest
import android.content.Context
import android.content.UriPermission
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat

object PermissionUtils {
    
    /**
     * Get the appropriate image permissions based on Android version
     */
    fun getImagePermissions(): Array<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                // Android 14+ (API 34+) - Selected Photos Access
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Android 13+ (API 33+)
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            }
            else -> {
                // Android 12 and below
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
    
    /**
     * Check if we have the necessary image permissions
     */
    fun hasImagePermissions(context: Context): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                // Android 14+ - Check for either full access or selected photos access
                ContextCompat.checkSelfPermission(
                    context, 
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context, 
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                ) == PackageManager.PERMISSION_GRANTED
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                // Android 13+
                ContextCompat.checkSelfPermission(
                    context, 
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            }
            else -> {
                // Android 12 and below
                ContextCompat.checkSelfPermission(
                    context, 
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }
    
    /**
     * Get user-friendly permission explanation based on Android version
     */
    fun getPermissionExplanation(): String {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                "This app needs access to your photos to set project images. You can choose to give access to all photos or select specific photos."
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                "This app needs access to your photos to set project images."
            }
            else -> {
                "This app needs storage permission to access your photos for project images."
            }
        }
    }
    
    /**
     * Check if a specific URI has persistent permission
     */
    fun hasUriPermission(context: Context, uri: Uri): Boolean {
        return try {
            val persistedUris = context.contentResolver.persistedUriPermissions
            persistedUris.any { permission ->
                permission.uri == uri && permission.isReadPermission
            }
        } catch (e: Exception) {
            Log.e("PermissionUtils", "Error checking URI permission: ${e.message}")
            false
        }
    }
    
    /**
     * Get all persisted URI permissions
     */
    fun getPersistedUriPermissions(context: Context): List<UriPermission> {
        return try {
            context.contentResolver.persistedUriPermissions
        } catch (e: Exception) {
            Log.e("PermissionUtils", "Error getting persisted permissions: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * Release persistent permission for a URI
     */
    fun releaseUriPermission(context: Context, uri: Uri) {
        try {
            context.contentResolver.releasePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            Log.d("PermissionUtils", "Released persistent permission for URI: $uri")
        } catch (e: Exception) {
            Log.w("PermissionUtils", "Could not release URI permission: ${e.message}")
        }
    }
    
    /**
     * Validate if URI is still accessible
     */
    fun isUriAccessible(context: Context, uri: Uri): Boolean {
        return try {
            context.contentResolver.openInputStream(uri)?.use { true } ?: false
        } catch (e: Exception) {
            Log.w("PermissionUtils", "URI not accessible: ${e.message}")
            false
        }
    }
    
    /**
     * Clean up invalid persisted URI permissions
     */
    fun cleanupInvalidUriPermissions(context: Context) {
        try {
            val persistedUris = context.contentResolver.persistedUriPermissions
            persistedUris.forEach { permission ->
                if (!isUriAccessible(context, permission.uri)) {
                    releaseUriPermission(context, permission.uri)
                    Log.d("PermissionUtils", "Cleaned up invalid URI: ${permission.uri}")
                }
            }
        } catch (e: Exception) {
            Log.e("PermissionUtils", "Error cleaning up URI permissions: ${e.message}")
        }
    }
}
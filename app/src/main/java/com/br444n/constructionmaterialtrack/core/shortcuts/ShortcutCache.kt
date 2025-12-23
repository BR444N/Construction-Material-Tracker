package com.br444n.constructionmaterialtrack.core.shortcuts

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

/**
 * Dual-layer cache for shortcut icons (memory + disk)
 */
class ShortcutCache(private val context: Context) {
    
    // Memory cache: 4MB for quick access
    private val memoryCache = object : LruCache<String, Bitmap>(4 * 1024 * 1024) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount
        }
    }
    
    private val cacheDir: File by lazy {
        File(context.cacheDir, "shortcuts").apply {
            if (!exists()) mkdirs()
        }
    }
    
    /**
     * Get cached bitmap (checks memory first, then disk)
     */
    fun get(projectId: String, imageHash: String): Bitmap? {
        val cacheKey = getCacheKey(projectId, imageHash)
        
        // Check memory cache first
        memoryCache[cacheKey]?.let { return it }
        
        // Check disk cache
        return loadFromDisk(cacheKey)?.also { bitmap ->
            // Store in memory for next access
            memoryCache.put(cacheKey, bitmap)
        }
    }
    
    /**
     * Save bitmap to cache (memory + disk)
     */
    fun put(projectId: String, imageHash: String, bitmap: Bitmap) {
        val cacheKey = getCacheKey(projectId, imageHash)
        
        // Save to memory
        memoryCache.put(cacheKey, bitmap)
        
        // Save to disk
        saveToDisk(cacheKey, bitmap)
    }
    
    /**
     * Invalidate cache for a specific project
     */
    fun invalidate(projectId: String) {
        // Remove from memory
        val keysToRemove = mutableListOf<String>()
        memoryCache.snapshot().keys.forEach { key ->
            if (key.startsWith("${projectId}_")) {
                keysToRemove.add(key)
            }
        }
        keysToRemove.forEach { memoryCache.remove(it) }
        
        // Remove from disk
        cacheDir.listFiles()?.forEach { file ->
            if (file.name.startsWith("${projectId}_")) {
                val deleted = file.delete()
                if (!deleted) {
                    android.util.Log.w("ShortcutCache", "Failed to delete cache file: ${file.name}")
                }
            }
        }
    }
    
    /**
     * Clean up old cache files (older than 7 days)
     */
    fun cleanup() {
        val maxAge = 7 * 24 * 60 * 60 * 1000L // 7 days
        val now = System.currentTimeMillis()
        
        cacheDir.listFiles()?.forEach { file ->
            if (now - file.lastModified() > maxAge) {
                val deleted = file.delete()
                if (!deleted) {
                    android.util.Log.w("ShortcutCache", "Failed to delete old cache file: ${file.name}")
                }
            }
        }
    }
    
    /**
     * Clear all cache
     */
    @Suppress("unused")
    fun clear() {
        memoryCache.evictAll()
        cacheDir.listFiles()?.forEach { 
            val deleted = it.delete()
            if (!deleted) {
                android.util.Log.w("ShortcutCache", "Failed to delete cache file: ${it.name}")
            }
        }
    }
    
    /**
     * Get cache size in bytes
     */
    @Suppress("unused")
    fun getCacheSize(): Long {
        return cacheDir.listFiles()?.sumOf { it.length() } ?: 0L
    }
    
    private fun getCacheKey(projectId: String, imageHash: String): String {
        return "${projectId}_${imageHash}"
    }
    
    private fun loadFromDisk(cacheKey: String): Bitmap? {
        return try {
            val file = File(cacheDir, "$cacheKey.png")
            if (file.exists()) {
                BitmapFactory.decodeFile(file.absolutePath)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun saveToDisk(cacheKey: String, bitmap: Bitmap) {
        try {
            val file = File(cacheDir, "$cacheKey.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    companion object {
        /**
         * Calculate hash of image file or Content URI for cache key
         */
        fun calculateImageHash(imagePath: String): String? {
            return try {
                // For Content URIs, use the URI itself as the hash source
                if (imagePath.startsWith("content://")) {
                    val digest = MessageDigest.getInstance("MD5")
                    val bytes = imagePath.toByteArray()
                    val hash = digest.digest(bytes)
                    return hash.joinToString("") { "%02x".format(it) }.take(8)
                }
                
                // For file paths, hash the file content
                val file = File(imagePath)
                if (!file.exists()) return null
                
                val digest = MessageDigest.getInstance("MD5")
                val bytes = file.readBytes()
                val hash = digest.digest(bytes)
                hash.joinToString("") { "%02x".format(it) }.take(8)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

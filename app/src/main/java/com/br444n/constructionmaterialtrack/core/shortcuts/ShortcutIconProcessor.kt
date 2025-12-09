package com.br444n.constructionmaterialtrack.core.shortcuts

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat
import com.br444n.constructionmaterialtrack.R
import com.br444n.constructionmaterialtrack.domain.model.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Processes and prepares icons for shortcuts in background
 */
class ShortcutIconProcessor(private val context: Context) {
    
    private val cache = ShortcutCache(context)
    private val processingLocks = mutableMapOf<String, Mutex>()
    
    // Target size for shortcut icons (adaptive icons work best at 108dp)
    private val iconSizePx: Int by lazy {
        val density = context.resources.displayMetrics.density
        (ICON_SIZE_DP * density).toInt()
    }
    
    /**
     * Prepare icon for project (async, with caching)
     * Returns null if processing fails
     */
    suspend fun prepareIcon(project: Project): Bitmap? = withContext(Dispatchers.IO) {
        try {
            // Get or create lock for this project
            val lock = synchronized(processingLocks) {
                processingLocks.getOrPut(project.id) { Mutex() }
            }
            
            // Ensure only one thread processes this project at a time
            lock.withLock {
                processIconInternal(project)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            // Clean up lock
            synchronized(processingLocks) {
                processingLocks.remove(project.id)
            }
        }
    }
    
    /**
     * Get default icon (fallback)
     */
    suspend fun getDefaultIcon(): Bitmap = withContext(Dispatchers.IO) {
        try {
            // Try to load default project image
            val bitmap = BitmapFactory.decodeResource(
                context.resources,
                R.drawable.pose_def_project
            )
            
            if (bitmap != null) {
                val resized = resizeBitmap(bitmap, iconSizePx)
                bitmap.recycle()
                applyCircularMask(resized)
            } else {
                // Fallback to app icon
                createIconFromDrawable()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            createIconFromDrawable()
        }
    }
    
    /**
     * Invalidate cache for project
     */
    fun invalidateCache(projectId: String) {
        cache.invalidate(projectId)
    }
    
    /**
     * Clean up old cache
     */
    fun cleanupCache() {
        cache.cleanup()
    }
    
    private suspend fun processIconInternal(project: Project): Bitmap? {
        // If no image, return default (don't cache it)
        val imageUri = project.imageUri
        if (imageUri == null) {
            println("ShortcutIconProcessor: Project ${project.id} has no image, using default")
            return getDefaultIcon()
        }
        
        println("ShortcutIconProcessor: Processing image for project ${project.id}, path: $imageUri")
        
        // Calculate hash for cache key
        val imageHash = ShortcutCache.calculateImageHash(imageUri)
        if (imageHash == null) {
            println("ShortcutIconProcessor: Failed to calculate hash for $imageUri, using default")
            return getDefaultIcon()
        }
        
        println("ShortcutIconProcessor: Image hash: $imageHash")
        
        // Check cache first
        cache.get(project.id, imageHash)?.let { 
            println("ShortcutIconProcessor: Using cached icon for project ${project.id}")
            return it 
        }
        
        println("ShortcutIconProcessor: Cache miss, loading and processing image")
        
        // Load and process image
        val processedBitmap = loadAndProcessImage(imageUri)
        if (processedBitmap == null) {
            println("ShortcutIconProcessor: Failed to load image from $imageUri, using default")
            return getDefaultIcon()
        }
        
        println("ShortcutIconProcessor: Successfully processed image, saving to cache")
        
        // Save to cache
        cache.put(project.id, imageHash, processedBitmap)
        
        return processedBitmap
    }
    
    private fun loadAndProcessImage(imagePath: String): Bitmap? {
        return try {
            // Check if it's a content URI
            if (imagePath.startsWith("content://")) {
                println("ShortcutIconProcessor: Loading from Content URI")
                return loadFromContentUri(imagePath)
            }
            
            // Otherwise, treat as file path
            val file = File(imagePath)
            if (!file.exists()) {
                println("ShortcutIconProcessor: Image file does not exist: $imagePath")
                return null
            }
            
            println("ShortcutIconProcessor: Image file exists, size: ${file.length()} bytes")
            
            // First, decode bounds to get dimensions
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(file.absolutePath, options)
            
            // Calculate sample size to avoid loading full resolution
            options.inSampleSize = calculateInSampleSize(
                options.outWidth,
                options.outHeight,
                iconSizePx,
                iconSizePx
            )
            
            // Now decode with sample size
            options.inJustDecodeBounds = false
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            
            val bitmap = BitmapFactory.decodeFile(file.absolutePath, options) ?: return null
            
            // Resize to exact size
            val resized = resizeBitmap(bitmap, iconSizePx)
            if (resized != bitmap) {
                bitmap.recycle()
            }
            
            // Apply circular mask
            val circular = applyCircularMask(resized)
            if (circular != resized) {
                resized.recycle()
            }
            
            circular
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun calculateInSampleSize(
        width: Int,
        height: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        var inSampleSize = 1
        
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            
            while (halfHeight / inSampleSize >= reqHeight &&
                halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        
        return inSampleSize
    }
    
    private fun resizeBitmap(bitmap: Bitmap, targetSize: Int): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        if (size == targetSize) return bitmap
        
        return Bitmap.createScaledBitmap(bitmap, targetSize, targetSize, true)
    }
    
    private fun applyCircularMask(bitmap: Bitmap): Bitmap {
        val size = bitmap.width
        
        // Create output bitmap with white background for adaptive icons
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        
        // Fill with white background (Google recommendation for adaptive icons)
        canvas.drawColor(Color.WHITE)
        
        // Create circular path
        val path = android.graphics.Path().apply {
            addCircle(size / 2f, size / 2f, size / 2f, android.graphics.Path.Direction.CW)
        }
        
        // Clip to circle
        canvas.clipPath(path)
        
        // Draw the bitmap within the circular clip
        val paint = Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
            isDither = true
        }
        
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        
        return output
    }
    
    private fun loadFromContentUri(contentUri: String): Bitmap? {
        return try {
            val uri = android.net.Uri.parse(contentUri)
            
            // Open input stream from Content URI
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                // First, decode bounds to get dimensions
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                
                // Need to reset stream, so decode from URI directly
                BitmapFactory.decodeStream(
                    context.contentResolver.openInputStream(uri),
                    null,
                    options
                )
                
                // Calculate sample size
                options.inSampleSize = calculateInSampleSize(
                    options.outWidth,
                    options.outHeight,
                    iconSizePx,
                    iconSizePx
                )
                
                // Now decode with sample size
                options.inJustDecodeBounds = false
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                
                val bitmap = context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream, null, options)
                } ?: return null
                
                // Resize to exact size
                val resized = resizeBitmap(bitmap, iconSizePx)
                if (resized != bitmap) {
                    bitmap.recycle()
                }
                
                // Apply circular mask
                val circular = applyCircularMask(resized)
                if (circular != resized) {
                    resized.recycle()
                }
                
                circular
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun createIconFromDrawable(): Bitmap {
        val drawable = ContextCompat.getDrawable(context, R.drawable.icon_app_blue)
            ?: throw IllegalStateException("Default icon not found")
        
        val bitmap = Bitmap.createBitmap(
            iconSizePx,
            iconSizePx,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        
        return applyCircularMask(bitmap)
    }
    
    companion object {
        // Final icon size - this is the actual size that will be displayed
        // 48dp is a good balance between quality and performance
        // Adjust this value to make icons larger (64dp, 72dp) or smaller (32dp, 40dp)
        private const val ICON_SIZE_DP = 48
    }
}

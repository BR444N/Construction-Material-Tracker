package com.br444n.constructionmaterialtrack.core.pdf.components

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.net.toUri
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.properties.HorizontalAlignment
import java.io.ByteArrayOutputStream

class PdfImageHandler(private val contentResolver: ContentResolver) {
    
    companion object {
        private const val TAG = "PdfImageHandler"
        private const val IMAGE_WIDTH = 120f
        private const val IMAGE_HEIGHT = 120f
        private const val IMAGE_MARGIN_BOTTOM = 20f
        private const val JPEG_QUALITY = 80
    }
    
    fun createProjectImage(imageUri: String?): Image? {
        return try {
            imageUri?.let { uri ->
                contentResolver.openInputStream(uri.toUri())?.use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                    bitmap?.let { 
                        val image = convertBitmapToImage(it)
                        it.recycle() // Prevent memory leaks
                        image
                    }
                }
            }
        } catch (e: Exception) {
            android.util.Log.w(TAG, "Failed to load project image: $imageUri", e)
            null // Return null if image loading fails
        }
    }
    
    private fun convertBitmapToImage(bitmap: Bitmap): Image {
        // Convert bitmap to byte array
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream)
        val imageBytes = outputStream.toByteArray()
        
        // Create iText image
        val imageData = ImageDataFactory.create(imageBytes)
        val image = Image(imageData)
        
        // Set image properties (circular effect through size and positioning)
        image.setWidth(IMAGE_WIDTH)
        image.setHeight(IMAGE_HEIGHT)
        image.setHorizontalAlignment(HorizontalAlignment.CENTER)
        image.setMarginBottom(IMAGE_MARGIN_BOTTOM)
        
        return image
    }
}
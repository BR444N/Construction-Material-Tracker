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
    
    fun createProjectImage(imageUri: String?): Image? {
        return try {
            imageUri?.let { uri ->
                val inputStream = contentResolver.openInputStream(uri.toUri())
                inputStream?.use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                    bitmap?.let { convertBitmapToImage(it) }
                }
            }
        } catch (e: Exception) {
            null // Return null if image loading fails
        }
    }
    
    private fun convertBitmapToImage(bitmap: Bitmap): Image {
        // Convert bitmap to byte array
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val imageBytes = outputStream.toByteArray()
        
        // Create iText image
        val imageData = ImageDataFactory.create(imageBytes)
        val image = Image(imageData)
        
        // Set image properties (circular effect through size and positioning)
        image.setWidth(120f)
        image.setHeight(120f)
        image.setHorizontalAlignment(HorizontalAlignment.CENTER)
        image.setMarginBottom(20f)
        
        return image
    }
}
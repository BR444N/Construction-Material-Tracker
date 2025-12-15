package com.br444n.constructionmaterialtrack.core.pdf.components

import com.itextpdf.kernel.colors.DeviceGray
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.properties.TextAlignment

object PdfStyleHelper {
    
    fun createHeaderCell(text: String): Cell {
        return Cell().add(
            Paragraph(text)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
        ).setBackgroundColor(DeviceGray(0.8f))
    }
    
    fun createProjectTitle(projectName: String): Paragraph {
        return Paragraph(projectName)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(24f)
            .setMarginTop(20f)
            .setMarginBottom(10f)
    }
    
    fun createProjectDescription(description: String): Paragraph {
        return Paragraph(description)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(16f)
            .setMarginBottom(30f)
    }
    
    fun createSectionHeader(title: String): Paragraph {
        return Paragraph(title)
            .setFontSize(18f)
            .setMarginBottom(15f)
    }
    
    fun createEmptyMessage(message: String): Paragraph {
        return Paragraph(message)
            .setFontSize(12f)
            .setFontColor(DeviceGray(0.5f))
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(20f)
    }
    
    fun createTotalCost(totalCost: Double): Paragraph {
        return Paragraph("Total Estimated Cost: $${String.format(java.util.Locale.getDefault(), "%.2f", totalCost)}")
            .setFontSize(14f)
            .setTextAlignment(TextAlignment.RIGHT)
            .setMarginTop(20f)
    }
}
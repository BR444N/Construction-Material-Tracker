package com.br444n.constructionmaterialtrack.core.pdf.components

import com.itextpdf.kernel.colors.DeviceGray
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.properties.TextAlignment
import java.util.*

object PdfStyleHelper {
    
    // Font sizes
    private const val HEADER_FONT_SIZE = 12f
    private const val TITLE_FONT_SIZE = 24f
    private const val DESCRIPTION_FONT_SIZE = 16f
    private const val SECTION_FONT_SIZE = 18f
    private const val EMPTY_MESSAGE_FONT_SIZE = 12f
    private const val TOTAL_COST_FONT_SIZE = 14f
    
    // Margins
    private const val TITLE_MARGIN_TOP = 20f
    private const val TITLE_MARGIN_BOTTOM = 10f
    private const val DESCRIPTION_MARGIN_BOTTOM = 30f
    private const val SECTION_MARGIN_BOTTOM = 15f
    private const val EMPTY_MESSAGE_MARGIN_TOP = 20f
    private const val TOTAL_COST_MARGIN_TOP = 20f
    
    // Colors
    private val HEADER_BACKGROUND_COLOR = DeviceGray(0.8f)
    private val EMPTY_MESSAGE_COLOR = DeviceGray(0.5f)
    
    fun createHeaderCell(text: String): Cell {
        return Cell().add(
            Paragraph(text)
                .setFontSize(HEADER_FONT_SIZE)
                .setTextAlignment(TextAlignment.CENTER)
        ).setBackgroundColor(HEADER_BACKGROUND_COLOR)
    }
    
    fun createProjectTitle(projectName: String): Paragraph {
        return Paragraph(projectName)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(TITLE_FONT_SIZE)
            .setMarginTop(TITLE_MARGIN_TOP)
            .setMarginBottom(TITLE_MARGIN_BOTTOM)
    }
    
    fun createProjectDescription(description: String): Paragraph {
        return Paragraph(description)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(DESCRIPTION_FONT_SIZE)
            .setMarginBottom(DESCRIPTION_MARGIN_BOTTOM)
    }
    
    fun createSectionHeader(title: String): Paragraph {
        return Paragraph(title)
            .setFontSize(SECTION_FONT_SIZE)
            .setMarginBottom(SECTION_MARGIN_BOTTOM)
    }
    
    fun createEmptyMessage(message: String): Paragraph {
        return Paragraph(message)
            .setFontSize(EMPTY_MESSAGE_FONT_SIZE)
            .setFontColor(EMPTY_MESSAGE_COLOR)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(EMPTY_MESSAGE_MARGIN_TOP)
    }
    
    fun createTotalCost(totalCost: Double): Paragraph {
        return Paragraph("Total Estimated Cost: $${String.format(Locale.US, "%.2f", totalCost)}")
            .setFontSize(TOTAL_COST_FONT_SIZE)
            .setTextAlignment(TextAlignment.RIGHT)
            .setMarginTop(TOTAL_COST_MARGIN_TOP)
    }
}
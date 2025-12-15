package com.br444n.constructionmaterialtrack.core.pdf.components

import com.br444n.constructionmaterialtrack.domain.model.Material
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.util.*

class PdfTableBuilder {
    
    fun createMaterialsTable(materials: List<Material>): Table {
        val table = Table(UnitValue.createPercentArray(floatArrayOf(8f, 35f, 12f, 8f, 15f, 22f)))
            .setWidth(UnitValue.createPercentValue(100f))
        
        // Add headers
        addTableHeaders(table)
        
        // Add material rows
        materials.forEach { material ->
            addMaterialRow(table, material)
        }
        
        return table
    }
    
    private fun addTableHeaders(table: Table) {
        table.addHeaderCell(PdfStyleHelper.createHeaderCell("Status"))
        table.addHeaderCell(PdfStyleHelper.createHeaderCell("Material"))
        table.addHeaderCell(PdfStyleHelper.createHeaderCell("Quantity"))
        table.addHeaderCell(PdfStyleHelper.createHeaderCell("Unit"))
        table.addHeaderCell(PdfStyleHelper.createHeaderCell("Price"))
        table.addHeaderCell(PdfStyleHelper.createHeaderCell("Description"))
    }
    
    private fun addMaterialRow(table: Table, material: Material) {
        // Status (checkbox)
        val statusCell = Cell().add(
            Paragraph(if (material.isPurchased) "☑" else "☐")
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
        )
        table.addCell(statusCell)
        
        // Material name
        val nameCell = Cell().add(
            Paragraph(material.name)
                .setFontSize(12f)
        )
        table.addCell(nameCell)
        
        // Quantity
        val quantityCell = Cell().add(
            Paragraph(material.quantity)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
        )
        table.addCell(quantityCell)
        
        // Unit
        val unitCell = Cell().add(
            Paragraph(material.unit)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
        )
        table.addCell(unitCell)
        
        // Price
        val priceText = formatPrice(material.price)
        val priceCell = Cell().add(
            Paragraph(priceText)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.RIGHT)
        )
        table.addCell(priceCell)
        
        // Description
        val descriptionCell = Cell().add(
            Paragraph(material.description.ifBlank { "-" })
                .setFontSize(10f)
        )
        table.addCell(descriptionCell)
    }
    
    private fun formatPrice(price: String): String {
        return try {
            val priceValue = price.toDoubleOrNull() ?: 0.0
            "$${String.format(Locale.getDefault(), "%.2f", priceValue)}"
        } catch (e: Exception) {
            "$$price"
        }
    }
    
    fun calculateTotalCost(materials: List<Material>): Double {
        var totalCost = 0.0
        materials.forEach { material ->
            try {
                val price = material.price.toDoubleOrNull() ?: 0.0
                val quantity = material.quantity.toIntOrNull() ?: 0
                totalCost += price * quantity
            } catch (e: Exception) {
                // Skip materials with invalid price/quantity
            }
        }
        return totalCost
    }
}
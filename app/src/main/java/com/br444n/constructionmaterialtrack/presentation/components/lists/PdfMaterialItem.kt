package com.br444n.constructionmaterialtrack.presentation.components.lists

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.domain.model.Material
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

@Composable
fun PdfMaterialItem(
    material: Material,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Checkbox
        MaterialStatusIcon(isPurchased = material.isPurchased)
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Material Details
        MaterialDetails(material = material)
    }
}

@Composable
private fun MaterialStatusIcon(
    isPurchased: Boolean,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = if (isPurchased) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
        contentDescription = if (isPurchased) "Purchased" else "Not purchased",
        tint = if (isPurchased) Color(0xFF4CAF50) else Color.Gray,
        modifier = modifier.size(20.dp)
    )
}

@Composable
private fun RowScope.MaterialDetails(
    material: Material,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.weight(1f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = material.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            
            Text(
                text = "Qty: ${material.quantity}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = "$${material.price}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
        
        if (material.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = material.description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PdfMaterialItemPreview() {
    ConstructionMaterialTrackTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PdfMaterialItem(
                material = Material(
                    id = "1",
                    name = "Cement Bags",
                    quantity = "50",
                    price = "250.00",
                    description = "High quality Portland cement for construction",
                    isPurchased = true
                )
            )
            
            PdfMaterialItem(
                material = Material(
                    id = "2",
                    name = "Steel Rods",
                    quantity = "100",
                    price = "1500.00",
                    description = "",
                    isPurchased = false
                )
            )
            
            PdfMaterialItem(
                material = Material(
                    id = "3",
                    name = "Bricks",
                    quantity = "5000",
                    price = "800.00",
                    description = "Red clay bricks for wall construction with excellent durability",
                    isPurchased = true
                )
            )
        }
    }
}
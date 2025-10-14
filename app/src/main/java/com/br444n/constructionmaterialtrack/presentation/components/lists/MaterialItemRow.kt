package com.br444n.constructionmaterialtrack.presentation.components.lists

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.domain.model.Material
import com.br444n.constructionmaterialtrack.ui.theme.Black
import com.br444n.constructionmaterialtrack.ui.theme.BlueDark
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme
import com.br444n.constructionmaterialtrack.ui.theme.TextSecondary

@Composable
fun MaterialItemRow(
    material: Material,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor =  BluePrimary.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = material.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = "Qty: ${material.quantity}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Price: $${material.price}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                if (material.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = material.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            Checkbox(
                checked = material.isPurchased,
                colors = CheckboxDefaults.colors(
                    checkedColor = BlueDark,
                    uncheckedColor = BlueDark,
                    checkmarkColor = Black
                ),
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MaterialItemRowPreview() {
    ConstructionMaterialTrackTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MaterialItemRow(
                material = Material(
                    id = "1",
                    name = "Concrete Blocks",
                    quantity = "100",
                    price = "1500.00",
                    description = "High-quality concrete blocks for foundation work",
                    isPurchased = false
                ),
                onCheckedChange = {}
            )
            
            MaterialItemRow(
                material = Material(
                    id = "2",
                    name = "Steel Rebar",
                    quantity = "50",
                    price = "800.00",
                    description = "",
                    isPurchased = true
                ),
                onCheckedChange = {}
            )
        }
    }
}
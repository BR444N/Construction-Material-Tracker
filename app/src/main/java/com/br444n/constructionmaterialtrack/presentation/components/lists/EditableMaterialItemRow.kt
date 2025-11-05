package com.br444n.constructionmaterialtrack.presentation.components.lists

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.domain.model.Material
import com.br444n.constructionmaterialtrack.ui.theme.BlueDark
import com.br444n.constructionmaterialtrack.ui.theme.BlueLight
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

@Composable
fun EditableMaterialItemRow(
    material: Material,
    onCheckedChange: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Colores base y oscuro para el efecto 3D basados en el estado isPurchased
    val baseColor = if (material.isPurchased) {
        BluePrimary
    } else {
        BlueLight
    }
    val darkerColor = if (material.isPurchased) {
        BlueLight
    } else {
        BluePrimary
    }
    
    // Animaciones de compresión y desplazamiento
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        label = "scale"
    )
    val darkPartHeight by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 6.dp,
        label = "darkPart"
    )
    
    // Calculamos la altura dinámica basada en el contenido
    val hasDescription = material.description.isNotBlank()
    val cardHeight = if (hasDescription) 120.dp else 88.dp
    
    Box(
        modifier = modifier
            .scale(scale)
            .height(cardHeight)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(darkerColor),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight - darkPartHeight)
                .clip(RoundedCornerShape(8.dp))
                .background(baseColor)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { onCheckedChange(!material.isPurchased) }
                ),
            contentAlignment = Alignment.Center
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
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        Text(
                            text = "${material.quantity} ${material.unit}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Price: ${material.price}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (material.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = material.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                
                // Edit button
                IconButton(
                    onClick = onEditClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit material",
                        tint = BlueDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Checkbox(
                    checked = material.isPurchased,
                    colors = CheckboxDefaults.colors(
                        checkedColor = BlueDark,
                        uncheckedColor = BlueDark,
                        checkmarkColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    onCheckedChange = onCheckedChange
                )
            }
        }
    }
}
@Preview(showBackground = false)
@Composable
private fun EditableMaterialItemRowPreview() {
    ConstructionMaterialTrackTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EditableMaterialItemRow(
                material = Material(
                    id = "1",
                    name = "Concrete Blocks",
                    quantity = "100",
                    unit = "pcs",
                    price = "1500.00",
                    description = "High-quality concrete blocks for foundation work",
                    isPurchased = false
                ),
                onCheckedChange = {},
                onEditClick = {}
            )
            
            EditableMaterialItemRow(
                material = Material(
                    id = "2",
                    name = "Steel Rebar",
                    quantity = "50",
                    unit = "m",
                    price = "800.00",
                    description = "",
                    isPurchased = true
                ),
                onCheckedChange = {},
                onEditClick = {}
            )
        }
    }
}
package com.br444n.constructionmaterialtrack.presentation.components.lists

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun MaterialItemRow(
    material: Material,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Creamos una ÚNICA fuente de interacción.
    val interactionSource = remember { MutableInteractionSource() }
    
    // 2. Usamos collectIsPressedAsState para obtener un 'State<Boolean>' de forma reactiva.
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
    
    // Animaciones de compresión y desplazamiento que ahora dependen de 'isPressed'.
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        label = "scale"
    )
    val darkPartHeight by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 6.dp, // El "borde" oscuro se hace más pequeño al presionar
        label = "darkPart"
    )
    
    // Calculamos la altura dinámica basada en el contenido
    val hasDescription = material.description.isNotBlank()
    val cardHeight = if (hasDescription) 120.dp else 88.dp
    
    Box(
        modifier = modifier
            .scale(scale) // La escala se aplica a to-do el card
            .height(cardHeight) // Altura dinámica para el card
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)) // Redondeo consistente
            .background(darkerColor), // El fondo general es el color oscuro
        contentAlignment = Alignment.TopCenter
    ) {
        // Esta es la parte superior y visible del card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                // La altura de la parte superior disminuye al presionar, revelando menos del fondo oscuro
                .height(cardHeight - darkPartHeight)
                .clip(RoundedCornerShape(8.dp))
                .background(baseColor)
                // 3. Aplicamos el modificador clickable AQUÍ, pasándole el interactionSource
                .clickable(
                    interactionSource = interactionSource,
                    indication = null, // Elimina el efecto de onda (ripple)
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
                    unit = "pcs",
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
                    unit = "m",
                    price = "800.00",
                    description = "",
                    isPurchased = true
                ),
                onCheckedChange = {}
            )
        }
    }
}
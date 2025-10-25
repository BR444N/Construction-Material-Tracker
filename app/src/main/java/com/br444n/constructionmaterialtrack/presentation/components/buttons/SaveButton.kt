package com.br444n.constructionmaterialtrack.presentation.components.buttons

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br444n.constructionmaterialtrack.ui.theme.BlueLight
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

/**
 * Configuration data class for SaveButton
 */
data class SaveButtonConfig(
    val enabled: Boolean = true,
    val isLoading: Boolean = false,
    val baseColor: Color = BlueLight,
    val darkerColor: Color = BluePrimary,
    val textColor: Color = Color.Unspecified // Will use MaterialTheme.colorScheme.onSurface if Unspecified
)

@Composable
fun SaveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    config: SaveButtonConfig = SaveButtonConfig()
) {
    val actualTextColor = if (config.textColor == Color.Unspecified) {
        MaterialTheme.colorScheme.onSurface
    } else {
        config.textColor
    }
    // 1. Creamos una ÚNICA fuente de interacción.
    val interactionSource = remember { MutableInteractionSource() }
    
    // 2. Usamos collectIsPressedAsState para obtener un 'State<Boolean>' de forma reactiva.
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Animaciones de compresión y desplazamiento que ahora dependen de 'isPressed'.
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        label = "scale"
    )
    val darkPartHeight by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 6.dp, // El "borde" oscuro se hace más pequeño al presionar
        label = "darkPart"
    )
    
    Box(
        modifier = modifier
            .scale(scale) // La escala se aplica a to-do el botón
            .height(56.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)) // Redondeo consistente
            .background(config.darkerColor), // El fondo general es el color oscuro
        contentAlignment = Alignment.TopCenter
    ) {
        // Esta es la parte superior y visible del botón
        Box(
            modifier = Modifier
                .fillMaxWidth()
                // La altura de la parte superior disminuye al presionar, revelando menos del fondo oscuro
                .height(56.dp - darkPartHeight)
                .clip(RoundedCornerShape(16.dp))
                .background(config.baseColor)
                // 3. Aplicamos el modificador clickable AQUÍ, pasándole el interactionSource
                .clickable(
                    interactionSource = interactionSource,
                    indication = null, // Elimina el efecto de onda (ripple)
                    enabled = config.enabled && !config.isLoading,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            if (config.isLoading) {
                CircularProgressIndicator(
                    color = actualTextColor,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = text,
                    color = actualTextColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SaveButtonPreview() {
    ConstructionMaterialTrackTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SaveButton(
                text = "Save Material",
                onClick = {},
                config = SaveButtonConfig(
                    enabled = true,
                    isLoading = false
                )
            )
            
            SaveButton(
                text = "Save Project",
                onClick = {},
                config = SaveButtonConfig(
                    enabled = true,
                    isLoading = false
                )
            )
            
            SaveButton(
                text = "Saving...",
                onClick = {},
                config = SaveButtonConfig(
                    enabled = true,
                    isLoading = true
                )
            )
        }
    }
}
package com.br444n.constructionmaterialtrack.presentation.components.buttons

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br444n.constructionmaterialtrack.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import com.br444n.constructionmaterialtrack.ui.theme.BlueLight
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

/**
 * Configuration data class for SecondaryButton
 */
data class SecondaryButtonConfig(
    val enabled: Boolean = true,
    val baseColor: Color = BluePrimary, // Cambiado para coincidir con SaveButton
    val darkerColor: Color = BlueLight, // Cambiado para coincidir con SaveButton
    val textColor: Color = Color.Unspecified, // Will use MaterialTheme.colorScheme.onSurface if Unspecified
    val icon: Painter? = null,
    val vectorIcon: ImageVector? = null
)

/**
 * Data class to hold dynamic colors for SecondaryButton
 */
private data class SecondaryButtonColors(
    val baseColor: Color,
    val darkerColor: Color,
    val textColor: Color
)

/**
 * Calculate dynamic colors based on button state
 */
private fun calculateSecondaryButtonColors(
    config: SecondaryButtonConfig,
    actualTextColor: Color
): SecondaryButtonColors {
    val isActive = config.enabled
    
    return SecondaryButtonColors(
        baseColor = if (isActive) config.baseColor else config.darkerColor.copy(alpha = 0.5f),
        darkerColor = if (isActive) config.darkerColor else config.darkerColor.copy(alpha = 0.5f),
        textColor = if (isActive) actualTextColor else actualTextColor.copy(alpha = 0.6f)
    )
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    config: SecondaryButtonConfig = SecondaryButtonConfig()
) {
    val actualTextColor = if (config.textColor == Color.Unspecified) {
        MaterialTheme.colorScheme.onSurface
    } else {
        config.textColor
    }
    
    // Calcular colores dinámicos basados en el estado enabled
    val colors = calculateSecondaryButtonColors(config, actualTextColor)
    
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
            .background(colors.darkerColor), // El fondo general es el color oscuro
        contentAlignment = Alignment.TopCenter
    ) {
        // Esta es la parte superior y visible del botón
        Box(
            modifier = Modifier
                .fillMaxWidth()
                // La altura de la parte superior disminuye al presionar, revelando menos del fondo oscuro
                .height(56.dp - darkPartHeight)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.baseColor)
                // 3. Aplicamos el modificador clickable AQUÍ, pasándole el interactionSource
                .clickable(
                    interactionSource = interactionSource,
                    indication = null, // Elimina el efecto de onda (ripple)
                    enabled = config.enabled,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                when {
                    config.icon != null -> {
                        Image(
                            painter = config.icon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    config.vectorIcon != null -> {
                        Icon(
                            imageVector = config.vectorIcon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = colors.textColor
                        )
                    }
                }
                Text(
                    text = text,
                    color = colors.textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SecondaryButtonPreview() {
    ConstructionMaterialTrackTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SecondaryButton(
                text = "Add Materials",
                onClick = {},
                config = SecondaryButtonConfig(
                    enabled = true,
                    vectorIcon = Icons.Default.Add
                )
            )
            
            SecondaryButton(
                text = "Export to PDF",
                onClick = {},
                config = SecondaryButtonConfig(
                    enabled = true,
                    icon = painterResource(id = R.drawable.export_pdf)
                )
            )
            
            SecondaryButton(
                text = "Add Materials (Disabled)",
                onClick = {},
                config = SecondaryButtonConfig(
                    enabled = false,
                    vectorIcon = Icons.Default.Add
                )
            )
        }
    }
}
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
    val baseColor: Color = BluePrimary,
    val darkerColor: Color = BlueLight,
    val textColor: Color = Color.Unspecified // Will use MaterialTheme.colorScheme.onSurface if Unspecified
)

/**
 * Data class to hold dynamic colors for SaveButton
 */
private data class SaveButtonColors(
    val baseColor: Color,
    val darkerColor: Color,
    val textColor: Color
)

/**
 * Calculate dynamic colors based on button state
 */
private fun calculateDynamicColors(
    config: SaveButtonConfig,
    actualTextColor: Color
): SaveButtonColors {
    val isActive = config.enabled && !config.isLoading
    
    return SaveButtonColors(
        baseColor = if (isActive) config.baseColor else config.darkerColor.copy(alpha = 0.5f),
        darkerColor = if (isActive) config.darkerColor else config.darkerColor.copy(alpha = 0.5f),
        textColor = if (isActive) actualTextColor else actualTextColor.copy(alpha = 0.6f)
    )
}

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
    
    val colors = calculateDynamicColors(config, actualTextColor)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    SaveButtonLayout(
        text = text,
        onClick = onClick,
        modifier = modifier,
        config = config,
        colors = colors,
        interactionSource = interactionSource,
        isPressed = isPressed
    )
}

@Composable
private fun SaveButtonLayout(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier,
    config: SaveButtonConfig,
    colors: SaveButtonColors,
    interactionSource: MutableInteractionSource,
    isPressed: Boolean
) {
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        label = "scale"
    )
    val darkPartHeight by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 6.dp,
        label = "darkPart"
    )
    
    Box(
        modifier = modifier
            .scale(scale)
            .height(56.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.darkerColor),
        contentAlignment = Alignment.TopCenter
    ) {
        SaveButtonSurface(
            text = text,
            onClick = onClick,
            config = config,
            colors = colors,
            interactionSource = interactionSource,
            darkPartHeight = darkPartHeight
        )
    }
}

@Composable
private fun SaveButtonSurface(
    text: String,
    onClick: () -> Unit,
    config: SaveButtonConfig,
    colors: SaveButtonColors,
    interactionSource: MutableInteractionSource,
    darkPartHeight: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp - darkPartHeight)
            .clip(RoundedCornerShape(16.dp))
            .background(colors.baseColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = config.enabled && !config.isLoading,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        SaveButtonContent(
            text = text,
            config = config,
            textColor = colors.textColor
        )
    }
}

@Composable
private fun SaveButtonContent(
    text: String,
    config: SaveButtonConfig,
    textColor: Color
) {
    if (config.isLoading) {
        CircularProgressIndicator(
            color = textColor,
            strokeWidth = 3.dp,
            modifier = Modifier.size(24.dp)
        )
    } else {
        Text(
            text = text,
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
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
            
            SaveButton(
                text = "Save Project (Disabled)",
                onClick = {},
                config = SaveButtonConfig(
                    enabled = false,
                    isLoading = false
                )
            )
        }
    }
}
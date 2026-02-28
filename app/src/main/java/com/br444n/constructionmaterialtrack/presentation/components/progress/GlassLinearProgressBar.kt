package com.br444n.constructionmaterialtrack.presentation.components.progress

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.luminance
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme
import com.br444n.constructionmaterialtrack.ui.theme.GlassReflection
import com.br444n.constructionmaterialtrack.ui.theme.GlassTrack
import kotlin.math.roundToInt

@Composable
fun GlassLinearProgressBar(
    progress: Float,
    totalMaterials: Int,
    completedMaterials: Int,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 24.dp
) {
    val density = LocalDensity.current
    // Detectar modo oscuro desde el esquema de colores actual (funciona también en previews)
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f

    // Animación suave del progreso
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800),
        label = "progress_animation"
    )

    // Colores adaptativos según el tema
    // En modo oscuro: tubo semi-transparente. En modo claro: negro/oscuro (onSurface) en vez de azul
    val trackColor = if (isDarkTheme) GlassTrack else MaterialTheme.colorScheme.onSurface
    // En modo oscuro: líquido azul primario. En modo claro: líquido negro/oscuro para contrastar con el tubo azul brillante
    val liquidColor = if (isDarkTheme) BluePrimary else MaterialTheme.colorScheme.onSurface
    val reflectionColor = if (isDarkTheme) GlassReflection else Color.White.copy(alpha = 0.45f)
    // En modo oscuro: borde blanco sutil. En modo claro: borde del mismo color negro pero más transparente
    val borderColor = if (isDarkTheme) Color.White.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Progress info text
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Project Progress",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "$completedMaterials/$totalMaterials materials",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }

        // Glass progress bar
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(height / 2))
        ) {
            drawGlassProgressBar(
                progress = animatedProgress,
                density = density,
                trackColor = trackColor,
                liquidColor = liquidColor,
                reflectionColor = reflectionColor,
                borderColor = borderColor
            )
        }

        // Percentage text
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            val percentage = (progress * 100).roundToInt()
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = when (percentage) {
                    0 -> MaterialTheme.colorScheme.onSurfaceVariant
                    100 -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.primary
                },
                fontSize = 14.sp
            )
        }
    }
}

private fun DrawScope.drawGlassProgressBar(
    progress: Float,
    density: androidx.compose.ui.unit.Density,
    trackColor: Color,
    liquidColor: Color,
    reflectionColor: Color,
    borderColor: Color
) {
    val width = size.width
    val height = size.height
    val cornerRadius = height / 2f
    val liquidPadding = height * 0.25f // 25% padding interno

    // 1. Dibujar el contenedor de cristal (tubo)
    drawGlassContainer(width, height, cornerRadius, density, trackColor)

    // 2. Dibujar el líquido interno con efecto glow
    if (progress > 0f) {
        drawLiquidProgress(width, height, liquidPadding, progress, density, liquidColor)
    }

    // 3. Dibujar reflejo superior
    drawSpecularHighlight(width, height, cornerRadius, reflectionColor)

    // 4. Dibujar bordes de definición
    drawDepthBorders(width, height, cornerRadius, density, borderColor)
}

private fun DrawScope.drawGlassContainer(
    width: Float,
    height: Float,
    cornerRadius: Float,
    density: androidx.compose.ui.unit.Density,
    trackColor: Color
) {
    val strokeWidth = with(density) { 4.dp.toPx() }

    drawRoundRect(
        color = trackColor,
        size = androidx.compose.ui.geometry.Size(width, height),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius),
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round
        )
    )
}

private fun DrawScope.drawLiquidProgress(
    width: Float,
    height: Float,
    padding: Float,
    progress: Float,
    density: androidx.compose.ui.unit.Density,
    liquidColor: Color
) {
    val liquidHeight = height - (padding * 2)
    val liquidWidth = (width - (padding * 2)) * progress
    val liquidCornerRadius = liquidHeight / 2f

        // Usar nativeCanvas para el efecto glow
        val paint = android.graphics.Paint().apply {
            color = liquidColor.toArgb()
            isAntiAlias = true
            // Efecto glow/sombra
            setShadowLayer(
                with(density) { 12.dp.toPx() }, // radius
                0f, // dx
                0f, // dy
                liquidColor.copy(alpha = 0.7f).toArgb() // shadowColor
            )
        }

    drawContext.canvas.nativeCanvas.drawRoundRect(
        padding, // left
        padding, // top
        padding + liquidWidth, // right
        padding + liquidHeight, // bottom
        liquidCornerRadius, // rx
        liquidCornerRadius, // ry
        paint
    )
}

private fun DrawScope.drawSpecularHighlight(
    width: Float,
    height: Float,
    cornerRadius: Float,
    reflectionColor: Color
) {
    val highlightHeight = height * 0.4f // 40% de la altura total
    val highlightPadding = height * 0.1f // 10% padding desde arriba

    val path = Path().apply {
        addRoundRect(
            androidx.compose.ui.geometry.RoundRect(
                left = highlightPadding,
                top = highlightPadding,
                right = width - highlightPadding,
                bottom = highlightPadding + highlightHeight,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius * 0.8f)
            )
        )
    }

    drawPath(
        path = path,
        color = reflectionColor
    )
}

private fun DrawScope.drawDepthBorders(
    width: Float,
    height: Float,
    cornerRadius: Float,
    density: androidx.compose.ui.unit.Density,
    borderColor: Color
) {
    val strokeWidth = with(density) { 2.dp.toPx() }

    drawRoundRect(
        color = borderColor,
        size = androidx.compose.ui.geometry.Size(width, height),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius),
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round
        )
    )
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Light Mode")
@Composable
private fun GlassLinearProgressBarLightPreview() {
    ConstructionMaterialTrackTheme(darkTheme = false) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassLinearProgressBar(
                progress = 0.0f,
                totalMaterials = 10,
                completedMaterials = 0
            )

            GlassLinearProgressBar(
                progress = 0.2f,
                totalMaterials = 10,
                completedMaterials = 2
            )

            GlassLinearProgressBar(
                progress = 0.65f,
                totalMaterials = 20,
                completedMaterials = 13
            )

            GlassLinearProgressBar(
                progress = 1.0f,
                totalMaterials = 5,
                completedMaterials = 5
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212, name = "Dark Mode")
@Composable
private fun GlassLinearProgressBarDarkPreview() {
    ConstructionMaterialTrackTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassLinearProgressBar(
                progress = 0.0f,
                totalMaterials = 10,
                completedMaterials = 0
            )

            GlassLinearProgressBar(
                progress = 0.2f,
                totalMaterials = 10,
                completedMaterials = 2
            )

            GlassLinearProgressBar(
                progress = 0.65f,
                totalMaterials = 20,
                completedMaterials = 13
            )

            GlassLinearProgressBar(
                progress = 1.0f,
                totalMaterials = 5,
                completedMaterials = 5
            )
        }
    }
}
package com.br444n.constructionmaterialtrack.presentation.components.cards

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.ui.res.stringResource
import com.br444n.constructionmaterialtrack.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.domain.model.Project
import com.br444n.constructionmaterialtrack.ui.theme.BlueDark
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme
import com.br444n.constructionmaterialtrack.presentation.components.images.ProjectImageDisplay
import com.br444n.constructionmaterialtrack.ui.theme.BlueLight


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProjectCard(
    modifier: Modifier = Modifier,
    project: Project,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false
) {
    // 1. Creamos una ÚNICA fuente de interacción.
    val interactionSource = remember { MutableInteractionSource() }
    
    // 2. Usamos collectIsPressedAsState para obtener un 'State<Boolean>' de forma reactiva.
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Colores base y oscuro para el efecto 3D
    val baseColor = if (isSelected) {
        BluePrimary
    } else {
        BlueLight
    }
    val darkerColor = if (isSelected) {
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
    
    Box(
        modifier = modifier
            .scale(scale) // La escala se aplica a to-do el card
            .height(88.dp) // Altura fija para el card
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)) // Redondeo consistente
            .background(darkerColor), // El fondo general es el color oscuro
        contentAlignment = Alignment.TopCenter
    ) {
        // Esta es la parte superior y visible del card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                // La altura de la parte superior disminuye al presionar, revelando menos del fondo oscuro
                .height(88.dp - darkPartHeight)
                .clip(RoundedCornerShape(12.dp))
                .background(baseColor)
                // 3. Aplicamos el modificador combinedClickable AQUÍ, pasándole el interactionSource
                .combinedClickable(
                    interactionSource = interactionSource,
                    indication = null, // Elimina el efecto de onda (ripple)
                    onClick = onClick,
                    onLongClick = onLongClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Selection Checkbox (only visible in selection mode)
                if (isSelectionMode) {
                    Checkbox(
                        checked = isSelected,
                        colors = CheckboxDefaults.colors(
                            checkedColor = BlueDark,
                            uncheckedColor = BlueDark,
                            checkmarkColor = MaterialTheme.colorScheme.onSurface
                        ),
                        onCheckedChange = { onClick() }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                
                // Project Image
                ProjectImageDisplay(
                    imageUri = project.imageUri,
                    imageRes = project.imageRes,
                    modifier = Modifier.size(60.dp),
                    contentDescription = stringResource(R.string.project_image)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Project Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = project.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = project.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProjectCardPreview() {
    ConstructionMaterialTrackTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProjectCard(
                project = Project(
                    id = "1",
                    name = "Modern House",
                    description = "A contemporary residential project with sustainable materials and modern design elements."
                ),
                onClick = {}
            )
            
            ProjectCard(
                project = Project(
                    id = "2",
                    name = "Office Building",
                    description = "Commercial project with modern facilities."
                ),
                onClick = {},
                isSelectionMode = true,
                isSelected = true
            )
        }
    }
}
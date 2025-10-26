package com.br444n.constructionmaterialtrack.presentation.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.br444n.constructionmaterialtrack.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br444n.constructionmaterialtrack.domain.model.Project
import com.br444n.constructionmaterialtrack.ui.theme.BlueLight
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.presentation.components.images.ProjectImageDisplay

@Composable
fun ProjectInfoCard(
    project: Project,
    modifier: Modifier = Modifier
) {
    // Colores fijos para el efecto 3D estático completo
    val baseColor = BlueLight // Color de fondo del card
    val borderColor = BluePrimary // Color del borde de profundidad
    val borderWidth = 6.dp // Ancho del borde en todos los lados
    
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        // Capa de fondo (borde de profundidad) - ligeramente desplazada
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight() // Cambiado a wrapContent para adaptarse al contenido
                .offset(x = borderWidth, y = borderWidth) // Desplazamiento para crear efecto de sombra
                .clip(RoundedCornerShape(12.dp))
                .background(borderColor)
        ) {
            // Contenido invisible para dar forma a la sombra
            ProjectInfoCardContent(
                project = project,
                isVisible = false
            )
        }
        
        // Capa principal del card (contenido)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight() // Cambiado a wrapContent
                .clip(RoundedCornerShape(12.dp))
                .background(baseColor)
        ) {
            ProjectInfoCardContent(
                project = project,
                isVisible = true
            )
        }
    }
}

@Composable
private fun ProjectInfoCardContent(
    project: Project,
    isVisible: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Project Image (más pequeña para dar espacio a la descripción)
        if (isVisible) {
            ProjectImageDisplay(
                imageUri = project.imageUri,
                imageRes = project.imageRes,
                contentDescription = stringResource(R.string.project_image),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp) // Reducida aún más para dar más espacio al texto
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            // Spacer invisible para la sombra
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Project Info
        ProjectInfoText(
            project = project,
            isVisible = isVisible
        )
    }
}

@Composable
private fun ProjectInfoText(
    project: Project,
    isVisible: Boolean = true
) {
    if (isVisible) {
        // Título del proyecto
        Text(
            text = project.name,
            style = MaterialTheme.typography.titleLarge, // Cambiado a titleLarge para mejor legibilidad
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2, // Permitir 2 líneas para nombres largos
            overflow = TextOverflow.Ellipsis
        )
        
        // Descripción del proyecto
        if (project.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = project.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp, // Mejor espaciado entre líneas
                // Sin maxLines para permitir que se muestre to-do el texto
                overflow = TextOverflow.Visible
            )
        }
    } else {
        // Contenido invisible para la sombra - mismo espacio pero transparente
        Text(
            text = project.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = androidx.compose.ui.graphics.Color.Transparent,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        
        if (project.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = project.description,
                style = MaterialTheme.typography.bodyMedium,
                color = androidx.compose.ui.graphics.Color.Transparent,
                lineHeight = 20.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProjInfCard(modifier: Modifier = Modifier) {
    ProjectInfoCard(
        project = Project(
            id = "1",
            name = "Sample Project",
            description = "This is a sample project description to showcase the ProjectInfoCard component.",
            imageUri = null,
            imageRes = null
        ),
        modifier = modifier.padding(16.dp)
    )
}
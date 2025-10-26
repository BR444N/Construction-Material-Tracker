package com.br444n.constructionmaterialtrack.presentation.components.states

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.R
import com.br444n.constructionmaterialtrack.ui.theme.BlueLight
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

@Composable
fun EmptyMaterialsState(
    modifier: Modifier = Modifier,
    title: String = "No materials yet",
    message: String = "Add materials to track your project inventory and progress.",
    image: Painter = painterResource(id = R.drawable.empty_state),
    imageSize: androidx.compose.ui.unit.Dp = 120.dp
) {
    // Colores fijos para el efecto 3D estático completo (siguiendo el patrón del ProjectInfoCard)
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
                .wrapContentHeight() // Se adapta al contenido
                .offset(x = borderWidth, y = borderWidth) // Desplazamiento para crear efecto de sombra
                .clip(RoundedCornerShape(12.dp))
                .background(borderColor)
        ) {
            // Contenido invisible para dar forma a la sombra
            EmptyMaterialsStateContent(
                title = title,
                message = message,
                image = image,
                imageSize = imageSize,
                isVisible = false
            )
        }
        
        // Capa principal del card (contenido)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight() // Se adapta al contenido
                .clip(RoundedCornerShape(12.dp))
                .background(baseColor)
        ) {
            EmptyMaterialsStateContent(
                title = title,
                message = message,
                image = image,
                imageSize = imageSize,
                isVisible = true
            )
        }
    }
}

@Composable
private fun EmptyMaterialsStateContent(
    title: String,
    message: String,
    image: Painter,
    imageSize: androidx.compose.ui.unit.Dp,
    isVisible: Boolean = true
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isVisible) {
            // Título visible
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            // Imagen visible
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier.size(imageSize)
            )
            
            // Mensaje visible
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        } else {
            // Contenido invisible para la sombra - mismo espacio pero transparente
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = androidx.compose.ui.graphics.Color.Transparent,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(imageSize))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = androidx.compose.ui.graphics.Color.Transparent,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyMaterialsStatePreview() {
    ConstructionMaterialTrackTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EmptyMaterialsState()
            
            EmptyMaterialsState(
                title = "No tools added",
                message = "Start adding tools to keep track of your equipment.",
                image = painterResource(id = R.drawable.empty_state),
                imageSize = 100.dp
            )
        }
    }
}
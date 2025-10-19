package com.br444n.constructionmaterialtrack.presentation.components.states

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

@Composable
fun EmptyState(
    message: String,
    modifier: Modifier = Modifier,
    title: String? = null,
    icon: ImageVector? = null,
    drawableRes: Int? = null,
    imageSize: androidx.compose.ui.unit.Dp = 120.dp,
    showCard: Boolean = true,
    cardColor: androidx.compose.ui.graphics.Color? = BluePrimary.copy(0.3f)
) {
    val content = @Composable {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Título primero
            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Imagen después del título
            when {
                drawableRes != null -> {
                    Image(
                        painter = painterResource(id = drawableRes),
                        contentDescription = null,
                        modifier = Modifier.size(imageSize)
                    )
                }
                icon != null -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Resaltar la frase "Click the + button"
            val annotatedMessage = buildAnnotatedString {
                val targetPhrase = "Click the + button"
                val startIndex = message.indexOf(targetPhrase)
                
                if (startIndex != -1) {
                    // Texto antes de la frase
                    append(message.substring(0, startIndex))
                    
                    // Frase resaltada
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        )
                    ) {
                        append(targetPhrase)
                    }
                    
                    // Texto después de la frase
                    append(message.substring(startIndex + targetPhrase.length))
                } else {
                    // Si no encuentra la frase, mostrar el mensaje normal
                    append(message)
                }
            }
            
            Text(
                text = annotatedMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
    
    if (showCard) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardColor ?: androidx.compose.ui.graphics.Color.Transparent
            )
        ) {
            content()
        }
    } else {
        Box(modifier = modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    ConstructionMaterialTrackTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Con Card y color por defecto
            EmptyState(
                message = "No materials added yet"
            )
            
            // Con Card y color personalizado
            EmptyState(
                title = "No projects found",
                message = "Click the + button to add your first project.",
                icon = Icons.Default.Inventory
            )
        }
    }
}
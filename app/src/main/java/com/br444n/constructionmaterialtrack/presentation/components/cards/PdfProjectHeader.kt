package com.br444n.constructionmaterialtrack.presentation.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.br444n.constructionmaterialtrack.R
import com.br444n.constructionmaterialtrack.domain.model.Project
import com.br444n.constructionmaterialtrack.ui.theme.BlueLight
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

@Composable
fun PdfProjectHeader(
    project: Project,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Project Image (Circular)
        ProjectImageCircle(project = project)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Project Name (Bold, Centered)
        Text(
            text = project.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Project Description (Normal, Centered)
        if (project.description.isNotBlank()) {
            Text(
                text = project.description,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProjectImageCircle(
    project: Project,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape)
            .border(3.dp, BlueLight.copy(0.4f), CircleShape)
            .background(BlueLight.copy(0.3f)),
        contentAlignment = Alignment.Center
    ) {
        when {
            project.imageUri != null -> {
                AsyncImage(
                    model = project.imageUri.toUri(),
                    contentDescription = "Project Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            project.imageRes != null -> {
                Image(
                    painter = painterResource(id = project.imageRes),
                    contentDescription = "Project Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            else -> {
                // Use vector drawable directly to avoid PNG loading issues
                Icon(
                    painter = painterResource(id = R.drawable.pose_def_project),
                    contentDescription = "Default Project Image",
                    modifier = Modifier.size(60.dp),
                    tint = Unspecified
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PdfProjectHeaderPreview() {
    ConstructionMaterialTrackTheme {
        PdfProjectHeader(
            project = Project(
                id = "1",
                name = "Modern House Construction",
                description = "A beautiful modern house with contemporary design and sustainable materials.",
                imageUri = null,
                imageRes = null
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "With Long Description")
@Composable
private fun PdfProjectHeaderLongDescriptionPreview() {
    ConstructionMaterialTrackTheme {
        PdfProjectHeader(
            project = Project(
                id = "1",
                name = "Luxury Villa Project",
                description = "An extensive luxury villa project featuring state-of-the-art amenities, premium materials, and innovative architectural design that combines modern aesthetics with traditional craftsmanship.",
                imageUri = null,
                imageRes = null
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
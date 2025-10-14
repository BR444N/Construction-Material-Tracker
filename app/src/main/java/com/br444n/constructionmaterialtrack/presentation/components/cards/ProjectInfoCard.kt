package com.br444n.constructionmaterialtrack.presentation.components.cards

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.domain.model.Project
import com.br444n.constructionmaterialtrack.ui.theme.Black
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.TextSecondary
import com.br444n.constructionmaterialtrack.presentation.components.images.ProjectImageDisplay

@Composable
fun ProjectInfoCard(
    project: Project,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = BluePrimary.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Project Image
            ProjectImageDisplay(
                imageUri = project.imageUri,
                imageRes = project.imageRes,
                contentDescription = "Project Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Project Info
            Text(
                text = project.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Black
            )
            
            if (project.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
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
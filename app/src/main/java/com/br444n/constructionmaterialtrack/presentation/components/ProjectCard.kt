package com.br444n.constructionmaterialtrack.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.domain.model.Project
import com.br444n.constructionmaterialtrack.ui.theme.Black
import com.br444n.constructionmaterialtrack.ui.theme.BlueDark
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme
import com.br444n.constructionmaterialtrack.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProjectCard(
    modifier: Modifier = Modifier,
    project: Project,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {},
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                BluePrimary.copy(alpha = 0.5f)
            } else {
                BluePrimary.copy(alpha = 0.4f)
            }
        )
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
                        checkmarkColor = Black
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
                contentDescription = "Project Image"
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
                    color = Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
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
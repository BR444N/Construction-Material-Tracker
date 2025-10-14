package com.br444n.constructionmaterialtrack.presentation.components.images

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.br444n.constructionmaterialtrack.R

@Composable
fun ProjectImageDisplay(
    modifier: Modifier = Modifier,
    imageUri: String? = null,
    imageRes: Int? = null,
    contentDescription: String = "Project Image"
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        when {
            imageUri != null -> {
                AsyncImage(
                    model = Uri.parse(imageUri),
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            imageRes != null -> {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = contentDescription,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            else -> {
                Image(
                    painter = painterResource(id = R.drawable.pose_def_project),
                    contentDescription = "Default $contentDescription",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
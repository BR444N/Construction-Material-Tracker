package com.br444n.constructionmaterialtrack.presentation.components.states

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br444n.constructionmaterialtrack.R
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

@Composable
fun EmptyState(
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val title = stringResource(R.string.no_projects)
    val message = stringResource(R.string.no_projects_message)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Image(
                painter = painterResource(id = R.drawable.state_image),
                contentDescription = null,
                modifier = Modifier.size(350.dp)
            )
            
            val annotatedMessage = buildAnnotatedString {
                val targetPhrase = "Click the + button"
                val startIndex = message.indexOf(targetPhrase)
                
                if (startIndex != -1) {
                    append(message.take(startIndex))
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        )
                    ) {
                        append(targetPhrase)
                    }
                    append(message.substring(startIndex + targetPhrase.length))
                } else {
                    append(message)
                }
            }
            
            Text(
                text = annotatedMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyStatePreview() {
    ConstructionMaterialTrackTheme {
        EmptyState()
    }
}
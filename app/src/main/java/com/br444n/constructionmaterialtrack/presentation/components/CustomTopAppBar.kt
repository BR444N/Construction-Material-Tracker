package com.br444n.constructionmaterialtrack.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    actions: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Medium
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        actions = { actions() }
    )
}

@Preview(showBackground = true)
@Composable
private fun CustomTopAppBarPreview() {
    ConstructionMaterialTrackTheme {
        CustomTopAppBar(
            title = "Add Material",
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomTopAppBarWithActionsPreview() {
    ConstructionMaterialTrackTheme {
        CustomTopAppBar(
            title = "Edit Project",
            onBackClick = {},
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Save"
                    )
                }
            }
        )
    }
}
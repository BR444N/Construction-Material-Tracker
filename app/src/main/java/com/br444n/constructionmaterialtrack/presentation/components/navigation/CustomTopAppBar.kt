package com.br444n.constructionmaterialtrack.presentation.components.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import com.br444n.constructionmaterialtrack.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupPositionProvider
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    actions: @Composable () -> Unit = {}
) {
    val density = LocalDensity.current
    
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        navigationIcon = {
            Box {
                TooltipBox(
                    positionProvider = object : PopupPositionProvider {
                        override fun calculatePosition(
                            anchorBounds: IntRect,
                            windowSize: IntSize,
                            layoutDirection: LayoutDirection,
                            popupContentSize: IntSize
                        ): IntOffset {
                            val spacingPx = with(density) { 4.dp.toPx().toInt() }
                            val x = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2
                            val y = anchorBounds.bottom + spacingPx
                            val adjustedX = x.coerceIn(0, windowSize.width - popupContentSize.width)
                            val adjustedY = y.coerceAtMost(windowSize.height - popupContentSize.height)
                            return IntOffset(adjustedX, adjustedY)
                        }
                    },
                    tooltip = {
                        PlainTooltip {
                            Text(stringResource(R.string.back))
                        }
                    },
                    state = remember { TooltipState() }
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        actions = { actions() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BluePrimary
    ))
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
                        imageVector = Icons.Default.Check,
                        contentDescription = "Save"
                    )
                }
            }
        )
    }
}
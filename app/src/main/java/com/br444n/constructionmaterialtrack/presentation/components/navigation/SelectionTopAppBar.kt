package com.br444n.constructionmaterialtrack.presentation.components.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SelectAll
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
import com.br444n.constructionmaterialtrack.ui.theme.Red
import com.br444n.constructionmaterialtrack.ui.theme.RedLight


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionTopAppBar(
    selectedCount: Int,
    onExitSelection: () -> Unit,
    onSelectAll: () -> Unit,
    onDelete: () -> Unit
) {

    val density = LocalDensity.current

    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.items_selected, selectedCount),
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
                            Text(stringResource(R.string.back_selected))
                        }
                    },
                    state = remember { TooltipState() }
                ) {
                    IconButton(onClick = onExitSelection) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.back_selected),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        actions = {
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
                            Text(stringResource(R.string.select_all))
                        }
                    },
                    state = remember { TooltipState() }
                ) {
                    IconButton(onClick = onSelectAll) {
                        Icon(
                            imageVector = Icons.Default.SelectAll,
                            contentDescription = stringResource(R.string.select_all),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
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
                            Text(stringResource(R.string.delete_selected))
                        }
                    },
                    state = remember { TooltipState() }
                ) {
                    IconButton(
                        onClick = onDelete,
                        enabled = selectedCount > 0
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete_selected),
                            tint = if (selectedCount > 0) {
                                RedLight
                            } else {
                                Red
                            }
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BluePrimary
        )
    )
}

// Vista previa sin simulación de acciones
@Preview(showBackground = true)
@Composable
fun PreviewSelectionTopAppBarNoActions() {
    SelectionTopAppBar(
        selectedCount = 2,
        onExitSelection = { /* No action */ },
        onSelectAll = { /* No action */ },
        onDelete = { /* No action */ }
    )
}


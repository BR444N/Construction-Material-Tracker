package com.br444n.constructionmaterialtrack.presentation.components.buttons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    iconPainter: Painter? = null,
    enabled: Boolean = true,
    isOutlined: Boolean = false,
    colors: ButtonColors? = null,
    iconTint: Color? = null,
    textColor: Color? = null,
    preserveIconColor: Boolean = false,
    iconSize: Dp = 20.dp
) {
    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = enabled
        ) {
            // Icon (either ImageVector or Painter)
            when {
                icon != null -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                        tint = when {
                            preserveIconColor -> Color.Unspecified
                            iconTint != null -> iconTint
                            else -> LocalContentColor.current
                        }
                    )
                }
                iconPainter != null -> {
                    Icon(
                        painter = iconPainter,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                        tint = when {
                            preserveIconColor -> Color.Unspecified
                            iconTint != null -> iconTint
                            else -> LocalContentColor.current
                        }
                    )
                }
            }
            
            if (icon != null || iconPainter != null) {
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Text(
                text = text,
                modifier = Modifier.padding(vertical = 4.dp),
                color = textColor ?: LocalContentColor.current
            )
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = enabled,
            colors = colors ?: ButtonDefaults.buttonColors()
        ) {
            // Icon (either ImageVector or Painter)
            when {
                icon != null -> {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                        tint = when {
                            preserveIconColor -> Color.Unspecified
                            iconTint != null -> iconTint
                            else -> LocalContentColor.current
                        }
                    )
                }
                iconPainter != null -> {
                    Icon(
                        painter = iconPainter,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                        tint = when {
                            preserveIconColor -> Color.Unspecified
                            iconTint != null -> iconTint
                            else -> LocalContentColor.current
                        }
                    )
                }
            }
            
            if (icon != null || iconPainter != null) {
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            Text(
                text = text,
                modifier = Modifier.padding(vertical = 4.dp),
                color = textColor ?: LocalContentColor.current
            )
        }
    }
}
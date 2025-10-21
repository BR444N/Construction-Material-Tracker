package com.br444n.constructionmaterialtrack.presentation.components.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.R
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

@Composable
fun SettingsSwitch(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    val switchDescription = stringResource(
        if (checked) R.string.switch_on else R.string.switch_off
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                enabled = enabled,
                role = Role.Switch
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIcon(icon = icon, enabled = enabled)
        Spacer(modifier = Modifier.width(16.dp))
        SettingsContent(
            title = title,
            subtitle = subtitle,
            enabled = enabled,
            modifier = Modifier.weight(1f)
        )
        SettingsToggle(
            checked = checked,
            enabled = enabled,
            title = title,
            switchDescription = switchDescription
        )
    }
}

@Composable
private fun SettingsIcon(
    icon: ImageVector,
    enabled: Boolean
) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(24.dp),
        tint = getEnabledColor(
            enabledColor = MaterialTheme.colorScheme.onSurface,
            enabled = enabled
        )
    )
}

@Composable
private fun SettingsContent(
    title: String,
    subtitle: String?,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = getEnabledColor(
                enabledColor = MaterialTheme.colorScheme.onSurface,
                enabled = enabled
            )
        )
        
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = getEnabledColor(
                    enabledColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    enabled = enabled
                )
            )
        }
    }
}

@Composable
private fun SettingsToggle(
    checked: Boolean,
    enabled: Boolean,
    title: String,
    switchDescription: String
) {
    Switch(
        checked = checked,
        onCheckedChange = null, // Handled by toggleable modifier
        enabled = enabled,
        modifier = Modifier
            .semantics {
                contentDescription = "$title - $switchDescription"
            },
        colors = getSwitchColors()
    )
}

@Composable
private fun getEnabledColor(
    enabledColor: Color,
    enabled: Boolean
) = if (enabled) enabledColor else enabledColor.copy(alpha = 0.38f)

@Composable
private fun getSwitchColors() = SwitchDefaults.colors(
    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
    checkedTrackColor = BluePrimary,
    checkedBorderColor = BluePrimary,
    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
    uncheckedBorderColor = MaterialTheme.colorScheme.outline,
    disabledCheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    disabledCheckedTrackColor = BluePrimary.copy(alpha = 0.12f),
    disabledUncheckedThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
    disabledUncheckedTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
)

@Preview(showBackground = true)
@Composable
private fun SettingsSwitchPreview() {
    ConstructionMaterialTrackTheme {
        Surface {
            Column {
                SettingsSwitch(
                    title = stringResource(R.string.dark_theme),
                    subtitle = stringResource(R.string.switch_on),
                    icon = Icons.Default.DarkMode,
                    checked = true,
                    onCheckedChange = {}
                )
                SettingsSwitch(
                    title = stringResource(R.string.dark_theme),
                    subtitle = stringResource(R.string.switch_off),
                    icon = Icons.Default.DarkMode,
                    checked = false,
                    onCheckedChange = {}
                )
                SettingsSwitch(
                    title = "Disabled Setting",
                    subtitle = "This setting is disabled",
                    icon = Icons.Default.DarkMode,
                    checked = false,
                    onCheckedChange = {},
                    enabled = false
                )
            }
        }
    }
}
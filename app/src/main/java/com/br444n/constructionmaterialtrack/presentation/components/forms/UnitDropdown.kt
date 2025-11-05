package com.br444n.constructionmaterialtrack.presentation.components.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.domain.model.MaterialUnit
import com.br444n.constructionmaterialtrack.ui.theme.BlueDark
import com.br444n.constructionmaterialtrack.ui.theme.BluePrimary
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropdown(
    selectedUnit: MaterialUnit,
    onUnitSelected: (MaterialUnit) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    units: List<MaterialUnit> = MaterialUnit.getCommonUnits()
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedUnit.displayName,
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Straighten,
                    contentDescription = null,
                    tint = BlueDark
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BlueDark,
                unfocusedBorderColor = BluePrimary,
                focusedLabelColor = BlueDark,
                unfocusedLabelColor = BluePrimary,
                focusedTextColor = BlueDark,
                unfocusedTextColor = BlueDark,
                cursorColor = BlueDark,
                focusedContainerColor = BluePrimary.copy(0.1f),
                unfocusedContainerColor = BluePrimary.copy(0.05f)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = unit.displayName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = unit.shortName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun UnitDropdownPreview() {
    ConstructionMaterialTrackTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            UnitDropdown(
                selectedUnit = MaterialUnit.PIECES,
                onUnitSelected = { },
                label = "Unit of Measurement"
            )
            
            UnitDropdown(
                selectedUnit = MaterialUnit.METERS,
                onUnitSelected = { },
                label = "Unit of Measurement"
            )
        }
    }
}
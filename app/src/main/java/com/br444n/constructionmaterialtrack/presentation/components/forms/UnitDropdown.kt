package com.br444n.constructionmaterialtrack.presentation.components.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
    val expandedState = remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current
    
    ExposedDropdownMenuBox(
        expanded = expandedState.value,
        onExpandedChange = { expandedState.value = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedUnit.getDisplayName(context),
            onValueChange = { },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = BlueDark
                )
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
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth()
        )

        // === Menu extended ===
        ExposedDropdownMenu(
            expanded = expandedState.value,
            onDismissRequest = { expandedState.value = false },
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    width = 2.dp,
                    color = BluePrimary,
                    shape = RoundedCornerShape(12.dp)
                ),
            scrollState = rememberScrollState()
        ) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = unit.getDisplayName(context),
                                style = MaterialTheme.typography.bodyMedium,
                                color = BlueDark
                            )
                            Text(
                                text = unit.shortName,
                                style = MaterialTheme.typography.bodySmall,
                                color = BluePrimary
                            )
                        }
                    },
                    onClick = {
                        onUnitSelected(unit)
                        expandedState.value = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = MaterialTheme.colorScheme.onSurface
                    )
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

@Preview(showBackground = true, name = "Dropdown Expanded")
@Composable
private fun UnitDropdownExpandedPreview() {
    ConstructionMaterialTrackTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Simulamos el dropdown expandido manualmente para el preview
            Column {
                // Campo principal
                OutlinedTextField(
                    value = "Pieces",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Unit of Measurement") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = BlueDark
                        )
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
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Menu desplegado simulado
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = BluePrimary,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        // Mostramos algunas unidades comunes para el preview
                        val previewUnits = listOf(
                            MaterialUnit.PIECES,
                            MaterialUnit.UNITS,
                            MaterialUnit.METERS,
                            MaterialUnit.KILOGRAMS,
                            MaterialUnit.LITERS,
                            MaterialUnit.BAGS
                        )
                        
                        previewUnits.forEach { unit ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = when(unit) {
                                            MaterialUnit.PIECES -> "Pieces"
                                            MaterialUnit.UNITS -> "Units"
                                            MaterialUnit.METERS -> "Meters"
                                            MaterialUnit.KILOGRAMS -> "Kilograms"
                                            MaterialUnit.LITERS -> "Liters"
                                            MaterialUnit.BAGS -> "Bags"
                                            else -> unit.shortName
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = unit.shortName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "All Unit Types")
@Composable
private fun UnitDropdownAllUnitsPreview() {
    ConstructionMaterialTrackTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Available Unit Types",
                style = MaterialTheme.typography.titleMedium,
                color = BlueDark
            )
            
            // Mostramos todas las unidades disponibles
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = BluePrimary,
                        shape = RoundedCornerShape(12.dp)
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Unidades comunes
                    Text(
                        text = "Common Units:",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    MaterialUnit.getCommonUnits().forEach { unit ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = when(unit) {
                                    MaterialUnit.PIECES -> "Pieces"
                                    MaterialUnit.UNITS -> "Units"
                                    MaterialUnit.METERS -> "Meters"
                                    MaterialUnit.KILOGRAMS -> "Kilograms"
                                    MaterialUnit.LITERS -> "Liters"
                                    MaterialUnit.BAGS -> "Bags"
                                    MaterialUnit.BOXES -> "Boxes"
                                    else -> unit.shortName
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "(${unit.shortName})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Total available: ${MaterialUnit.getAllUnits().size} units",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
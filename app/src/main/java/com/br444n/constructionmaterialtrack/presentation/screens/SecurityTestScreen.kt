package com.br444n.constructionmaterialtrack.presentation.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExposurePlus1
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br444n.constructionmaterialtrack.presentation.components.forms.CustomTextField
import com.br444n.constructionmaterialtrack.presentation.components.forms.SecureTextField
import com.br444n.constructionmaterialtrack.presentation.components.images.ImagePicker
import com.br444n.constructionmaterialtrack.presentation.components.images.SecureImagePicker
import com.br444n.constructionmaterialtrack.presentation.hooks.ValidationType
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme

@Composable
fun SecurityTestScreen(
    modifier: Modifier = Modifier
) {
    var projectName by remember { mutableStateOf("") }
    var materialName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageError by remember { mutableStateOf("") }
    
    // Test strings for security validation
    val sqlInjectionTest = "'; DROP TABLE projects; --"
    val xssTest = "<script>alert('XSS')</script>"
    val specialCharsTest = "Test@#$%^&*()+={}[]|\\:;\"'<>?,./"
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "🔒 Security Test Screen",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Test the security features by entering malicious content in the fields below:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        // Test buttons for quick input
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Quick Test Inputs:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { projectName = sqlInjectionTest },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("SQL Injection", style = MaterialTheme.typography.bodySmall)
                    }
                    
                    Button(
                        onClick = { materialName = xssTest },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("XSS Attack", style = MaterialTheme.typography.bodySmall)
                    }
                    
                    Button(
                        onClick = { description = specialCharsTest },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Special Chars", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        
        Divider()
        
        Text(
            text = "🛡️ SECURE Components (Security Enabled)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        // Secure Image Picker
        SecureImagePicker(
            selectedImageUri = imageUri,
            onImageSelected = { 
                imageUri = it
                imageError = ""
            },
            onValidationError = { error ->
                imageError = error
            }
        )
        
        if (imageError.isNotEmpty()) {
            Text(
                text = "Image Error: $imageError",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        // Secure Project Name Field
        SecureTextField(
            value = projectName,
            onValueChange = { projectName = it },
            label = "Project Name (Secure)",
            validationType = ValidationType.PROJECT_NAME,
            leadingIcon = Icons.Default.Title,
            keyboardType = KeyboardType.Text
        )
        
        // Secure Material Name Field
        SecureTextField(
            value = materialName,
            onValueChange = { materialName = it },
            label = "Material Name (Secure)",
            validationType = ValidationType.MATERIAL_NAME,
            leadingIcon = Icons.Default.Badge,
            keyboardType = KeyboardType.Text
        )
        
        // Secure Price and Quantity
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SecureTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = "Quantity (Secure)",
                validationType = ValidationType.QUANTITY,
                leadingIcon = Icons.Default.ExposurePlus1,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
            
            SecureTextField(
                value = price,
                onValueChange = { price = it },
                label = "Price (Secure)",
                validationType = ValidationType.PRICE,
                leadingIcon = Icons.Default.AttachMoney,
                keyboardType = KeyboardType.Decimal,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Secure Description Field
        SecureTextField(
            value = description,
            onValueChange = { description = it },
            label = "Description (Secure)",
            validationType = ValidationType.DESCRIPTION,
            leadingIcon = Icons.Default.Description,
            keyboardType = KeyboardType.Text,
            singleLine = false,
            maxLines = 3,
            isRequired = false
        )
        
        Divider()
        
        Text(
            text = "⚠️ REGULAR Components (No Security)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary
        )
        
        // Regular CustomTextField (Security Disabled)
        CustomTextField(
            value = projectName,
            onValueChange = { projectName = it },
            label = "Project Name (No Security)",
            leadingIcon = Icons.Default.Title,
            enableSecurity = false  // Security disabled
        )
        
        // Regular CustomTextField with Security Enabled
        CustomTextField(
            value = materialName,
            onValueChange = { materialName = it },
            label = "Material Name (Security Enabled)",
            leadingIcon = Icons.Default.Badge,
            validationType = ValidationType.MATERIAL_NAME,
            enableSecurity = true  // Security enabled
        )
        
        // Regular ImagePicker (No Security)
        ImagePicker(
            selectedImageUri = imageUri,
            onImageSelected = { imageUri = it },
            enableSecurity = false  // Security disabled
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Results Display
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "📊 Current Values:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Text("Project Name: '$projectName'", style = MaterialTheme.typography.bodySmall)
                Text("Material Name: '$materialName'", style = MaterialTheme.typography.bodySmall)
                Text("Price: '$price'", style = MaterialTheme.typography.bodySmall)
                Text("Quantity: '$quantity'", style = MaterialTheme.typography.bodySmall)
                Text("Description: '$description'", style = MaterialTheme.typography.bodySmall)
                Text("Image URI: ${imageUri?.toString() ?: "None"}", style = MaterialTheme.typography.bodySmall)
            }
        }
        
        // Automated Tests
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🤖 Automated Security Tests",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Button(
                    onClick = {
                        val results = com.br444n.constructionmaterialtrack.core.security.SecurityTester.runSecurityTests()
                        com.br444n.constructionmaterialtrack.core.security.SecurityTester.printTestResults(results)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Run All Security Tests (Check Logcat)")
                }
                
                Text(
                    text = "This will run comprehensive security tests and print results to Logcat. Look for 'SecurityTester' tag.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
        
        // Instructions
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🧪 How to Test:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Text("1. Use the quick test buttons to inject malicious content", style = MaterialTheme.typography.bodySmall)
                Text("2. Try typing SQL injection manually: '; DROP TABLE users; --", style = MaterialTheme.typography.bodySmall)
                Text("3. Try XSS: <script>alert('hack')</script>", style = MaterialTheme.typography.bodySmall)
                Text("4. Compare SECURE vs REGULAR components behavior", style = MaterialTheme.typography.bodySmall)
                Text("5. Upload invalid files (non-images, large files) to test image security", style = MaterialTheme.typography.bodySmall)
                Text("6. Watch for red error indicators and validation messages", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SecurityTestScreenPreview() {
    ConstructionMaterialTrackTheme {
        SecurityTestScreen()
    }
}
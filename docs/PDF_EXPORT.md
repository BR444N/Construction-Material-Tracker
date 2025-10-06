# PDF Export Feature

## Overview
The PDF Export feature allows users to generate professional PDF reports of their projects, including project details, images, and material lists. The feature includes a preview screen where users can see exactly how the PDF will look before generating and downloading it.

## Features

### 1. PDF Preview Screen
- **Live Preview**: Shows exactly how the PDF will appear
- **Project Header**: Circular project image, name (bold), and description
- **Materials List**: Complete list with checkboxes, quantities, prices, and descriptions
- **Professional Layout**: Clean, printable design with proper spacing

### 2. PDF Generation with iText
- **Professional Quality**: Industry-standard iText library for PDF generation
- **Rich Formatting**: Tables, images, fonts, and layout control
- **Automatic Naming**: Files named with project name and timestamp
- **Download Location**: Saves to device's Downloads folder
- **Progress Feedback**: Loading indicators during generation

### 3. Layout Design
- **Circular Image**: Project image displayed in a circle with border
- **Centered Header**: Project name and description centered on page
- **Material Checklist**: Visual checkboxes showing purchase status
- **Responsive Layout**: Adapts to different content lengths

## Implementation Details

### UI State Management
```kotlin
data class PdfPreviewUiState(
    val project: Project? = null,
    val materials: List<Material> = emptyList(),
    val isLoading: Boolean = false,
    val isGeneratingPdf: Boolean = false,
    val pdfGenerated: Boolean = false,
    val errorMessage: String? = null
)
```

### PDF Preview Screen Components

#### Project Header
```kotlin
@Composable
private fun PdfProjectHeader(project: Project) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Circular image with border
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(3.dp, Color.Gray, CircleShape)
        ) {
            AsyncImage(model = project.imageUri, ...)
        }
        
        // Bold project name
        Text(
            text = project.name,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        // Normal description
        Text(
            text = project.description,
            textAlign = TextAlign.Center
        )
    }
}
```

#### Material Items
```kotlin
@Composable
private fun PdfMaterialItem(material: Material) {
    Row {
        // Checkbox icon
        Icon(
            imageVector = if (material.isPurchased) 
                Icons.Default.CheckBox 
            else 
                Icons.Default.CheckBoxOutlineBlank
        )
        
        // Material details
        Column {
            Row {
                Text(material.name, fontWeight = FontWeight.Medium)
                Text("Qty: ${material.quantity}")
                Text("$${material.price}")
            }
            if (material.description.isNotBlank()) {
                Text(material.description, color = Color.Gray)
            }
        }
    }
}
```

### PDF Generation Process

#### Document Creation with iText
```kotlin
private fun createPdfDocument(project: Project, materials: List<Material>) {
    val writer = PdfWriter(FileOutputStream(file))
    val pdfDocument = PdfDocument(writer)
    val document = Document(pdfDocument, PageSize.A4)
    
    document.setMargins(50f, 50f, 50f, 50f)
    
    try {
        addProjectImage(document, project)
        addProjectHeader(document, project)
        addMaterialsSection(document, materials)
    } finally {
        document.close()
    }
}
```

#### Professional Layout Components
```kotlin
// Project Header
val projectName = Paragraph(project.name)
    .setTextAlignment(TextAlignment.CENTER)
    .setFontSize(24f)
    .setBold()
    .setMarginTop(20f)

// Materials Table
val table = Table(UnitValue.createPercentArray(floatArrayOf(8f, 40f, 15f, 15f, 22f)))
    .setWidth(UnitValue.createPercentValue(100f))

// Image Handling
val imageData = ImageDataFactory.create(imageBytes)
val image = Image(imageData)
    .setWidth(120f)
    .setHeight(120f)
    .setHorizontalAlignment(HorizontalAlignment.CENTER)
```

### Navigation Integration
```kotlin
// In ProjectDetailsScreen
Button(onClick = { onExportToPdf(project.id) }) {
    Icon(Icons.Default.PictureAsPdf)
    Text("Export to PDF")
}

// In Navigation
composable(Screen.PdfPreview.route) { backStackEntry ->
    val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
    PdfPreviewScreen(
        projectId = projectId,
        onBackClick = { navController.popBackStack() }
    )
}
```

## User Experience Flow

### Accessing PDF Export
1. User views project details
2. Scrolls to bottom and taps "Export to PDF" button
3. Navigates to PDF Preview screen
4. Sees live preview of how PDF will look

### Preview Screen
1. **Header Section**: Circular project image, bold name, normal description
2. **Materials Section**: Title "Materials" followed by complete list
3. **Material Items**: Checkbox, name, quantity, price, and description
4. **Empty State**: Shows "No materials added yet" if no materials

### Generating PDF
1. User reviews preview and taps download icon in top bar
2. Loading indicator appears in top bar
3. PDF is generated in background
4. File is saved to Downloads folder with descriptive name
5. Success feedback (can be enhanced with toast/snackbar)

### File Management
1. **Automatic Naming**: `ProjectName_YYYYMMDD_HHMMSS.pdf`
2. **Download Location**: Device's Downloads folder
3. **File Access**: Users can find file in file manager or downloads app

## Technical Benefits

### Preview Accuracy
- **WYSIWYG**: Preview matches final PDF exactly
- **Real-time Updates**: Preview updates when project data changes
- **Layout Consistency**: Same components used for preview and generation

### Performance
- **Lazy Loading**: Preview content loaded efficiently
- **Background Generation**: PDF creation doesn't block UI
- **Memory Management**: Proper cleanup of PDF resources

### File Management
- **Unique Names**: Timestamp prevents file conflicts
- **Standard Location**: Downloads folder is user-accessible
- **Proper Permissions**: Handles storage permissions correctly

## Error Handling

### Preview Errors
- **Loading Failures**: Shows error state with retry option
- **Missing Data**: Graceful handling of missing project/materials
- **Image Loading**: Fallback for unavailable project images

### Generation Errors
- **Storage Permissions**: Requests permissions if needed
- **Disk Space**: Handles insufficient storage gracefully
- **File Creation**: Error messages for file system issues

### User Feedback
- **Loading States**: Clear indicators during operations
- **Error Messages**: User-friendly error descriptions
- **Success Confirmation**: Feedback when PDF is generated

## Accessibility Features

### Screen Reader Support
- **Content Descriptions**: All interactive elements properly labeled
- **Semantic Structure**: Proper heading hierarchy in preview
- **Navigation**: Clear focus management and tab order

### Visual Accessibility
- **High Contrast**: Preview works in high contrast mode
- **Text Scaling**: Adapts to user's text size preferences
- **Color Independence**: Checkboxes don't rely solely on color

## Best Practices

### User Interface
- **Clear Preview**: Shows exactly what will be generated
- **Intuitive Controls**: Standard download icon for generation
- **Immediate Feedback**: Loading states and progress indicators

### File Management
- **Descriptive Names**: Include project name and timestamp
- **Standard Location**: Use system Downloads folder
- **Conflict Prevention**: Timestamp ensures unique filenames

### Performance
- **Efficient Rendering**: Minimal re-composition in preview
- **Background Processing**: PDF generation doesn't block UI
- **Resource Cleanup**: Proper disposal of PDF resources

## Future Enhancements

### Advanced Features
- **Custom Templates**: Multiple PDF layout options
- **Branding**: Add company logo and contact information
- **Multi-page Support**: Handle large projects with page breaks
- **Print Preview**: Show page boundaries and margins

### Export Options
- **Multiple Formats**: Export to Word, Excel, or image formats
- **Email Integration**: Direct sharing via email
- **Cloud Storage**: Save to Google Drive, Dropbox, etc.
- **Batch Export**: Generate PDFs for multiple projects

### Customization
- **Layout Options**: Different arrangements of content
- **Font Choices**: User-selectable fonts and sizes
- **Color Themes**: Professional color schemes
- **Content Selection**: Choose which sections to include

### Integration
- **Print Service**: Direct printing from preview
- **Share Intent**: System sharing with other apps
- **QR Codes**: Add QR codes linking to digital versions
- **Digital Signatures**: Sign PDFs for official documents
# Guía de Uso de Componentes con Seguridad

## 🔒 Componentes Actualizados con Seguridad

Tus componentes existentes ahora tienen capacidades de seguridad opcionales que puedes habilitar según necesites.

### 1. CustomTextField con Seguridad

#### Uso Básico (Sin Seguridad)
```kotlin
CustomTextField(
    value = projectName,
    onValueChange = { projectName = it },
    label = "Project Name",
    leadingIcon = Icons.Default.Title
)
```

#### Uso Seguro (Con Validación)
```kotlin
CustomTextField(
    value = projectName,
    onValueChange = { projectName = it },
    label = "Project Name",
    leadingIcon = Icons.Default.Title,
    validationType = ValidationType.PROJECT_NAME,
    enableSecurity = true  // 🔒 Habilita seguridad
)
```

### 2. ImagePicker con Seguridad

#### Uso Básico (Sin Seguridad)
```kotlin
ImagePicker(
    selectedImageUri = imageUri,
    onImageSelected = { imageUri = it }
)
```

#### Uso Seguro (Con Validación)
```kotlin
ImagePicker(
    selectedImageUri = imageUri,
    onImageSelected = { imageUri = it },
    enableSecurity = true,  // 🔒 Habilita seguridad
    onValidationError = { error ->
        // Maneja errores de validación
        showErrorMessage(error)
    }
)
```

## 🛡️ Tipos de Validación Disponibles

### Para CustomTextField:
- `ValidationType.PROJECT_NAME` - Nombres de proyecto (100 chars max)
- `ValidationType.MATERIAL_NAME` - Nombres de material (100 chars max)
- `ValidationType.DESCRIPTION` - Descripciones (500 chars max)
- `ValidationType.PRICE` - Precios (solo números y decimales)
- `ValidationType.QUANTITY` - Cantidades (solo números y decimales)
- `ValidationType.TEXT` - Texto básico sin validación específica

### Para ImagePicker:
- Validación de tipos MIME (JPEG, PNG, WebP)
- Límite de tamaño de archivo (5MB)
- Verificación de accesibilidad
- Consistencia de extensión/MIME

## 📋 Ejemplo Completo de Formulario Seguro

```kotlin
@Composable
fun SecureProjectForm() {
    var projectName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageError by remember { mutableStateOf("") }
    
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Imagen con validación de seguridad
        ImagePicker(
            selectedImageUri = imageUri,
            onImageSelected = { 
                imageUri = it
                imageError = ""
            },
            enableSecurity = true,
            onValidationError = { error ->
                imageError = error
            }
        )
        
        // Nombre del proyecto con validación
        CustomTextField(
            value = projectName,
            onValueChange = { projectName = it },
            label = "Project Name",
            leadingIcon = Icons.Default.Title,
            validationType = ValidationType.PROJECT_NAME,
            enableSecurity = true
        )
        
        // Descripción con validación
        CustomTextField(
            value = description,
            onValueChange = { description = it },
            label = "Description",
            leadingIcon = Icons.Default.Description,
            singleLine = false,
            maxLines = 3,
            validationType = ValidationType.DESCRIPTION,
            enableSecurity = true
        )
        
        // Botón de envío
        Button(
            onClick = { 
                // Aquí el formulario ya está validado automáticamente
                submitForm(projectName, description, imageUri)
            },
            enabled = projectName.isNotEmpty() && imageError.isEmpty()
        ) {
            Text("Save Project")
        }
    }
}
```

## 🔧 Migración Gradual

### Opción 1: Habilitar Seguridad Gradualmente
Puedes migrar tus formularios existentes gradualmente:

```kotlin
// Paso 1: Agregar parámetro de seguridad (deshabilitado por defecto)
CustomTextField(
    value = value,
    onValueChange = onValueChange,
    label = label,
    enableSecurity = false  // Mantén comportamiento actual
)

// Paso 2: Habilitar seguridad cuando estés listo
CustomTextField(
    value = value,
    onValueChange = onValueChange,
    label = label,
    validationType = ValidationType.PROJECT_NAME,
    enableSecurity = true  // Habilita cuando estés listo
)
```

### Opción 2: Usar Componentes Dedicados
Alternativamente, puedes usar los componentes dedicados de seguridad:

```kotlin
// Para máxima seguridad, usa los componentes dedicados
SecureTextField(
    value = value,
    onValueChange = onValueChange,
    label = label,
    validationType = ValidationType.PROJECT_NAME
)

SecureImagePicker(
    selectedImageUri = uri,
    onImageSelected = { uri = it },
    onValidationError = { error -> handleError(error) }
)
```

## ⚡ Características de Seguridad Activas

### Cuando `enableSecurity = true`:

#### CustomTextField:
- ✅ Validación en tiempo real
- ✅ Filtrado de caracteres peligrosos
- ✅ Detección de patrones de SQL injection
- ✅ Sanitización automática de entrada
- ✅ Límites de longitud aplicados
- ✅ Indicadores visuales de error
- ✅ Mensajes de error descriptivos

#### ImagePicker:
- ✅ Validación de tipos de archivo
- ✅ Verificación de tamaño de archivo
- ✅ Validación de MIME type
- ✅ Consistencia extensión/MIME
- ✅ Verificación de accesibilidad de URI
- ✅ Manejo seguro de permisos

## 🚨 Patrones Bloqueados

### SQL Injection:
- `'`, `--`, `;`, `|`, `*`, `%`
- `union`, `select`, `insert`, `update`, `delete`
- `drop`, `create`, `alter`, `exec`

### XSS:
- `<script>`, `javascript:`, `vbscript:`
- `<`, `>`, `&lt;`, `&gt;`
- `onload`, `onerror`, `onclick`

### Caracteres Peligrosos:
- HTML/XML: `<`, `>`, `"`, `'`, `&`
- Caracteres de control
- Secuencias de escape maliciosas

## 📊 Rendimiento

- **Impacto mínimo**: La validación solo se ejecuta cuando `enableSecurity = true`
- **Lazy validation**: Solo valida cuando el usuario ha interactuado con el campo
- **Filtrado eficiente**: Filtrado de caracteres en tiempo real para campos numéricos
- **Caché de validación**: Los resultados se cachean para evitar re-validaciones innecesarias

## 🔍 Testing

Para probar la seguridad, intenta ingresar:

### Texto Malicioso:
```
'; DROP TABLE projects; --
<script>alert('XSS')</script>
javascript:alert('XSS')
```

### Archivos Inválidos:
- Archivos .exe, .bat, .sh
- Archivos mayores a 5MB
- Archivos con MIME types incorrectos

### Caracteres Especiales:
```
<>&"'
../../../etc/passwd
%00%01%02
```

Todos estos deberían ser bloqueados o sanitizados automáticamente cuando la seguridad está habilitada.
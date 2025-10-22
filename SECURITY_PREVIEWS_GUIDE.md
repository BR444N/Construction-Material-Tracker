# 🎨 Guía de Previews de Seguridad

## 📱 Previews Creadas para Componentes Seguros

### 🖼️ SecureImagePicker Previews

#### 1. **Default State** (`PreviewSecureImagePickerDefault`)
- **Estado**: Sin imagen seleccionada
- **Apariencia**: Círculo con icono de cámara y texto "Add Photo"
- **Borde**: Azul (estado normal)

#### 2. **With Valid Image** (`PreviewSecureImagePickerWithImage`)
- **Estado**: Imagen válida cargada
- **Apariencia**: Imagen mostrada en círculo
- **Borde**: Azul (estado válido)

#### 3. **Invalid Image Error** (`PreviewSecureImagePickerError`)
- **Estado**: Archivo de imagen inválido
- **Apariencia**: Icono de error rojo
- **Borde**: Rojo (estado de error)
- **Mensaje**: "Unsupported image format. Use JPEG, PNG, or WebP"

#### 4. **File Too Large Error** (`PreviewSecureImagePickerFileTooLarge`)
- **Estado**: Archivo mayor a 5MB
- **Apariencia**: Icono de error rojo
- **Borde**: Rojo (estado de error)
- **Mensaje**: "Image file too large (max 5MB)"

#### 5. **Permission Denied Error** (`PreviewSecureImagePickerPermissionError`)
- **Estado**: Sin permisos de acceso
- **Apariencia**: Icono de error rojo
- **Borde**: Rojo (estado de error)
- **Mensaje**: "Permission denied to access image"

#### 6. **All States Comparison** (`PreviewSecureImagePickerAllStates`)
- **Estado**: Comparación de todos los estados
- **Apariencia**: Lista con todos los estados en cards
- **Útil para**: Ver todos los casos de uso de una vez

### 📝 SecureTextField Previews

#### 1. **Valid Inputs** (`SecureTextFieldValidPreview`)
- **Estado**: Campos con datos válidos
- **Apariencia**: Campos normales sin errores
- **Ejemplos**: 
  - "Valid Project Name"
  - "123.45" (precio)
  - "100" (cantidad)

#### 2. **Security Errors** (`SecureTextFieldErrorPreview`)
- **Estado**: Ataques de seguridad bloqueados
- **Apariencia**: Campos con bordes rojos y iconos de error
- **Ejemplos**:
  - `'; DROP TABLE users; --` → "Invalid characters detected"
  - `<script>alert('XSS')</script>` → "Invalid characters detected"
  - `abc123` (en precio) → "Price must contain only numbers and decimal point"

#### 3. **Length Limit Errors** (`SecureTextFieldLengthErrorPreview`)
- **Estado**: Campos que exceden límites de longitud
- **Apariencia**: Campos con bordes rojos y mensajes de error
- **Ejemplos**:
  - Nombre > 100 caracteres → "Project name too long (max 100 characters)"
  - Descripción > 500 caracteres → "Description too long (max 500 characters)"

#### 4. **Security Comparison** (`SecureTextFieldComparisonPreview`)
- **Estado**: Comparación visual de seguridad
- **Apariencia**: Cards mostrando campo seguro vs ataques bloqueados
- **Útil para**: Demostrar efectividad de la seguridad

## 🎯 Cómo Usar las Previews

### En Android Studio:

1. **Abrir archivo de componente**:
   - `SecureImagePicker.kt`
   - `SecureTextField.kt`

2. **Ver previews**:
   - Cambiar a vista "Split" o "Design"
   - Verás todas las previews listadas
   - Cada preview tiene un nombre descriptivo

3. **Interactuar con previews**:
   - Hacer clic en preview para verla en pantalla completa
   - Usar botones de refresh si es necesario

### Para Desarrollo:

#### **Verificar Estados Visuales**:
```kotlin
// Ver cómo se ve un error de validación
@Preview(name = "SQL Injection Blocked")
@Composable
fun MySecurityTest() {
    // Preview muestra el estado de error
}
```

#### **Documentar Comportamiento**:
- Las previews sirven como documentación visual
- Muestran todos los casos de uso posibles
- Facilitan el testing visual

#### **Debugging Visual**:
- Si un componente no se ve bien, revisar previews
- Comparar estado actual vs estado esperado
- Identificar problemas de UI rápidamente

## 🧪 Casos de Uso de las Previews

### 1. **Para Desarrolladores**:
- **Verificar** que los errores se muestran correctamente
- **Comparar** estados válidos vs inválidos
- **Documentar** comportamiento esperado

### 2. **Para Diseñadores**:
- **Revisar** que los colores de error son apropiados
- **Verificar** que los mensajes son legibles
- **Asegurar** consistencia visual

### 3. **Para Testing**:
- **Casos de prueba** visuales documentados
- **Estados edge** cubiertos
- **Regresión visual** fácil de detectar

### 4. **Para Documentación**:
- **Screenshots** automáticos para documentación
- **Ejemplos visuales** para guías de usuario
- **Referencia rápida** de comportamiento

## 🎨 Personalización de Previews

### Agregar Nuevos Estados:

```kotlin
@Preview(showBackground = true, name = "Custom Error State")
@Composable
fun MyCustomErrorPreview() {
    ConstructionMaterialTrackTheme {
        SecureTextField(
            value = "custom error case",
            onValueChange = {},
            label = "Custom Field",
            validationType = ValidationType.PROJECT_NAME
        )
    }
}
```

### Modificar Previews Existentes:

```kotlin
@Preview(showBackground = true, name = "Dark Theme Error")
@Composable
fun DarkThemeErrorPreview() {
    ConstructionMaterialTrackTheme(darkTheme = true) {
        // Preview en tema oscuro
    }
}
```

## 📊 Beneficios de las Previews

### ✅ **Desarrollo Más Rápido**:
- Ver cambios inmediatamente
- No necesidad de compilar app completa
- Testing visual instantáneo

### ✅ **Mejor Calidad**:
- Todos los estados cubiertos
- Casos edge documentados
- Regresión visual prevenida

### ✅ **Documentación Viva**:
- Ejemplos siempre actualizados
- Comportamiento visual claro
- Referencia para nuevos desarrolladores

### ✅ **Testing Eficiente**:
- Casos de prueba visuales
- Verificación rápida de cambios
- Debugging visual simplificado

---

## 🎉 Resultado Final

**Ahora tienes previews completas que muestran:**

- ✅ **Estados normales** de los componentes
- ❌ **Estados de error** con validación de seguridad
- 🔍 **Comparaciones visuales** entre seguro vs inseguro
- 📱 **Todos los casos de uso** documentados visualmente

**¡Puedes ver inmediatamente cómo se comportan tus componentes seguros en Android Studio!** 🛡️
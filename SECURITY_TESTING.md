# 🧪 Guía de Pruebas de Seguridad

## 🚀 Cómo Probar la Seguridad

### 1. Usar la Pantalla de Pruebas

He creado `SecurityTestScreen.kt` que puedes agregar temporalmente a tu app para probar:

```kotlin
// En tu MainActivity o donde manejes la navegación
SecurityTestScreen()
```

### 2. Pruebas Manuales Rápidas

#### 🔍 **SQL Injection Tests:**
Intenta escribir estos textos en los campos **SEGUROS**:

```sql
'; DROP TABLE users; --
' OR '1'='1' --
'; DELETE FROM projects; --
' UNION SELECT * FROM users --
admin'--
```

**Resultado esperado**: ❌ Error rojo, mensaje "Invalid characters detected"

#### 🔍 **XSS Attack Tests:**
```html
<script>alert('XSS')</script>
<img src=x onerror=alert('hack')>
javascript:alert('XSS')
<svg onload=alert('XSS')>
```

**Resultado esperado**: ❌ Error rojo, mensaje "Invalid characters detected"

#### 🔍 **Length Tests:**
- Proyecto: Escribe más de 100 caracteres
- Descripción: Escribe más de 500 caracteres

**Resultado esperado**: ❌ Error "too long"

#### 🔍 **Numeric Tests:**
En campos de precio/cantidad:
```
-10.50 (negativo)
abc123 (letras)
12.34.56 (múltiples decimales)
12$34 (caracteres especiales)
```

**Resultado esperado**: ❌ Solo números y punto decimal permitidos

### 3. Pruebas Automáticas

#### Ejecutar Tests Programáticos:
```kotlin
// En cualquier parte de tu código
val results = SecurityTester.runSecurityTests()
SecurityTester.printTestResults(results)
```

#### Ver Resultados en Logcat:
1. Abre Android Studio
2. Ve a **Logcat**
3. Filtra por tag: `SecurityTester`
4. Ejecuta los tests
5. Verás algo como:

```
I/SecurityTester: === SECURITY TEST RESULTS ===
I/SecurityTester: --- SQL Injection Tests ---
I/SecurityTester: ✅ PASS: '; DROP TABLE users; --
I/SecurityTester: ✅ PASS: ' OR '1'='1' --
I/SecurityTester: --- XSS Attack Tests ---
I/SecurityTester: ✅ PASS: <script>alert('XSS')</script>
I/SecurityTester: === SUMMARY ===
I/SecurityTester: Total Tests: 45
I/SecurityTester: Passed: 43
I/SecurityTester: Failed: 2
I/SecurityTester: Success Rate: 95%
```

### 4. Comparación Visual

La `SecurityTestScreen` muestra:

#### ✅ **Componentes SEGUROS:**
- `SecureTextField` - Validación completa
- `SecureImagePicker` - Validación de archivos
- `CustomTextField` con `enableSecurity = true`

#### ⚠️ **Componentes REGULARES:**
- `CustomTextField` con `enableSecurity = false`
- `ImagePicker` con `enableSecurity = false`

### 5. Indicadores Visuales de Seguridad

#### ✅ **Cuando la Seguridad Funciona:**
- 🔴 **Borde rojo** en el campo
- ❌ **Icono de error** 
- 📝 **Mensaje de error** debajo del campo
- 🚫 **Caracteres filtrados** automáticamente

#### ❌ **Cuando NO hay Seguridad:**
- ✅ **Acepta cualquier texto**
- 🟢 **Sin indicadores de error**
- 💀 **Contenido malicioso pasa**

### 6. Pruebas de Archivos (ImagePicker)

#### 🔍 **Archivos Válidos:**
- `image.jpg` (✅ Debería funcionar)
- `photo.png` (✅ Debería funcionar)
- `picture.webp` (✅ Debería funcionar)

#### 🔍 **Archivos Inválidos:**
- `virus.exe` (❌ Debería rechazarse)
- `document.pdf` (❌ Debería rechazarse)
- `huge_image.jpg` > 5MB (❌ Debería rechazarse)
- `fake.jpg` (archivo .exe renombrado) (❌ Debería rechazarse)

### 7. Ejemplo de Uso en Código Real

```kotlin
@Composable
fun MySecureForm() {
    var projectName by remember { mutableStateOf("") }
    
    // OPCIÓN 1: Usar SecureTextField (siempre seguro)
    SecureTextField(
        value = projectName,
        onValueChange = { projectName = it },
        label = "Project Name",
        validationType = ValidationType.PROJECT_NAME
    )
    
    // OPCIÓN 2: Usar CustomTextField con seguridad habilitada
    CustomTextField(
        value = projectName,
        onValueChange = { projectName = it },
        label = "Project Name",
        validationType = ValidationType.PROJECT_NAME,
        enableSecurity = true  // 🔒 IMPORTANTE: Habilitar seguridad
    )
}
```

### 8. Debugging Tips

#### Si no ves errores de validación:
1. ✅ Verifica que `enableSecurity = true`
2. ✅ Verifica que tienes `validationType` configurado
3. ✅ Usa `SecureTextField` en lugar de `CustomTextField` para garantizar seguridad
4. ✅ Revisa Logcat para mensajes de validación

#### Para ver logs de seguridad:
```
adb logcat | grep -E "(SecurityTester|InputValidator|SecureTextField)"
```

### 9. Métricas de Éxito

#### ✅ **Tests Exitosos:**
- SQL injection bloqueado: 100%
- XSS attacks bloqueados: 100%
- Límites de longitud aplicados: 100%
- Validación numérica funcionando: 100%
- Archivos maliciosos rechazados: 100%

#### 📊 **Objetivo:**
- **95%+ de tests pasando**
- **0 vulnerabilidades críticas**
- **Experiencia de usuario fluida**

### 10. Integración en tu App

Para integrar en tu app existente:

1. **Reemplaza gradualmente** tus TextFields:
```kotlin
// Antes
OutlinedTextField(value = text, onValueChange = { text = it })

// Después
SecureTextField(
    value = text, 
    onValueChange = { text = it },
    validationType = ValidationType.PROJECT_NAME
)
```

2. **Habilita seguridad** en CustomTextField existentes:
```kotlin
CustomTextField(
    // ... parámetros existentes
    enableSecurity = true,
    validationType = ValidationType.PROJECT_NAME
)
```

3. **Monitorea logs** para detectar intentos de ataque:
```
I/InputValidator: Blocked SQL injection attempt: '; DROP TABLE users; --
W/SecureImagePicker: Invalid file type rejected: application/exe
```

¡Ahora puedes probar completamente la seguridad de tu app! 🛡️
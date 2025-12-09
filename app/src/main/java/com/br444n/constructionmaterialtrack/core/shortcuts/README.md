# App Shortcuts Implementation - Optimized

Este módulo maneja los shortcuts de la aplicación con procesamiento optimizado en background para evitar bloquear la UI.

## Tipos de Shortcuts

### 1. Static Shortcuts (Estáticos)
Definidos en `res/xml/shortcuts.xml`, siempre disponibles:

- **Add Project**: Abre directamente la pantalla de agregar proyecto

### 2. Dynamic Shortcuts (Dinámicos)
Gestionados por `DynamicShortcutManager`, se actualizan automáticamente:

- **Recent Projects**: Muestra los 2 proyectos más recientes con sus imágenes
- Se actualizan cuando:
  - Se carga la lista de proyectos
  - Se elimina un proyecto
  - Se refresca la lista
  - **Actualización inmediata** cuando cambia el número de proyectos (crear/eliminar)

## Arquitectura Optimizada

```
ProjectListViewModel
    ↓
DynamicShortcutManager (debounce 5 min)
    ↓ (background coroutine)
ShortcutIconProcessor
    ├── ShortcutCache (check)
    │   ├── Memory (LruCache 4MB)
    │   └── Disk (cacheDir/shortcuts/)
    ├── Load & Resize (inSampleSize)
    ├── Apply Circular Mask
    └── Save to Cache
    ↓
ShortcutManagerCompat.setDynamicShortcuts()
```

## Componentes

### 1. ShortcutCache
- **Caché dual**: Memoria (LruCache) + Disco
- **Hash-based keys**: `projectId_imageHash`
- **Auto-cleanup**: Elimina archivos > 7 días
- **Invalidación**: Por proyecto o total
- **Soporte Content URI**: Maneja tanto rutas de archivo como Content URIs (`content://`)

### 2. ShortcutIconProcessor
- **Procesamiento async**: Todo en Dispatchers.IO
- **Soporte dual de imágenes**:
  - **File paths**: Imágenes de cámara guardadas localmente
  - **Content URIs**: Imágenes de galería (`content://media/picker_get_content/...`)
  - Detección automática y manejo apropiado para cada tipo
- **Optimización de imágenes**:
  - Tamaño final: 48dp (configurable en `ICON_SIZE_DP`)
  - Este es el tamaño real que se mostrará (NO se escala después)
  - inSampleSize para evitar cargar full resolution
  - Redimensionamiento exacto al tamaño objetivo
  - Máscara circular con fondo blanco (Google recommendation)
  - Anti-aliasing y filtrado de alta calidad
- **Adaptive Icons**: Usa `createWithAdaptiveBitmap()` para mejor compatibilidad
- **Mutex por proyecto**: Evita procesamiento concurrente
- **Fallback robusto**: Ícono por defecto si falla

### 3. DynamicShortcutManager
- **Debounce inteligente**: 2 minutos entre actualizaciones
  - Se omite automáticamente cuando cambia el número de proyectos
  - Actualización inmediata al crear/eliminar proyectos
- **Background processing**: Coroutines con SupervisorJob
- **Verificación de versión**: Android 7.1+ (API 25)
- **Manejo de errores**: Try-catch en cada etapa

## Optimizaciones Implementadas

### ✅ Procesamiento en Background
- Todo el procesamiento de imágenes en Dispatchers.IO
- No bloquea el hilo principal
- Coroutines con SupervisorJob para aislamiento

### ✅ Caché Dual
- Memoria: Acceso instantáneo (LruCache 4MB)
- Disco: Persistencia entre sesiones
- Hash-based: Detecta cambios en imágenes

### ✅ Tamaño Optimizado
- 48dp tamaño final (configurable)
- Este es el tamaño real mostrado en el launcher
- inSampleSize para decodificación eficiente
- Redimensionamiento antes de máscara circular
- Fondo blanco para seguir recomendaciones de Google

**Nota**: Para cambiar el tamaño de los íconos, modifica `ICON_SIZE_DP` en `ShortcutIconProcessor.kt`
- Más pequeño (32-40dp): Mejor rendimiento, menos detalle
- Actual (48dp): Balance entre calidad y rendimiento
- Más grande (64-72dp): Más detalle, mayor uso de memoria

### ✅ Debouncing Inteligente
- Máximo 1 actualización cada 2 minutos (para cambios normales)
- **Bypass automático** cuando cambia el número de proyectos
- Actualización inmediata al crear/eliminar proyectos
- Evita procesamiento repetitivo innecesario

### ✅ Soporte Content URI
- Maneja imágenes de cámara (file paths)
- Maneja imágenes de galería (Content URIs)
- Hash calculation adaptado para ambos tipos
- ContentResolver para acceso seguro a Content URIs

### ✅ Sincronización
- Mutex por proyecto
- Evita procesamiento concurrente de la misma imagen

### ✅ Limpieza Automática
- Cache cleanup después de cada actualización
- Elimina archivos > 7 días
- Invalidación por proyecto

## Uso

### Actualizar Shortcuts
```kotlin
// Automático desde ProjectListViewModel
DynamicShortcutManager.updateProjectShortcuts(context, projectList)
```

### Invalidar Caché de Proyecto
```kotlin
// Cuando se actualiza la imagen de un proyecto
DynamicShortcutManager.invalidateProjectCache(context, projectId)
```

### Limpiar Shortcuts
```kotlin
DynamicShortcutManager.clearProjectShortcuts(context)
```

## Rendimiento

### Antes (Bloqueante)
- ❌ Procesamiento en hilo principal
- ❌ Sin caché
- ❌ Imágenes full resolution
- ❌ ANR después de 5 segundos
- ❌ CPU 55% kernel
- ❌ No soportaba Content URIs

### Después (Optimizado)
- ✅ Procesamiento en background
- ✅ Caché dual (memoria + disco)
- ✅ Imágenes redimensionadas (48dp)
- ✅ Sin bloqueo de UI
- ✅ Debouncing inteligente (2 min)
- ✅ Soporte completo para Content URIs
- ✅ Actualización inmediata al crear/eliminar proyectos

## Testing

1. **Primera carga**: Procesa y cachea imágenes
2. **Cargas subsecuentes**: Usa caché (instantáneo)
3. **Cambio de imagen**: Detecta por hash y reprocesa
4. **Sin imagen**: Usa ícono por defecto
5. **Imagen de cámara**: Procesa desde file path
6. **Imagen de galería**: Procesa desde Content URI
7. **Crear proyecto**: Actualización inmediata (bypass debounce)
8. **Eliminar proyecto**: Actualización inmediata (bypass debounce)

## Limitaciones

- Máximo 2 proyectos (configurable en `MAX_SHORTCUTS`)
- Android 7.1+ (API 25+) requerido
- Debounce de 2 minutos entre actualizaciones (omitido si cambia número de proyectos)
- Caché de 7 días (auto-limpieza)

## Troubleshooting

### Shortcuts no aparecen
- Verificar Android 7.1+
- Reinstalar app completamente
- Verificar permisos de almacenamiento

### Imágenes no se muestran
- Verificar que imageUri existe
- Revisar logs para errores de procesamiento
- Verificar permisos de lectura (especialmente para Content URIs)
- Limpiar caché: `ShortcutCache.clear()`
- Verificar si es Content URI o file path en los logs

### Performance issues
- Verificar tamaño de caché: `ShortcutCache.getCacheSize()`
- Ejecutar cleanup manual si es necesario
- Verificar debounce está activo

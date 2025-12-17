# Project Widget Implementation (Glance)

## 📋 Estructura del Widget

```
widget/
├── ProjectWidget.kt                     // Widget provider (Glance)
├── ProjectWidgetContent.kt              // Widget UI content (Glance Composable)
├── ProjectWidgetReceiver.kt             // Glance widget receiver
├── ProjectWidgetUpdateReceiver.kt       // Broadcast receiver para actualizaciones
├── ProjectWidgetConfigActivity.kt       // Activity de configuración
├── MANIFEST_INSTRUCTIONS.md             // Instrucciones para AndroidManifest
└── ui/
    ├── ProjectWidgetConfigScreen.kt     // UI de configuración (Compose)
    └── ProjectWidgetConfigViewModel.kt  // ViewModel de configuración
```

## 🎯 Funcionalidad

### Widget Semi-Estático
- Muestra **1 proyecto** seleccionado por el usuario
- Se actualiza automáticamente cuando:
  - Se marca/desmarca un material
  - Se elimina el proyecto
  - Cambia el progreso del proyecto
  - El usuario toca el widget (refresh manual)

### Configuración del Widget
- Al añadir el widget, se abre una pantalla de selección
- Muestra todos los proyectos del usuario
- Usa `ProjectCard` para mostrar cada proyecto
- Usa `SaveButton` para confirmar la selección

## 🔧 Componentes Implementados

### 1. WidgetPreferences
- Guarda la configuración del widget (projectId por widgetId)
- Usa SharedPreferences para persistencia
- Métodos:
  - `saveProjectIdForWidget(widgetId, projectId)`
  - `getProjectIdForWidget(widgetId)`
  - `deleteWidgetPreferences(widgetId)`

### 2. ProjectWidgetConfigViewModel
- Carga todos los proyectos del usuario
- Maneja la selección del proyecto
- Guarda la configuración del widget
- Estados:
  - `projects`: Lista de proyectos
  - `selectedProjectId`: Proyecto seleccionado
  - `isLoading`: Estado de carga
  - `errorMessage`: Mensajes de error

### 3. ProjectWidgetConfigScreen
- UI de configuración usando Compose
- Muestra lista de proyectos con `ProjectCard`
- Botón "Add Widget" con `SaveButton`
- Botón "Cancel" para cancelar

### 4. ProjectWidgetConfigActivity
- Activity que maneja la configuración del widget
- Recibe el `appWidgetId` del sistema
- Guarda la configuración y actualiza el widget
- Retorna `RESULT_OK` al sistema

### 5. ProjectWidget
- AppWidgetProvider principal
- Métodos:
  - `onUpdate()`: Actualiza widgets
  - `onDeleted()`: Limpia preferencias
  - `updateAppWidget()`: Actualiza un widget específico
  - `updateAllWidgets()`: Actualiza todos los widgets

### 6. ProjectWidgetReceiver
- BroadcastReceiver para actualizaciones
- Acciones:
  - `ACTION_UPDATE_WIDGET`: Actualizar por cambios en materiales
  - `ACTION_REFRESH_WIDGET`: Actualizar manualmente
- Método helper: `sendUpdateBroadcast(context)`

## 📝 Pendiente

### 1. Diseño del Widget UI
- Crear layout XML con tu diseño
- Implementar RemoteViews en `ProjectWidget.updateAppWidget()`

### 2. Integración con ViewModels
- Llamar `ProjectWidgetReceiver.sendUpdateBroadcast()` cuando:
  - Se marca/desmarca un material en `ProjectDetailsViewModel`
  - Se elimina un proyecto en `ProjectListViewModel`
  - Cambia el progreso del proyecto

### 3. AndroidManifest.xml
Agregar las siguientes declaraciones:

```xml
<!-- Widget Provider -->
<receiver
    android:name=".widget.ProjectWidget"
    android:exported="true">
    <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
    </intent-filter>
    <meta-data
        android:name="android.appwidget.provider"
        android:resource="@xml/project_widget_info" />
</receiver>

<!-- Widget Receiver -->
<receiver
    android:name=".widget.ProjectWidgetReceiver"
    android:exported="false">
    <intent-filter>
        <action android:name="com.br444n.constructionmaterialtrack.ACTION_UPDATE_WIDGET" />
        <action android:name="com.br444n.constructionmaterialtrack.ACTION_REFRESH_WIDGET" />
    </intent-filter>
</receiver>

<!-- Widget Config Activity -->
<activity
    android:name=".widget.ProjectWidgetConfigActivity"
    android:exported="true"
    android:theme="@style/Theme.ConstructionMaterialTrack">
    <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_CONFIGURE" />
    </intent-filter>
</activity>
```

### 4. Strings Resources
Agregar en `strings.xml`:

```xml
<string name="widget_description">Display project progress on your home screen</string>
```

## 🚀 Próximos Pasos

1. **Diseñar el Widget UI** - Proporcionar el diseño visual del widget
2. **Implementar RemoteViews** - Crear la UI del widget con los datos del proyecto
3. **Agregar al Manifest** - Registrar el widget y sus componentes
4. **Integrar actualizaciones** - Llamar al receiver cuando cambien los datos
5. **Testing** - Probar el widget en diferentes escenarios

## 📱 Uso del Widget

1. Usuario mantiene presionado en la pantalla de inicio
2. Selecciona "Widgets"
3. Encuentra "Project Widget"
4. Arrastra el widget a la pantalla
5. Se abre `ProjectWidgetConfigActivity`
6. Usuario selecciona un proyecto
7. Presiona "Add Widget"
8. Widget se muestra con el proyecto seleccionado

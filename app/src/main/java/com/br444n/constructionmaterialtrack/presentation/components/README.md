# Components Structure

Esta carpeta contiene todos los componentes reutilizables de la aplicación, organizados por categorías funcionales para mejorar la mantenibilidad y escalabilidad.

## Estructura de Carpetas

### 📁 buttons/
Componentes de botones y acciones:
- `ActionButton.kt` - Botón genérico con iconos y variantes
- `SaveButton.kt` - Botón principal para guardar con animaciones
- `SecondaryButton.kt` - Botón secundario con efectos visuales

### 📁 cards/
Componentes de tarjetas y contenedores:
- `EditableProjectCard.kt` - Tarjeta de proyecto editable
- `ProjectCard.kt` - Tarjeta básica de proyecto
- `ProjectInfoCard.kt` - Tarjeta de información del proyecto

### 📁 dialogs/
Componentes de diálogos y modales:
- `ConfirmationDialog.kt` - Diálogo de confirmación genérico
- `ImageSourceDialog.kt` - Diálogo para seleccionar origen de imagen (Cámara/Galería)
- `PermissionDeniedDialog.kt` - Diálogo cuando se niegan permisos
- `PdfSuccessDialog.kt` - Diálogo de éxito al generar PDF

### 📁 forms/
Componentes de formularios y entrada de datos:
- `CustomTextField.kt` - Campo de texto personalizado
- `MultilineTextField.kt` - Campo de texto multilínea
- `NumberTextField.kt` - Campo de texto para números

### 📁 images/
Componentes relacionados con imágenes:
- `ImagePicker.kt` - Selector de imágenes
- `ProjectImageDisplay.kt` - Visualizador de imágenes de proyecto

### 📁 lists/
Componentes de listas y elementos de lista:
- `MaterialItemRow.kt` - Fila de elemento de material

### 📁 navigation/
Componentes de navegación y barras superiores:
- `AppTopAppBar.kt` - Barra superior principal de la app
- `CustomTopAppBar.kt` - Barra superior personalizable
- `SelectionTopAppBar.kt` - Barra superior para modo selección

### 📁 states/
Componentes de estados de la aplicación:
- `EmptyMaterialsState.kt` - Estado vacío para materiales
- `EmptyState.kt` - Estado vacío genérico
- `ErrorContent.kt` - Contenido de error
- `ErrorMessage.kt` - Mensaje de error
- `LoadingIndicator.kt` - Indicador de carga
- `LoadingOverlay.kt` - Overlay de carga

### 📁 ui/
Componentes de interfaz de usuario generales:
- `SectionHeader.kt` - Encabezado de sección

## Importaciones

Para usar estos componentes, importa desde su nueva ubicación:

```text
// Botones
 import com.br444n.constructionmaterialtrack.presentation.components.buttons.SaveButton
 import com.br444n.constructionmaterialtrack.presentation.components.buttons.ActionButton


// Formularios
import com.br444n.constructionmaterialtrack.presentation.components.forms.CustomTextField
import com.br444n.constructionmaterialtrack.presentation.components.forms.NumberTextField


// Estados
import com.br444n.constructionmaterialtrack.presentation.components.states.LoadingIndicator
import com.br444n.constructionmaterialtrack.presentation.components.states.EmptyState
```

// Y así sucesivamente...


## Beneficios de esta Estructura

1. **Organización Clara**: Cada componente está en su categoría lógica
2. **Fácil Navegación**: Encontrar componentes específicos es más rápido
3. **Escalabilidad**: Agregar nuevos componentes es más estructurado
4. **Mantenibilidad**: Cambios y actualizaciones son más focalizados
5. **Reutilización**: Los componentes están mejor organizados para reutilización

## Convenciones

- Cada componente debe tener su propio archivo
- Los nombres de archivo deben ser descriptivos y en PascalCase
- Cada componente debe incluir un Preview para desarrollo
- Los packages deben reflejar la estructura de carpetas
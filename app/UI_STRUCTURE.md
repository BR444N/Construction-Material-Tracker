# Architect Project Manager - UI Structure

## 📱 Screens Overview

This Android app follows **MVVM + Clean Architecture** principles with **Jetpack Compose** for the UI layer.

### 🎨 Design System
- **Primary Color**: `BluePrimary = Color(0xFF1882FF)`
- **Material 3** components with rounded corners
- **Dark theme** support ready
- Clean, minimal design approach

### 📂 Project Structure

```
domain/
└── model/              # Domain models (Clean Architecture)
    ├── Project.kt      # Project data class
    └── Material.kt     # Material data class

presentation/
├── components/         # Reusable UI components
│   ├── ProjectCard.kt  # Card component for project list
│   └── MaterialItemRow.kt # Row component for material items
├── navigation/         # Navigation setup
│   └── Navigation.kt   # NavHost and navigation logic
└── screens/           # Screen implementations
    ├── project_list/
    │   └── ProjectListScreen.kt
    ├── add_project/
    │   └── AddProjectScreen.kt
    ├── add_material/
    │   └── AddMaterialScreen.kt
    └── project_details/
        └── ProjectDetailsScreen.kt
```

### 🚀 Implemented Screens

#### 1. **Project List Screen** (`ProjectListScreen.kt`)
- **Layout**: Scaffold with TopAppBar and FAB
- **Features**:
  - App branding in TopAppBar (icon + name)
  - Settings icon in TopAppBar
  - LazyColumn with project cards
  - Empty state with placeholder
  - FAB for adding new projects

#### 2. **Add Project Screen** (`AddProjectScreen.kt`)
- **Layout**: Scaffold with back navigation
- **Features**:
  - Image picker section with camera icon
  - Project name and description fields
  - "Add Materials" and "Save Project" buttons
  - Form validation (name required)

#### 3. **Add Material Screen** (`AddMaterialScreen.kt`)
- **Layout**: Scaffold with back navigation
- **Features**:
  - Material name, quantity, and price fields
  - Optional description field
  - Form validation (name, quantity, price required)
  - Save button

#### 4. **Project Details Screen** (`ProjectDetailsScreen.kt`)
- **Layout**: Scaffold with back navigation
- **Features**:
  - Project header with image and info
  - Materials list with checkboxes
  - Material purchase status tracking
  - "Export to PDF" button
  - Empty state for no materials

### 🧩 Reusable Components

#### **ProjectCard** (`components/ProjectCard.kt`)
- Displays project image, name, and description
- Handles click events
- Consistent styling with rounded corners

#### **MaterialItemRow** (`components/MaterialItemRow.kt`)
- Shows material details (name, quantity, price, description)
- Checkbox for purchase status
- Responsive layout

### 🧭 Navigation

The app uses **Navigation Compose** with the following routes:
- `project_list` - Main screen (start destination)
- `add_project` - New project form
- `add_material/{projectId}` - Add material to specific project
- `project_details/{projectId}` - Project details with materials

#### Navigation Architecture
- **Navigation.kt**: Clean navigation without hardcoded data
- **NavigationWithViewModels.kt**: Example implementation with proper MVVM integration
- All data flows through ViewModels following Clean Architecture principles

### 🎯 Key Features Implemented

✅ **Material 3 Design System**
✅ **Responsive Layouts**
✅ **Clean Navigation Architecture**
✅ **Form validation**
✅ **Empty states**
✅ **Reusable components**
✅ **Clean Architecture structure**
✅ **MVVM ViewModels prepared**
✅ **Preview support for all screens**

### 🏗️ Architecture Implementation

#### ViewModels Created:
- **ProjectListViewModel**: Manages project list state and loading
- **AddProjectViewModel**: Handles project creation and validation
- **ProjectDetailsViewModel**: Manages project details and material updates

#### Clean Navigation:
- No hardcoded sample data in navigation
- Proper separation of concerns
- ViewModels handle all business logic
- Navigation only handles routing

### ✅ **Recent Updates**

- **Domain Models**: Moved `Project` and `Material` data classes to `domain/model/` following Clean Architecture
- **Import Cleanup**: Updated all imports to use domain models instead of duplicated classes
- **Architecture Compliance**: Better separation of concerns with proper domain layer

### 🚧 TODO (Future Enhancements)

- [ ] Image picker implementation
- [ ] PDF export functionality  
- [ ] Settings screen
- [ ] Data persistence (Room database)
- [ ] Repository layer implementation
- [ ] Material cost calculations
- [ ] Project progress tracking
- [ ] Search and filter functionality
- [ ] Error handling and loading states
- [ ] Unit tests for ViewModels

### 📝 Implementation Notes

#### Current State:
- ✅ **Navigation**: Clean architecture without hardcoded data
- ✅ **ViewModels**: Basic structure created, ready for repository integration
- ✅ **UI Screens**: All screens implemented with proper state management
- ✅ **Domain Models**: Clean separation of concerns

#### Next Steps:
1. **Repository Layer**: Implement data layer with Room database
2. **Dependency Injection**: Add Hilt or manual DI for ViewModels
3. **State Management**: Connect ViewModels to repositories
4. **Error Handling**: Add proper error states and user feedback

### 🎨 Theme Integration

All screens use the custom theme defined in `ui/theme/`:
- **Colors**: BluePrimary, Secondary, AccentOrange
- **Typography**: Material 3 typography scale
- **Shapes**: Rounded corners (12dp for cards, 8dp for smaller elements)

The UI is ready for both light and dark themes with proper color schemes defined in the theme files.
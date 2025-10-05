# Claude UI Instructions – Architect Project Management App

Claude, your task is to generate Jetpack Compose UI code for this Android application.  
The app is a **Project Management Tool for Architects**. It uses **MVVM + Clean Architecture** principles.  
Follow these instructions carefully when creating the UI:

---

## 🎨 Design Guidelines
- **Primary color:** Defined in `ui/theme/Color.kt` as:
  ```kotlin
  val BluePrimary = Color(0xFF1882FF)

- Clean, minimal design with Material 3 components.
- Use **Scaffold** for main layouts.
- Rounded corners on cards and input fields.
- Keep UI elements accessible and consistent.

---

## 📱 Screens

### 1. Project List Screen
- Use a **Scaffold** layout with:
    - **TopAppBar**:
        - Left: app icon + app name (“Architect Project Manager”).
        - Right: settings icon.
    - **FloatingActionButton (FAB)** at bottom-right to add a new project.
- Content:
    - Display all projects as **Cards** in a vertical list.
    - Each card shows:
        - Project image (placeholder if empty).
        - Project name.
        - Short description.
    - If there are no projects, show a placeholder image and below it a short text instruction:  
      *“Click the + button to add your first project.”*

---

### 2. Add Project Screen
- TopAppBar: Back navigation icon + Title `"New Project"`.
- Content:
    - **Image Picker Icon** to upload/select a project image.
    - **OutlinedTextField** for:
        - Project name.
        - Project description.
    - **Button**: `"Add Materials"` → navigate to Add Material Screen.

---

### 3. Add Material Screen
- TopAppBar: Back navigation icon + Title `"Add Material"`.
- Content:
    - **OutlinedTextField** for:
        - Material name.
        - Quantity.
        - Price.
        - (Optional) Description.
    - **Save Button** to add material to the project.

---

### 4. Project Details Screen
- TopAppBar: Back navigation icon + Title with project name.
- Content:
    - Show project image, name, and description.
    - Show **Material List**:
        - Each item is a row with:
            - Material name.
            - Quantity + Price.
            - Checkbox → mark as purchased or not.
    - **Button `"Export to PDF"`** at the bottom.

---

## 🛠️ Additional Notes
- Use `ConstraintLayout` or `Column` with padding for structure.
- All lists should use **LazyColumn** for performance.
- Remember to prepare UI for **dark theme** support.
- Keep components modular (e.g., `ProjectCard`, `MaterialItemRow`).

---
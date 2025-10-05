# Construction Material Track - Documentation

## 📚 Documentation Index

### Architecture & Structure
- **[UI Structure](UI_STRUCTURE.md)** - Complete overview of screens, components, and navigation architecture
- **[MVVM Implementation](UI_STRUCTURE.md# architecture-implementation)** - ViewModels and Clean Architecture details

### Features Implementation
- **[Image Implementation](IMAGE_IMPLEMENTATION.md)** - How project images are handled and displayed
- **[Project Editing](PROJECT_EDITING.md)** - In-place editing of project details with image support
- **[Persistent Permissions](PERSISTENT_PERMISSIONS.md)** - Robust image URI permissions across app sessions
- **[Android 14+ Permissions](ANDROID_14_PERMISSIONS.md)** - Selected Photos Access implementation

### Technical Details
- **[Permission Management](PERSISTENT_PERMISSIONS.md#permission-utilities)** - Comprehensive permission handling utilities
- **[Error Handling](PERSISTENT_PERMISSIONS.md#troubleshooting)** - Common issues and solutions

## 🏗️ Project Overview

This is an Android app for architects to manage construction materials across multiple projects, built with:

- **Jetpack Compose** for modern UI
- **MVVM + Clean Architecture** for maintainable code
- **Room Database** for local data persistence
- **Material 3 Design System** for consistent UI
- **Android 14+ Selected Photos Access** for privacy-focused image handling

## 🚀 Key Features

- ✅ Project management with images
- ✅ In-place project editing with image updates
- ✅ Material tracking with quantities and prices
- ✅ Persistent image permissions
- ✅ Cross-session data persistence
- ✅ Modern Material 3 UI
- ✅ Multi-version Android compatibility

## 📱 Screens

1. **Project List** - Overview of all projects
2. **Add Project** - Create new projects with images
3. **Project Details** - View project info and materials
4. **Add Material** - Add materials to projects

## 🔧 Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Database**: Room
- **Image Loading**: Coil
- **Navigation**: Navigation Compose
- **Design System**: Material 3

## 📖 Getting Started

1. Review the [UI Structure](UI_STRUCTURE.md) for overall architecture
2. Check [Image Implementation](IMAGE_IMPLEMENTATION.md) for image handling
3. Understand [Persistent Permissions](PERSISTENT_PERMISSIONS.md) for robust permission management
4. See [Android 14+ Permissions](ANDROID_14_PERMISSIONS.md) for modern permission handling
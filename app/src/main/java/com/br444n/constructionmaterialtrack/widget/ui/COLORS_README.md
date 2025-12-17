# Colors Resource File

This file contains all the colors used in the Construction Material Track app, including widget-specific colors.

## Color Palette

### Primary Colors
- **blue_primary** (#FF1882FF) - Main brand color
- **blue_dark** (#FF0D6ADC) - Darker variant
- **blue_light** (#FF5AA8FF) - Lighter variant

### Secondary Colors
- **secondary** (#FF2ECC71) - Emerald green (for completed items)
- **secondary_light** (#FF58D68D) - Lighter green variant

### Background Colors
- **background_light** (#FFF9F9F9) - Light background
- **surface_light** (#FFFFFFFF) - Light surface (app bar)
- **background_dark** (#FF121212) - Dark background
- **surface_dark** (#FF1E1E1E) - Dark surface

### Text Colors
- **black** (#FF000000) - Pure black
- **white** (#FFFFFFFF) - Pure white
- **text_secondary** (#FF666666) - Secondary text
- **on_surface_variant** (#FFF9FDFE) - Text on blue surface
- **red_light** (#FFE74C3C) - Light red
- **red** (#FFFF4444) - Standard red

### Glass Progress Bar Colors
- **glass_track** (#20FFFFFF) - Glass container body (12% opacity white)
- **glass_reflection** (#50FFFFFF) - Specular light reflections (31% opacity white)
- **glass_inner_glow** (#30FFFFFF) - Inner glow effect for glass depth (19% opacity white)
- **glass_shadow** (#40000000) - Subtle shadow for volume (25% opacity black)

### Widget Specific Colors
- **widget_background** (#FF1882FF) - Widget background (blue primary)
- **widget_text_primary** (#FFFFFFFF) - Primary text on widget (white)
- **widget_text_secondary** (#E0E0E0) - Secondary text on widget (light gray)
- **widget_progress_track** (#40FFFFFF) - Progress ring track (25% opacity white)
- **widget_progress_fill** (#FFFFFFFF) - Progress ring fill (white)

### Glance Widget Colors (New Glass Effect System)
- **glance_widget_background** (#FF1882FF) - Glance widget background (blue primary)
- **glance_widget_content** (#FFFFFFFF) - Glance widget content/text (white)

## Usage

### In XML Layouts
```xml
<View
    android:background="@color/blue_primary"
    android:textColor="@color/widget_text_primary" />
```

### In Glance Widgets
```kotlin
// Background
val bgColorInt = context.getColor(R.color.glance_widget_background)
val bgColor = Color(bgColorInt)
ColorProvider(day = bgColor, night = bgColor)

// Glass effect colors for WidgetBitmapUtils
val trackColorInt = context.getColor(R.color.glass_track)
val reflectionColorInt = context.getColor(R.color.glass_reflection)
val innerGlowColorInt = context.getColor(R.color.glass_inner_glow)
val shadowColorInt = context.getColor(R.color.glass_shadow)
```

### In Compose (from theme)
```kotlin
MaterialTheme.colorScheme.primary // Maps to blue_primary
```

## Glass Effect Implementation

The glass effect system uses multiple layers to create a realistic glass container with liquid:

### Glass Container Layers (in order):
1. **Glass Track** (`glass_track`) - The main glass tube body
2. **Glass Borders** - Subtle white borders (inner/outer) for definition
3. **Glass Reflections** (`glass_reflection`) - Specular highlights simulating light
4. **Glass Shadow** (`glass_shadow`) - Depth and volume shadows

### Liquid Effect:
- **Content Color** (`glance_widget_content`) - The liquid inside the glass
- **Inner Glow** (`glass_inner_glow`) - Glow effect around the liquid
- **Shadow Layer** - Applied to liquid for realistic glow

### Usage in WidgetBitmapUtils:
```kotlin
WidgetBitmapUtils.createCircularProgressBitmap(
    progress = progress,
    sizePx = 300,
    color = contentColorInt,           // Liquid color
    trackColor = trackColorInt,        // Glass container
    reflectionColor = reflectionColorInt, // Glass reflections
    innerGlowColor = innerGlowColorInt,   // Inner glow
    shadowColor = shadowColorInt       // Shadows
)
```

## Consistency

All colors are synchronized between:
- `ui/theme/Color.kt` (Compose)
- `res/values/colors.xml` (XML/Widgets)
- `widget/ui/WidgetBitmapUtils.kt` (Glass effect system)
- Widget drawables (legacy)

When updating colors, make sure to update all files to maintain consistency across the app.

## Migration Notes

The new glass effect system (`glance_widget_*` and `glass_*` colors) replaces the old static drawable system for widgets. The old `widget_*` colors are kept for backward compatibility but new implementations should use the glass effect system.

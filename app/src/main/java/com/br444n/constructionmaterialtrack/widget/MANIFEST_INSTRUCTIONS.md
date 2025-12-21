# Widget Configuration in AndroidManifest.xml

This document explains the widget configuration in the AndroidManifest.xml file.

## Widget Receiver Configuration

The widget receiver must be declared in the AndroidManifest.xml with the following configuration:

```xml
<!-- Widget Receiver (Glance) -->
<receiver
    android:name=".widget.ProjectWidgetReceiver"
    android:exported="true">
    <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
    </intent-filter>
    <meta-data
        android:name="android.appwidget.provider"
        android:resource="@xml/project_widget_info" />
</receiver>

<!-- Widget Update Receiver -->
<receiver
    android:name=".widget.ProjectWidgetUpdateReceiver"
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

## Key Points

1. **ProjectWidgetReceiver**: Main widget receiver that handles widget updates
   - `android:exported="true"` is required for system to access it
   - Must include `APPWIDGET_UPDATE` action in intent-filter
   - References `@xml/project_widget_info` for widget configuration

2. **ProjectWidgetUpdateReceiver**: Custom receiver for manual widget updates
   - `android:exported="false"` for internal use only
   - Handles custom actions for widget refresh

3. **ProjectWidgetConfigActivity**: Configuration activity for widget setup
   - `android:exported="true"` required for system to launch it
   - Must include `APPWIDGET_CONFIGURE` action in intent-filter
   - Uses app theme for consistent UI

## Widget Info XML

The `@xml/project_widget_info` file should contain:

```xml
<?xml version="1.0" encoding="utf-8"?>
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:configure="com.br444n.constructionmaterialtrack.widget.ProjectWidgetConfigActivity"
    android:description="@string/widget_description"
    android:minWidth="250dp"
    android:minHeight="180dp"
    android:previewImage="@drawable/ic_launcher_foreground"
    android:resizeMode="horizontal|vertical"
    android:updatePeriodMillis="300000"
    android:widgetCategory="home_screen" />
```

## Important Notes

- Update period is set to 5 minutes (300000ms) for frequent updates
- Widget is resizable both horizontally and vertically
- Configuration activity is required for widget setup
- Custom update receiver allows manual widget refresh when data changes
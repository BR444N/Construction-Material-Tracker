package com.br444n.constructionmaterialtrack.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class WidgetPreferences(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    fun saveProjectIdForWidget(widgetId: Int, projectId: String) {
        val key = getProjectIdKey(widgetId)
        android.util.Log.d("WidgetPreferences", "Saving - Key: '$key', ProjectId: '$projectId'")
        
        prefs.edit {
            putString(key, projectId)
        }
            
        android.util.Log.d("WidgetPreferences", "Save completed")
    }
    
    fun getProjectIdForWidget(widgetId: Int): String? {
        val key = getProjectIdKey(widgetId)
        val result = prefs.getString(key, null)
        android.util.Log.d("WidgetPreferences", "Getting - Key: '$key', Result: '$result'")
        return result
    }
    
    // Cache methods for widget data
    fun cacheWidgetData(widgetId: Int, projectName: String, progress: Float, completed: Int, total: Int) {
        android.util.Log.d("WidgetPreferences", "Caching widget data for widget $widgetId")
        prefs.edit {
            putString(getCacheProjectNameKey(widgetId), projectName)
            putFloat(getCacheProgressKey(widgetId), progress)
            putInt(getCacheCompletedKey(widgetId), completed)
            putInt(getCacheTotalKey(widgetId), total)
            putLong(getCacheTimestampKey(widgetId), System.currentTimeMillis())
        }
    }
    
    fun getCachedWidgetData(widgetId: Int): CachedWidgetData? {
        val projectName = prefs.getString(getCacheProjectNameKey(widgetId), null)
        val progress = prefs.getFloat(getCacheProgressKey(widgetId), -1f)
        val completed = prefs.getInt(getCacheCompletedKey(widgetId), -1)
        val total = prefs.getInt(getCacheTotalKey(widgetId), -1)
        val timestamp = prefs.getLong(getCacheTimestampKey(widgetId), -1L)
        
        return if (projectName != null && progress >= 0 && completed >= 0 && total >= 0 && timestamp > 0) {
            android.util.Log.d("WidgetPreferences", "Found cached data for widget $widgetId: $projectName")
            CachedWidgetData(projectName, progress, completed, total, timestamp)
        } else {
            android.util.Log.d("WidgetPreferences", "No cached data found for widget $widgetId")
            null
        }
    }
    
    fun clearCachedWidgetData(widgetId: Int) {
        prefs.edit {
            remove(getCacheProjectNameKey(widgetId))
            remove(getCacheProgressKey(widgetId))
            remove(getCacheCompletedKey(widgetId))
            remove(getCacheTotalKey(widgetId))
            remove(getCacheTimestampKey(widgetId))
        }
    }
    
    fun hasWidgetConfiguration(widgetId: Int): Boolean {
        return prefs.contains(getProjectIdKey(widgetId))
    }
    
    /**
     * Get all configured widget IDs
     */
    fun getAllConfiguredWidgetIds(): List<Int> {
        val widgetIds = mutableListOf<Int>()
        val allKeys = prefs.all.keys.toSet()
        
        allKeys.forEach { key ->
            if (key.startsWith("${KEY_PROJECT_ID}_")) {
                try {
                    val widgetId = key.substringAfter("${KEY_PROJECT_ID}_").toInt()
                    widgetIds.add(widgetId)
                } catch (_: NumberFormatException) {
                    android.util.Log.w("WidgetPreferences", "Invalid widget ID in key: $key")
                }
            }
        }
        
        android.util.Log.d("WidgetPreferences", "Found ${widgetIds.size} configured widgets: $widgetIds")
        return widgetIds.toList() // Make immutable
    }
    
    /**
     * Clear all cached widget data for all widgets
     */
    fun clearAllCachedData() {
        val configuredWidgets = getAllConfiguredWidgetIds()
        android.util.Log.d("WidgetPreferences", "Clearing cache for ${configuredWidgets.size} widgets")
        
        prefs.edit {
            configuredWidgets.forEach { widgetId ->
                remove(getCacheProjectNameKey(widgetId))
                remove(getCacheProgressKey(widgetId))
                remove(getCacheCompletedKey(widgetId))
                remove(getCacheTotalKey(widgetId))
                remove(getCacheTimestampKey(widgetId))
            }
        }
        
        android.util.Log.d("WidgetPreferences", "All widget caches cleared")
    }
    
    /**
     * Get widget IDs that are configured for a specific project
     */
    fun getWidgetIdsForProject(projectId: String): List<Int> {
        val widgetIds = mutableListOf<Int>()
        val allConfiguredWidgets = getAllConfiguredWidgetIds()
        
        allConfiguredWidgets.forEach { widgetId ->
            val configuredProjectId = getProjectIdForWidget(widgetId)
            if (configuredProjectId == projectId) {
                widgetIds.add(widgetId)
            }
        }
        
        android.util.Log.d("WidgetPreferences", "Found ${widgetIds.size} widgets for project $projectId: $widgetIds")
        return widgetIds
    }
    
    /**
     * Clear cached data for widgets showing a specific project
     */
    fun clearCachedDataForProject(projectId: String) {
        val widgetIds = getWidgetIdsForProject(projectId)
        android.util.Log.d("WidgetPreferences", "Clearing cache for ${widgetIds.size} widgets showing project $projectId")
        
        prefs.edit {
            widgetIds.forEach { widgetId ->
                remove(getCacheProjectNameKey(widgetId))
                remove(getCacheProgressKey(widgetId))
                remove(getCacheCompletedKey(widgetId))
                remove(getCacheTotalKey(widgetId))
                remove(getCacheTimestampKey(widgetId))
            }
        }
        
        android.util.Log.d("WidgetPreferences", "Cache cleared for widgets showing project $projectId")
    }
    
    private fun getProjectIdKey(widgetId: Int): String {
        return "${KEY_PROJECT_ID}_$widgetId"
    }
    
    private fun getCacheProjectNameKey(widgetId: Int): String {
        return "${KEY_CACHE_PROJECT_NAME}_$widgetId"
    }
    
    private fun getCacheProgressKey(widgetId: Int): String {
        return "${KEY_CACHE_PROGRESS}_$widgetId"
    }
    
    private fun getCacheCompletedKey(widgetId: Int): String {
        return "${KEY_CACHE_COMPLETED}_$widgetId"
    }
    
    private fun getCacheTotalKey(widgetId: Int): String {
        return "${KEY_CACHE_TOTAL}_$widgetId"
    }
    
    private fun getCacheTimestampKey(widgetId: Int): String {
        return "${KEY_CACHE_TIMESTAMP}_$widgetId"
    }
    
    companion object {
        private const val PREFS_NAME = "com.br444n.constructionmaterialtrack.widget"
        private const val KEY_PROJECT_ID = "project_id"
        private const val KEY_CACHE_PROJECT_NAME = "cache_project_name"
        private const val KEY_CACHE_PROGRESS = "cache_progress"
        private const val KEY_CACHE_COMPLETED = "cache_completed"
        private const val KEY_CACHE_TOTAL = "cache_total"
        private const val KEY_CACHE_TIMESTAMP = "cache_timestamp"
    }
}

data class CachedWidgetData(
    val projectName: String,
    val progress: Float,
    val completedMaterials: Int,
    val totalMaterials: Int,
    val timestamp: Long
)
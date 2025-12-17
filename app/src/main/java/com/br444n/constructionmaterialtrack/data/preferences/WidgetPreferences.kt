package com.br444n.constructionmaterialtrack.data.preferences

import android.content.Context
import android.content.SharedPreferences

class WidgetPreferences(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    fun saveProjectIdForWidget(widgetId: Int, projectId: String) {
        prefs.edit()
            .putString(getProjectIdKey(widgetId), projectId)
            .apply()
    }
    
    fun getProjectIdForWidget(widgetId: Int): String? {
        return prefs.getString(getProjectIdKey(widgetId), null)
    }
    
    fun deleteWidgetPreferences(widgetId: Int) {
        prefs.edit()
            .remove(getProjectIdKey(widgetId))
            .apply()
    }
    
    fun hasWidgetConfiguration(widgetId: Int): Boolean {
        return prefs.contains(getProjectIdKey(widgetId))
    }
    
    private fun getProjectIdKey(widgetId: Int): String {
        return "${KEY_PROJECT_ID}_$widgetId"
    }
    
    companion object {
        private const val PREFS_NAME = "com.br444n.constructionmaterialtrack.widget"
        private const val KEY_PROJECT_ID = "project_id"
    }
}
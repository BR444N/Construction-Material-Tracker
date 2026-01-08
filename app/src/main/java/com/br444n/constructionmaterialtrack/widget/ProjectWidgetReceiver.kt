package com.br444n.constructionmaterialtrack.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Broadcast receiver to handle widget update events
 */
class ProjectWidgetUpdateReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        android.util.Log.d("ProjectWidgetUpdate", "Received broadcast: ${intent.action}")
        
        when (intent.action) {
            ACTION_UPDATE_WIDGET, ACTION_REFRESH_WIDGET -> {
                android.util.Log.d("ProjectWidgetUpdate", "Updating all widgets...")
                // Update all widgets when materials are changed
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Force update all widgets
                        ProjectWidget().updateAll(context)
                        android.util.Log.d("ProjectWidgetUpdate", "All widgets updated successfully")
                        
                        // Additional update with delay to ensure database consistency
                        kotlinx.coroutines.delay(500)
                        ProjectWidget().updateAll(context)
                        android.util.Log.d("ProjectWidgetUpdate", "Secondary widget update completed")
                        
                    } catch (e: Exception) {
                        android.util.Log.e("ProjectWidgetUpdate", "Error updating widgets", e)
                    }
                }
            }
            ACTION_FORCE_REFRESH_WIDGET -> {
                android.util.Log.d("ProjectWidgetUpdate", "Force refreshing all widgets (clearing cache)...")
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        // Clear all widget caches before updating
                        clearAllWidgetCaches(context)
                        
                        // Force update all widgets
                        ProjectWidget().updateAll(context)
                        android.util.Log.d("ProjectWidgetUpdate", "Force refresh completed successfully")
                        
                    } catch (e: Exception) {
                        android.util.Log.e("ProjectWidgetUpdate", "Error force refreshing widgets", e)
                    }
                }
            }
        }
    }
    
    private fun clearAllWidgetCaches(context: Context) {
        try {
            val widgetPreferences = com.br444n.constructionmaterialtrack.data.preferences.WidgetPreferences(context)
            
            // Clear all cached widget data
            widgetPreferences.clearAllCachedData()
            
            android.util.Log.d("ProjectWidgetUpdate", "All widget caches cleared successfully")
        } catch (e: Exception) {
            android.util.Log.e("ProjectWidgetUpdate", "Error clearing widget caches", e)
        }
    }
    
    companion object {
        const val ACTION_UPDATE_WIDGET = "com.br444n.constructionmaterialtrack.ACTION_UPDATE_WIDGET"
        const val ACTION_REFRESH_WIDGET = "com.br444n.constructionmaterialtrack.ACTION_REFRESH_WIDGET"
        const val ACTION_FORCE_REFRESH_WIDGET = "com.br444n.constructionmaterialtrack.ACTION_FORCE_REFRESH_WIDGET"
        
        /**
         * Send broadcast to update widgets
         */
        fun sendUpdateBroadcast(context: Context) {
            android.util.Log.d("ProjectWidgetUpdate", "Sending update broadcast")
            val intent = Intent(ACTION_UPDATE_WIDGET)
            intent.setPackage(context.packageName)
            context.sendBroadcast(intent)
        }
        
        /**
         * Send broadcast to force refresh widgets (clears cache)
         */
        fun sendForceRefreshBroadcast(context: Context) {
            android.util.Log.d("ProjectWidgetUpdate", "Sending force refresh broadcast")
            val intent = Intent(ACTION_FORCE_REFRESH_WIDGET)
            intent.setPackage(context.packageName)
            context.sendBroadcast(intent)
        }
        
        /**
         * Send broadcast to update widgets with delay
         */
        fun sendDelayedUpdateBroadcast(context: Context, delayMillis: Long = 1000) {
            CoroutineScope(Dispatchers.IO).launch {
                kotlinx.coroutines.delay(delayMillis)
                sendUpdateBroadcast(context)
            }
        }
        
        /**
         * Send delayed force refresh broadcast
         */
        fun sendDelayedForceRefreshBroadcast(context: Context, delayMillis: Long = 1000) {
            CoroutineScope(Dispatchers.IO).launch {
                kotlinx.coroutines.delay(delayMillis)
                sendForceRefreshBroadcast(context)
            }
        }
    }
}
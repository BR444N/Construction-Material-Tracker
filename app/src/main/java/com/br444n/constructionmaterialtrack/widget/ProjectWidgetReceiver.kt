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
        when (intent.action) {
            ACTION_UPDATE_WIDGET, ACTION_REFRESH_WIDGET -> {
                // Update all widgets when materials are changed
                CoroutineScope(Dispatchers.IO).launch {
                    ProjectWidget().updateAll(context)
                }
            }
        }
    }
    
    companion object {
        const val ACTION_UPDATE_WIDGET = "com.br444n.constructionmaterialtrack.ACTION_UPDATE_WIDGET"
        const val ACTION_REFRESH_WIDGET = "com.br444n.constructionmaterialtrack.ACTION_REFRESH_WIDGET"
        
        /**
         * Send broadcast to update widgets
         */
        fun sendUpdateBroadcast(context: Context) {
            val intent = Intent(ACTION_UPDATE_WIDGET)
            intent.setPackage(context.packageName)
            context.sendBroadcast(intent)
        }
    }
}
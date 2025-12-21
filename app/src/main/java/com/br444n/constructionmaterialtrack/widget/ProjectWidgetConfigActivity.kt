package com.br444n.constructionmaterialtrack.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.br444n.constructionmaterialtrack.ui.theme.ConstructionMaterialTrackTheme
import com.br444n.constructionmaterialtrack.widget.ui.ProjectWidgetConfigScreen
import com.br444n.constructionmaterialtrack.widget.ui.ProjectWidgetConfigViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class ProjectWidgetConfigActivity : ComponentActivity() {
    
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private val viewModel: ProjectWidgetConfigViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set the result to CANCELED. This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(Activity.RESULT_CANCELED)
        
        // Find the widget id from the intent
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        
        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
        
        setContent {
            ConstructionMaterialTrackTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ProjectWidgetConfigScreen(
                        viewModel = viewModel,
                        onSaveClick = { handleSaveClick() },
                        onCancelClick = { handleCancelClick() }
                    )
                }
            }
        }
    }
    
    private fun handleSaveClick() {
        android.util.Log.d("ProjectWidgetConfig", "=== SAVE CLICK STARTED ===")
        android.util.Log.d("ProjectWidgetConfig", "Widget ID: $appWidgetId")
        
        // Move all operations to background thread to avoid ANR
        CoroutineScope(Dispatchers.IO).launch {
            try {
                android.util.Log.d("ProjectWidgetConfig", "Saving widget configuration...")
                val success = viewModel.saveWidgetConfiguration(appWidgetId)
                
                if (success) {
                    android.util.Log.d("ProjectWidgetConfig", "Configuration saved successfully, updating widget...")
                    
                    // Wait a bit for the database to be ready
                    kotlinx.coroutines.delay(1000)
                    
                    // Update the widget using Glance with multiple attempts
                    try {
                        val glanceAppWidgetManager = androidx.glance.appwidget.GlanceAppWidgetManager(this@ProjectWidgetConfigActivity)
                        val glanceId = glanceAppWidgetManager.getGlanceIdBy(appWidgetId)
                        
                        android.util.Log.d("ProjectWidgetConfig", "Updating widget with ID: $appWidgetId, GlanceId: $glanceId")
                        
                        // Force update multiple times to ensure it works
                        repeat(3) { attempt ->
                            try {
                                android.util.Log.d("ProjectWidgetConfig", "Update attempt ${attempt + 1}")
                                ProjectWidget().update(this@ProjectWidgetConfigActivity, glanceId)
                                kotlinx.coroutines.delay(500) // Small delay between attempts
                            } catch (e: Exception) {
                                android.util.Log.e("ProjectWidgetConfig", "Update attempt ${attempt + 1} failed", e)
                            }
                        }
                        
                        // Also try to update using AppWidgetManager
                        try {
                            android.util.Log.d("ProjectWidgetConfig", "Triggering AppWidgetManager update...")
                            val appWidgetManager = AppWidgetManager.getInstance(this@ProjectWidgetConfigActivity)
                            val intent = Intent(this@ProjectWidgetConfigActivity, ProjectWidgetReceiver::class.java).apply {
                                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
                            }
                            sendBroadcast(intent)
                            
                            // Also trigger a manual update after a delay
                            kotlinx.coroutines.delay(1000)
                            sendBroadcast(intent)
                            
                            // Use the custom update receiver as well
                            ProjectWidgetUpdateReceiver.sendUpdateBroadcast(this@ProjectWidgetConfigActivity)
                            ProjectWidgetUpdateReceiver.sendDelayedUpdateBroadcast(this@ProjectWidgetConfigActivity, 2000)
                        } catch (e: Exception) {
                            android.util.Log.e("ProjectWidgetConfig", "AppWidgetManager update failed", e)
                        }
                        
                        android.util.Log.d("ProjectWidgetConfig", "All widget updates completed")
                    } catch (e: Exception) {
                        android.util.Log.e("ProjectWidgetConfig", "Failed to update widget", e)
                    }
                    
                    // Switch back to main thread for UI operations
                    runOnUiThread {
                        android.util.Log.d("ProjectWidgetConfig", "Setting result and finishing activity")
                        // Make sure we pass back the original appWidgetId
                        val resultValue = Intent().apply {
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                        }
                        setResult(Activity.RESULT_OK, resultValue)
                        finish()
                    }
                } else {
                    android.util.Log.e("ProjectWidgetConfig", "Failed to save widget configuration")
                    runOnUiThread {
                        // Handle error case on main thread
                        finish()
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("ProjectWidgetConfig", "Error in handleSaveClick", e)
                runOnUiThread {
                    finish()
                }
            }
        }
        android.util.Log.d("ProjectWidgetConfig", "=== SAVE CLICK BACKGROUND TASK STARTED ===")
    }
    
    private fun handleCancelClick() {
        finish()
    }
}
package com.br444n.constructionmaterialtrack.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.delay

/**
 * Implementation of App Widget functionality using Glance.
 * App Widget Configuration implemented in [ProjectWidgetConfigActivity]
 */
class ProjectWidget : GlanceAppWidget() {
    
    // Define state definition - CRITICAL for widget persistence
    override val stateDefinition: GlanceStateDefinition<Preferences> = PreferencesGlanceStateDefinition
    
    override suspend fun provideGlance(context: Context, id: androidx.glance.GlanceId) {
        android.util.Log.d("ProjectWidget", "provideGlance called for id: $id")
        
        try {
            // Load widget data with fallback to cached data
            val widgetData = loadWidgetDataWithFallback(context, id)
            
            provideContent {
                ProjectWidgetContent(context, widgetData)
            }
        } catch (e: Exception) {
            android.util.Log.e("ProjectWidget", "Error in provideGlance", e)
            // Provide fallback content
            provideContent {
                ProjectWidgetContent(context, WidgetData())
            }
        }
    }
    
    private suspend fun loadWidgetDataWithFallback(context: Context, glanceId: androidx.glance.GlanceId): WidgetData {
        return try {
            android.util.Log.d("ProjectWidget", "=== Starting loadWidgetDataWithFallback ===")
            android.util.Log.d("ProjectWidget", "GlanceId: $glanceId")
            
            val widgetPreferences = com.br444n.constructionmaterialtrack.data.preferences.WidgetPreferences(context)
            val glanceAppWidgetManager = androidx.glance.appwidget.GlanceAppWidgetManager(context)
            
            // Try to get the app widget ID from the glance ID
            val appWidgetId = try {
                val id = glanceAppWidgetManager.getAppWidgetId(glanceId)
                android.util.Log.d("ProjectWidget", "Successfully got app widget ID: $id")
                id
            } catch (e: Exception) {
                android.util.Log.e("ProjectWidget", "Failed to get app widget ID from glance ID", e)
                return tryGetCachedData(widgetPreferences, -1) ?: WidgetData()
            }
            
            // Validate widget ID
            if (appWidgetId <= 0) {
                android.util.Log.w("ProjectWidget", "Invalid widget ID: $appWidgetId")
                return tryGetCachedData(widgetPreferences, appWidgetId) ?: WidgetData()
            }
            
            // Check if widget has configuration with retry
            var hasConfig = false
            var projectId: String? = null
            
            // Try multiple times to get configuration (in case it's still being saved)
            repeat(3) { attempt ->
                hasConfig = widgetPreferences.hasWidgetConfiguration(appWidgetId)
                projectId = widgetPreferences.getProjectIdForWidget(appWidgetId)
                
                android.util.Log.d("ProjectWidget", "Configuration check attempt ${attempt + 1}: hasConfig=$hasConfig, projectId='$projectId'")
                
                if (hasConfig && !projectId.isNullOrEmpty()) {
                    return@repeat // Found configuration, exit loop
                }
                
                if (attempt < 2) {
                    // Wait a bit before retrying
                    delay(1000)
                }
            }
            
            android.util.Log.d("ProjectWidget", "Final configuration state: hasConfig=$hasConfig, projectId='$projectId'")
            
            if (projectId.isNullOrEmpty()) {
                android.util.Log.d("ProjectWidget", "No project ID found - widget not configured or project deleted")
                // Don't try cached data if no project ID - return empty widget data
                return WidgetData()
            }
            
            // Load data with retry mechanism, fallback to cache
            return loadProjectDataWithRetry(context, projectId, appWidgetId, maxRetries = 3)
            
        } catch (e: Exception) {
            android.util.Log.e("ProjectWidget", "Error loading widget data", e)
            WidgetData()
        }
    }
    
    private suspend fun loadProjectDataWithRetry(
        context: Context, 
        projectId: String,
        appWidgetId: Int,
        maxRetries: Int = 2
    ): WidgetData {
        var lastException: Exception? = null
        val widgetPreferences = com.br444n.constructionmaterialtrack.data.preferences.WidgetPreferences(context)
        
        // Clear cached data to force fresh load
        widgetPreferences.clearCachedWidgetData(appWidgetId)
        android.util.Log.d("ProjectWidget", "Cleared cached data for widget $appWidgetId to force fresh load")
        
        repeat(maxRetries) { attempt ->
            try {
                android.util.Log.d("ProjectWidget", "Attempt ${attempt + 1} to load project data")
                
                // Get a fresh database instance for each attempt
                val database = com.br444n.constructionmaterialtrack.data.local.database.ConstructionDatabase.getDatabase(context)
                val projectRepository = com.br444n.constructionmaterialtrack.data.repository.ProjectRepositoryImpl(database.projectDao())
                val materialRepository = com.br444n.constructionmaterialtrack.data.repository.MaterialRepositoryImpl(database.materialDao())
                
                android.util.Log.d("ProjectWidget", "Fetching project with ID: $projectId")
                val project = projectRepository.getProjectById(projectId)
                
                if (project == null) {
                    android.util.Log.w("ProjectWidget", "Project not found for ID: $projectId - Project may have been deleted")
                    
                    // Clear widget configuration since project no longer exists
                    widgetPreferences.clearWidgetConfiguration(appWidgetId)
                    android.util.Log.d("ProjectWidget", "Cleared widget configuration for deleted project")
                    
                    // Return empty widget data to show "No Project Selected"
                    return WidgetData()
                }
                
                android.util.Log.d("ProjectWidget", "Project found: ${project.name} (ID: ${project.id})")
                
                // Get materials with timeout - force fresh data
                android.util.Log.d("ProjectWidget", "Fetching materials for project...")
                val materials = try {
                    withTimeoutOrNull(5000) { // Increased timeout for fresh data
                        val materialFlow = materialRepository.getMaterialsByProjectId(projectId)
                        android.util.Log.d("ProjectWidget", "Got material flow, collecting fresh data...")
                        val result = materialFlow.first()
                        android.util.Log.d("ProjectWidget", "Fresh materials collected: ${result.size} items")
                        
                        // Log each material for debugging
                        result.forEachIndexed { index, material ->
                            android.util.Log.d("ProjectWidget", "Material $index: ${material.name} - Purchased: ${material.isPurchased}")
                        }
                        
                        result
                    } ?: run {
                        android.util.Log.w("ProjectWidget", "Timeout occurred while fetching materials")
                        emptyList()
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ProjectWidget", "Error fetching materials on attempt ${attempt + 1}", e)
                    if (attempt == maxRetries - 1) {
                        // Last attempt failed, try cached data
                        android.util.Log.d("ProjectWidget", "Last attempt failed, trying cached data")
                        return tryGetCachedData(widgetPreferences, appWidgetId) ?: WidgetData()
                    }
                    emptyList()
                }
                
                val totalMaterials = materials.size
                val completedMaterials = materials.count { it.isPurchased }
                val progress = if (totalMaterials > 0) {
                    completedMaterials.toFloat() / totalMaterials.toFloat()
                } else {
                    0f
                }
                
                android.util.Log.d("ProjectWidget", "=== FRESH CALCULATION DETAILS ===")
                android.util.Log.d("ProjectWidget", "Project: ${project.name}")
                android.util.Log.d("ProjectWidget", "Total materials: $totalMaterials")
                android.util.Log.d("ProjectWidget", "Completed materials: $completedMaterials")
                android.util.Log.d("ProjectWidget", "Progress: $progress (${(progress * 100).toInt()}%)")
                android.util.Log.d("ProjectWidget", "=== END FRESH CALCULATION ===")
                
                // Cache the fresh data
                widgetPreferences.cacheWidgetData(
                    appWidgetId, 
                    project.name, 
                    progress, 
                    completedMaterials, 
                    totalMaterials
                )
                android.util.Log.d("ProjectWidget", "Fresh data cached successfully for widget $appWidgetId")
                
                val result = WidgetData(
                    project = project,
                    materials = materials,
                    progress = progress,
                    completedMaterials = completedMaterials,
                    totalMaterials = totalMaterials
                )
                
                android.util.Log.d("ProjectWidget", "=== loadWidgetData completed successfully with FRESH data on attempt ${attempt + 1} ===")
                return result
                
            } catch (e: Exception) {
                lastException = e
                android.util.Log.e("ProjectWidget", "Attempt ${attempt + 1} failed", e)
                
                if (attempt < maxRetries - 1) {
                    // Wait before retry
                    delay(1000L * (attempt + 1)) // Progressive delay with Long
                    android.util.Log.d("ProjectWidget", "Retrying in ${1000 * (attempt + 1)}ms...")
                }
            }
        }
        
        android.util.Log.e("ProjectWidget", "All attempts failed", lastException)
        
        // Try to return cached data as fallback
        return tryGetCachedData(widgetPreferences, appWidgetId) ?: WidgetData()
    }
    
    private fun tryGetCachedData(
        widgetPreferences: com.br444n.constructionmaterialtrack.data.preferences.WidgetPreferences,
        widgetId: Int
    ): WidgetData? {
        if (widgetId <= 0) return null
        
        // Check if widget still has a valid configuration
        val projectId = widgetPreferences.getProjectIdForWidget(widgetId)
        if (projectId.isNullOrEmpty()) {
            android.util.Log.d("ProjectWidget", "No project ID for widget $widgetId - not using cached data")
            return null
        }
        
        android.util.Log.d("ProjectWidget", "Trying to get cached data for widget $widgetId")
        
        val cachedData = widgetPreferences.getCachedWidgetData(widgetId)
        return if (cachedData != null) {
            android.util.Log.d("ProjectWidget", "=== USING CACHED DATA ===")
            android.util.Log.d("ProjectWidget", "Cached project: ${cachedData.projectName}")
            android.util.Log.d("ProjectWidget", "Cached progress: ${cachedData.progress}")
            android.util.Log.d("ProjectWidget", "Cached completed: ${cachedData.completedMaterials}")
            android.util.Log.d("ProjectWidget", "Cached total: ${cachedData.totalMaterials}")
            android.util.Log.d("ProjectWidget", "Cache timestamp: ${cachedData.timestamp}")
            android.util.Log.d("ProjectWidget", "=== END CACHED DATA ===")
            
            // Create a dummy project with cached name
            val dummyProject = com.br444n.constructionmaterialtrack.domain.model.Project(
                id = projectId, // Use the actual project ID
                name = cachedData.projectName,
                description = "Cached data"
            )
            
            WidgetData(
                project = dummyProject,
                materials = emptyList(), // We don't cache individual materials
                progress = cachedData.progress,
                completedMaterials = cachedData.completedMaterials,
                totalMaterials = cachedData.totalMaterials
            )
        } else {
            android.util.Log.d("ProjectWidget", "No cached data available for widget $widgetId")
            null
        }
    }
}

class ProjectWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ProjectWidget()
}
package com.br444n.constructionmaterialtrack.core.shortcuts

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.br444n.constructionmaterialtrack.MainActivity
import com.br444n.constructionmaterialtrack.domain.model.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Manages dynamic shortcuts for recent projects
 * Optimized to avoid blocking the main thread
 */
object DynamicShortcutManager {
    
    private const val MAX_SHORTCUTS = 2 // Maximum number of project shortcuts to show
    private const val UPDATE_DEBOUNCE_MS = 2 * 60 * 1000L // 2 minutes (reduced for better UX)
    private const val TAG = "DynamicShortcutManager"
    
    private var lastUpdateTime = 0L
    private var lastProjectCount = 0 // Track project count to detect changes
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    /**
     * Updates dynamic shortcuts with the most recent projects
     * Processes icons in background to avoid blocking UI
     */
    fun updateProjectShortcuts(context: Context, recentProjects: List<Project>) {
        // Check Android version
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
            return // Dynamic shortcuts not supported
        }
        
        val now = System.currentTimeMillis()
        val projectCount = recentProjects.size
        
        // Skip debounce if project count changed (new project created/deleted)
        val projectCountChanged = projectCount != lastProjectCount
        lastProjectCount = projectCount
        
        // Debounce: avoid updating too frequently (unless project count changed)
        if (!projectCountChanged && now - lastUpdateTime < UPDATE_DEBOUNCE_MS) {
            android.util.Log.d(TAG, "Skipping update due to debounce (${(now - lastUpdateTime) / 1000}s since last update)")
            return
        }
        
        if (projectCountChanged) {
            android.util.Log.d(TAG, "Project count changed ($projectCount), forcing update")
        }
        
        lastUpdateTime = now
        
        // Process in background
        scope.launch {
            try {
                updateShortcutsAsync(context.applicationContext, recentProjects)
            } catch (e: Exception) {
                android.util.Log.e(TAG, "Error updating shortcuts", e)
            }
        }
    }
    
    /**
     * Process shortcuts asynchronously
     */
    private suspend fun updateShortcutsAsync(context: Context, recentProjects: List<Project>) {
        // Create processor locally to avoid memory leaks
        val processor = ShortcutIconProcessor(context)
        
        // Take only first 2 projects
        val projectsToProcess = recentProjects.take(MAX_SHORTCUTS)
        
        // Prepare icons in background
        val shortcuts = projectsToProcess.mapIndexed { index, project ->
            val icon = processor.prepareIcon(project) ?: processor.getDefaultIcon()
            createProjectShortcut(context, project, icon, index)
        }
        
        // Update shortcuts on main thread
        ShortcutManagerCompat.setDynamicShortcuts(context, shortcuts)
        
        // Cleanup old cache
        processor.cleanupCache()
    }
    
    /**
     * Creates a shortcut for a specific project with prepared icon
     */
    private fun createProjectShortcut(
        context: Context,
        project: Project,
        iconBitmap: Bitmap,
        rank: Int
    ): ShortcutInfoCompat {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra("shortcut_action", "open_project")
            putExtra("project_id", project.id)
        }
        
        val icon = IconCompat.createWithAdaptiveBitmap(iconBitmap)
        
        return ShortcutInfoCompat.Builder(context, "project_${project.id}")
            .setShortLabel(project.name)
            .setLongLabel("Open ${project.name}")
            .setIcon(icon)
            .setIntent(intent)
            .setRank(rank)
            .build()
    }
    
    /**
     * Invalidate cache for a specific project
     * Call this when a project's image changes to ensure shortcuts show the new image
     * 
     * Example usage:
     * ```
     * // After updating project image in database
     * DynamicShortcutManager.invalidateProjectCache(context, projectId)
     * DynamicShortcutManager.updateProjectShortcuts(context, updatedProjects)
     * ```
     */
    @Suppress("unused")
    fun invalidateProjectCache(context: Context, projectId: String) {
        // Create processor locally to avoid memory leaks
        val processor = ShortcutIconProcessor(context.applicationContext)
        processor.invalidateCache(projectId)
    }
    
    /**
     * Force update shortcuts immediately (bypass debounce)
     * Use when you need immediate shortcut updates without waiting for debounce period
     * 
     * Note: The system already handles project creation/deletion automatically.
     * This is mainly useful for other immediate updates.
     * 
     * Example usage:
     * ```
     * // For immediate updates when needed
     * DynamicShortcutManager.forceUpdateShortcuts(context, projects)
     * ```
     */
    @Suppress("unused")
    fun forceUpdateShortcuts(context: Context, recentProjects: List<Project>) {
        // Check Android version
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
            return
        }
        
        // Reset debounce timer to force update
        lastUpdateTime = 0L
        
        // Update shortcuts
        updateProjectShortcuts(context, recentProjects)
    }
    
    /**
     * Removes all dynamic shortcuts
     */
    @Suppress("unused")
    fun clearProjectShortcuts(context: Context) {
        ShortcutManagerCompat.removeAllDynamicShortcuts(context)
    }
    
    /**
     * Removes a specific project shortcut
     */
    @Suppress("unused")
    fun removeProjectShortcut(context: Context, projectId: String) {
        ShortcutManagerCompat.removeDynamicShortcuts(context, listOf("project_$projectId"))
    }
}

package com.br444n.constructionmaterialtrack.widget

import android.content.Context
import androidx.glance.action.actionStartActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.compose.ui.graphics.Color
import androidx.glance.color.ColorProvider
import com.br444n.constructionmaterialtrack.MainActivity
import com.br444n.constructionmaterialtrack.R
import com.br444n.constructionmaterialtrack.data.local.database.ConstructionDatabase
import com.br444n.constructionmaterialtrack.data.preferences.WidgetPreferences
import com.br444n.constructionmaterialtrack.data.repository.MaterialRepositoryImpl
import com.br444n.constructionmaterialtrack.data.repository.ProjectRepositoryImpl
import com.br444n.constructionmaterialtrack.domain.model.Material
import com.br444n.constructionmaterialtrack.domain.model.Project
import kotlinx.coroutines.flow.first

data class WidgetData(
    val project: Project? = null,
    val materials: List<Material> = emptyList(),
    val progress: Float = 0f,
    val completedMaterials: Int = 0,
    val totalMaterials: Int = 0
)

@Composable
fun ProjectWidgetContent(context: Context, glanceId: GlanceId) {
    var widgetData by remember { mutableStateOf(WidgetData()) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(glanceId) {
        android.util.Log.d("ProjectWidget", "ProjectWidgetContent called with glanceId: $glanceId")
        try {
            widgetData = loadWidgetData(context, glanceId)
            android.util.Log.d("ProjectWidget", "Widget data loaded: project=${widgetData.project?.name}, progress=${widgetData.progress}")
        } catch (e: Exception) {
            android.util.Log.e("ProjectWidget", "Error loading widget data in LaunchedEffect", e)
        } finally {
            isLoading = false
        }
    }
    
    // Resolve colors from resources manually to use in ColorProvider(Color)
    val bgColorInt = context.getColor(R.color.glance_widget_background)
    val bgColor = Color(bgColorInt)

    
    GlanceTheme {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(day = bgColor, night = bgColor))
                .cornerRadius(16.dp)
                .padding(16.dp)
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    LoadingWidget(context)
                }
                widgetData.project != null -> {
                    ProjectWidgetLayout(widgetData, context)
                }
                else -> {
                    NoProjectSelected(context)
                }
            }
        }
    }
}

@Composable
private fun ProjectWidgetLayout(data: WidgetData, context: Context) {
    // Resolve text colors
    val contentColorInt = context.getColor(R.color.glance_widget_content)
    val contentColor = Color(contentColorInt)
    
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Project Name at top
        Text(
            text = data.project?.name ?: "",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ColorProvider(day = contentColor, night = contentColor),
                textAlign = TextAlign.Center
            ),
            modifier = GlanceModifier.fillMaxWidth().padding(horizontal = 8.dp)
        )
        
        Spacer(modifier = GlanceModifier.height(16.dp))
        
        // Circular Progress Ring - centered and larger
        Box(
            modifier = GlanceModifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressRing(
                progress = data.progress,
                completedMaterials = data.completedMaterials,
                totalMaterials = data.totalMaterials,
                context = context
            )
        }
    }
}

@Composable
private fun CircularProgressRing(
    progress: Float,
    completedMaterials: Int,
    totalMaterials: Int,
    context: Context
) {
    // Resolve colors from resources manually to use in ColorProvider(Color)
    val contentColorInt = context.getColor(R.color.glance_widget_content)
    val trackColorInt = context.getColor(R.color.glass_track)
    val reflectionColorInt = context.getColor(R.color.glass_reflection)
    val innerGlowColorInt = context.getColor(R.color.glass_inner_glow)
    val shadowColorInt = context.getColor(R.color.glass_shadow)
    
    val contentColor = Color(contentColorInt)
    
    // Generate Bitmap for progress
    // Size 100dp approx 262px at xxhdpi, let's use a decent px size like 300px for quality
    val progressBitmap = remember(progress, contentColorInt) {
        com.br444n.constructionmaterialtrack.widget.ui.WidgetBitmapUtils.createCircularProgressBitmap(
            progress = progress,
            sizePx = 300,
            color = contentColorInt,
            trackColor = trackColorInt,
            reflectionColor = reflectionColorInt,
            innerGlowColor = innerGlowColorInt,
            shadowColor = shadowColorInt
        )
    }
    
    Box(
        modifier = GlanceModifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        // Custom drawn circular progress
        Image(
            provider = ImageProvider(progressBitmap),
            contentDescription = "Progress ${(progress * 100).toInt()}%",
            modifier = GlanceModifier.size(120.dp)
        )
        
        // Center content
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Percentage
            Text(
                text = "${(progress * 100).toInt()}%",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(day = contentColor, night = contentColor)
                )
            )
            
            // Materials count
            Text(
                text = "$completedMaterials/$totalMaterials",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = ColorProvider(day = contentColor, night = contentColor)
                )
            )
        }
    }
}

@Composable
private fun LoadingWidget(context: Context) {
    // Resolve text colors
    val contentColorInt = context.getColor(R.color.glance_widget_content)
    val contentColor = Color(contentColorInt)
    
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Loading...",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = ColorProvider(day = contentColor, night = contentColor),
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun NoProjectSelected(context: Context) {
    // Resolve text colors
    val contentColorInt = context.getColor(R.color.glance_widget_content)
    val contentColor = Color(contentColorInt)
    
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No Project Selected",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = ColorProvider(day = contentColor, night = contentColor),
                textAlign = TextAlign.Center
            )
        )
        
        Spacer(modifier = GlanceModifier.height(8.dp))
        
        Text(
            text = "Tap to configure",
            style = TextStyle(
                fontSize = 12.sp,
                color = ColorProvider(day = contentColor, night = contentColor),
                textAlign = TextAlign.Center
            )
        )
    }
}



private suspend fun loadWidgetData(context: Context, glanceId: GlanceId): WidgetData {
    return try {
        val widgetPreferences = WidgetPreferences(context)
        val glanceAppWidgetManager = androidx.glance.appwidget.GlanceAppWidgetManager(context)
        
        // Try to get the app widget ID from the glance ID
        val appWidgetId = try {
            glanceAppWidgetManager.getAppWidgetId(glanceId)
        } catch (e: Exception) {
            android.util.Log.e("ProjectWidget", "Failed to get app widget ID from glance ID", e)
            return WidgetData()
        }
        
        // Debug: Log the widget ID
        android.util.Log.d("ProjectWidget", "Widget ID: $appWidgetId")
        
        // Validate widget ID
        if (appWidgetId <= 0) {
            android.util.Log.w("ProjectWidget", "Invalid widget ID: $appWidgetId")
            return WidgetData()
        }
        
        val projectId = widgetPreferences.getProjectIdForWidget(appWidgetId)
        
        // Debug: Log the project ID
        android.util.Log.d("ProjectWidget", "Project ID from preferences: $projectId")
        
        if (projectId.isNullOrEmpty()) {
            android.util.Log.d("ProjectWidget", "No project ID found, returning empty data")
            return WidgetData()
        }
        
        val database = ConstructionDatabase.getDatabase(context)
        val projectRepository = ProjectRepositoryImpl(database.projectDao())
        val materialRepository = MaterialRepositoryImpl(database.materialDao())
        
        val project = projectRepository.getProjectById(projectId)
        
        if (project == null) {
            android.util.Log.w("ProjectWidget", "Project not found for ID: $projectId")
            return WidgetData()
        }
        
        // Use timeout to avoid hanging on database operations
        val materials = kotlinx.coroutines.withTimeoutOrNull(5000) {
            materialRepository.getMaterialsByProjectId(projectId).first()
        } ?: emptyList()
        
        // Debug: Log project and materials info
        android.util.Log.d("ProjectWidget", "Project: ${project.name}, Materials count: ${materials.size}")
        
        val totalMaterials = materials.size
        val completedMaterials = materials.count { it.isPurchased }
        val progress = if (totalMaterials > 0) {
            completedMaterials.toFloat() / totalMaterials.toFloat()
        } else {
            0f
        }
        
        // Debug: Log progress info
        android.util.Log.d("ProjectWidget", "Progress: $progress, Completed: $completedMaterials, Total: $totalMaterials")
        
        WidgetData(
            project = project,
            materials = materials,
            progress = progress,
            completedMaterials = completedMaterials,
            totalMaterials = totalMaterials
        )
    } catch (e: Exception) {
        android.util.Log.e("ProjectWidget", "Error loading widget data", e)
        WidgetData()
    }
}
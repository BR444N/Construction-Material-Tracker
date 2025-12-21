package com.br444n.constructionmaterialtrack.widget

import android.content.Context
import androidx.glance.action.actionStartActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.br444n.constructionmaterialtrack.domain.model.Material
import com.br444n.constructionmaterialtrack.domain.model.Project
import com.br444n.constructionmaterialtrack.widget.ui.WidgetBitmapUtils


data class WidgetData(
    val project: Project? = null,
    val materials: List<Material> = emptyList(),
    val progress: Float = 0f,
    val completedMaterials: Int = 0,
    val totalMaterials: Int = 0
)

@Composable
fun ProjectWidgetContent(context: Context, widgetData: WidgetData) {
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
        // Project Name at top - with maxLines to prevent overflow
        Text(
            text = data.project?.name ?: "",
            style = TextStyle(
                fontSize = 16.sp, // Reduced from 18sp
                fontWeight = FontWeight.Bold,
                color = ColorProvider(day = contentColor, night = contentColor),
                textAlign = TextAlign.Center
            ),
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            maxLines = 2 // Limit to 2 lines
        )
        
        Spacer(modifier = GlanceModifier.height(12.dp)) // Reduced from 16dp
        
        // Circular Progress Ring - centered and larger
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(120.dp), // Fixed height instead of weight
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
    android.util.Log.d("ProjectWidget", "CircularProgressRing called with progress=$progress, completed=$completedMaterials, total=$totalMaterials")
    
    // Resolve colors from resources manually to use in ColorProvider(Color)
    val contentColorInt = context.getColor(R.color.glance_widget_content)
    val contentColor = Color(contentColorInt)
    
    // Resolve glass effect colors
    val trackColorInt = context.getColor(R.color.glass_track)
    val reflectionColorInt = context.getColor(R.color.glass_reflection)
    val innerGlowColorInt = context.getColor(R.color.glass_inner_glow)
    val shadowColorInt = context.getColor(R.color.glass_shadow)
    
    // Generate Bitmap for progress with glass tube effect (without remember)
    val progressBitmap = WidgetBitmapUtils.createCircularProgressBitmap(
        progress = progress,
        sizePx = 300, // High resolution for quality
        color = contentColorInt,
        trackColor = trackColorInt,
        reflectionColor = reflectionColorInt,
        innerGlowColor = innerGlowColorInt,
        shadowColor = shadowColorInt
    )
    
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Glass tube progress ring with overlaid text
        Box(contentAlignment = Alignment.Center) {
            // Custom drawn circular progress with glass tube effect
            Image(
                provider = ImageProvider(progressBitmap),
                contentDescription = "Progress ${(progress * 100).toInt()}%",
                modifier = GlanceModifier.size(100.dp)
            )
            
            // Percentage text overlaid on the progress ring
            Text(
                text = "${(progress * 100).toInt()}%",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorProvider(day = contentColor, night = contentColor),
                    textAlign = TextAlign.Center
                )
            )
        }
        
        Spacer(modifier = GlanceModifier.height(8.dp))
        
        // Materials count
        Text(
            text = "$completedMaterials/$totalMaterials materials",
            style = TextStyle(
                fontSize = 12.sp,
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
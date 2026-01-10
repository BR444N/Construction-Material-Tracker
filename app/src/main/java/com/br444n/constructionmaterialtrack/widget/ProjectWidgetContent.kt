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
import androidx.compose.ui.res.stringResource
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
                .clickable(
                    if (widgetData.project != null) {
                        actionStartActivity<MainActivity>()
                    } else {
                        // If no project is selected, open widget configuration
                        actionStartActivity<ProjectWidgetConfigActivity>()
                    }
                ),
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
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(vertical = 12.dp), // More generous padding
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Project Name at top - much larger
        Text(
            text = data.project?.name ?: "",
            style = TextStyle(
                fontSize = 22.sp, // Significantly increased from 16sp
                fontWeight = FontWeight.Bold,
                color = ColorProvider(day = contentColor, night = contentColor),
                textAlign = TextAlign.Center
            ),
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            maxLines = 2
        )
        
        Spacer(modifier = GlanceModifier.height(20.dp)) // Much more spacing
        
        // Progress section - much more expanded
        Column(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Glass tube progress ring - dramatically larger
            Box(
                contentAlignment = Alignment.Center,
                modifier = GlanceModifier.size(160.dp) // Much larger from 120dp
            ) {
                // Custom drawn circular progress with glass tube effect
                Image(
                    provider = ImageProvider(createProgressBitmap(data.progress, context)),
                    contentDescription = "Progress ${(data.progress * 100).toInt()}%",
                    modifier = GlanceModifier.size(160.dp)
                )
                
                // Percentage text - much larger
                Text(
                    text = "${(data.progress * 100).toInt()}%",
                    style = TextStyle(
                        fontSize = 32.sp, // Significantly increased from 24sp
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(day = contentColor, night = contentColor),
                        textAlign = TextAlign.Center
                    )
                )
            }
            
            Spacer(modifier = GlanceModifier.height(16.dp)) // More spacing
            
            // Materials count - much larger
            Text(
                text = "${data.completedMaterials}/${data.totalMaterials} materials",
                style = TextStyle(
                    fontSize = 18.sp, // Significantly increased from 14sp
                    fontWeight = FontWeight.Medium,
                    color = ColorProvider(day = contentColor, night = contentColor),
                    textAlign = TextAlign.Center
                ),
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
            
            // Bottom spacer
            Spacer(modifier = GlanceModifier.height(8.dp))
        }
    }
}

@Composable
private fun createProgressBitmap(progress: Float, context: Context): android.graphics.Bitmap {
    // Resolve glass effect colors
    val contentColorInt = context.getColor(R.color.glance_widget_content)
    val trackColorInt = context.getColor(R.color.glass_track)
    val reflectionColorInt = context.getColor(R.color.glass_reflection)
    val innerGlowColorInt = context.getColor(R.color.glass_inner_glow)
    val shadowColorInt = context.getColor(R.color.glass_shadow)
    
    return WidgetBitmapUtils.createCircularProgressBitmap(
        progress = progress,
        sizePx = 480, // Much larger for 160dp size (160 * 3 = 480px at xxhdpi)
        color = contentColorInt,
        trackColor = trackColorInt,
        reflectionColor = reflectionColorInt,
        innerGlowColor = innerGlowColorInt,
        shadowColor = shadowColorInt,
        strokeWidth = 65f // Much thicker stroke for better visibility
    )
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
            text = stringResource(R.string.no_project_selected),
            style = TextStyle(
                fontSize = 22.sp, // Much larger from 18sp
                fontWeight = FontWeight.Medium,
                color = ColorProvider(day = contentColor, night = contentColor),
                textAlign = TextAlign.Center
            )
        )
        
        Spacer(modifier = GlanceModifier.height(16.dp)) // More spacing
        
        Text(
            text = stringResource(R.string.tap_configure),
            style = TextStyle(
                fontSize = 16.sp, // Larger from 14sp
                color = ColorProvider(day = contentColor, night = contentColor),
                textAlign = TextAlign.Center
            )
        )
    }
}
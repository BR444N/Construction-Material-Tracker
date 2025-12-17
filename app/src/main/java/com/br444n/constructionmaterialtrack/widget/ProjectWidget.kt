package com.br444n.constructionmaterialtrack.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent

/**
 * Implementation of App Widget functionality using Glance.
 * App Widget Configuration implemented in [ProjectWidgetConfigActivity]
 */
class ProjectWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: androidx.glance.GlanceId) {
        provideContent {
            ProjectWidgetContent(context, id)
        }
    }
}

class ProjectWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ProjectWidget()
}
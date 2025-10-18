package com.br444n.constructionmaterialtrack.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleManager {
    
    fun setLocale(context: Context, languageCode: String): Context {
        val locale = Locale.forLanguageTag(languageCode)
        Locale.setDefault(locale)
        return updateResources(context, locale)
    }
    
    private fun updateResources(context: Context, locale: Locale): Context {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }
    
    fun recreateActivityForLanguageChange(context: Context) {
        if (context is android.app.Activity) {
            context.recreate()
        }
    }
    
    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        return prefs.getString("current_language", "en") ?: "en"
    }
}
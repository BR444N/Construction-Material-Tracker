package com.br444n.constructionmaterialtrack.domain.model

data class Language(
    val code: String,
    val name: String,
    val nativeName: String
)

object LanguageConstants {
    val SUPPORTED_LANGUAGES = listOf(
        Language("en", "English", "English"),
        Language("es", "Spanish", "Español"),
        Language("fr", "French", "Français")
    )
    
    fun getLanguageDisplayName(code: String): String {
        return SUPPORTED_LANGUAGES.find { it.code == code }?.nativeName ?: "English"
    }
}
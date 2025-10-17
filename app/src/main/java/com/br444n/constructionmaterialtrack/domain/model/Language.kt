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
    
    fun getLanguageByCode(code: String): Language? {
        return SUPPORTED_LANGUAGES.find { it.code == code }
    }
    
    fun getLanguageDisplayName(code: String): String {
        return getLanguageByCode(code)?.nativeName ?: "English"
    }
}
package com.br444n.constructionmaterialtrack.domain.model

enum class MaterialUnit(val displayName: String, val shortName: String) {
    PIECES("Pieces", "pcs"),
    UNITS("Units", "units"),
    METERS("Meters", "m"),
    CENTIMETERS("Centimeters", "cm"),
    INCHES("Inches", "in"),
    FEET("Feet", "ft"),
    KILOGRAMS("Kilograms", "kg"),
    GRAMS("Grams", "g"),
    POUNDS("Pounds", "lbs"),
    LITERS("Liters", "L"),
    GALLONS("Gallons", "gal"),
    SQUARE_METERS("Square Meters", "m²"),
    SQUARE_FEET("Square Feet", "ft²"),
    CUBIC_METERS("Cubic Meters", "m³"),
    CUBIC_FEET("Cubic Feet", "ft³"),
    BAGS("Bags", "bags"),
    BOXES("Boxes", "boxes"),
    ROLLS("Rolls", "rolls"),
    SHEETS("Sheets", "sheets");

    companion object {
        fun fromShortName(shortName: String): MaterialUnit {
            return entries.find { it.shortName == shortName } ?: PIECES
        }
        
        fun getCommonUnits(): List<MaterialUnit> {
            return listOf(PIECES, UNITS, METERS, KILOGRAMS, LITERS, BAGS, BOXES)
        }
        
        fun getAllUnits(): List<MaterialUnit> {
            return entries
        }
    }
}
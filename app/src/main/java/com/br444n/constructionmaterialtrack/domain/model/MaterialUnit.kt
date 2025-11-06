package com.br444n.constructionmaterialtrack.domain.model

import android.content.Context
import androidx.annotation.StringRes
import com.br444n.constructionmaterialtrack.R

enum class MaterialUnit(
    @param:StringRes val displayNameRes: Int,
    val shortName: String
) {
    PIECES(R.string.unit_pieces, "pcs"),
    UNITS(R.string.unit_units, "units"),
    METERS(R.string.unit_meters, "m"),
    CENTIMETERS(R.string.unit_centimeters, "cm"),
    INCHES(R.string.unit_inches, "in"),
    FEET(R.string.unit_feet, "ft"),
    KILOGRAMS(R.string.unit_kilograms, "kg"),
    GRAMS(R.string.unit_grams, "g"),
    POUNDS(R.string.unit_pounds, "lbs"),
    LITERS(R.string.unit_liters, "L"),
    GALLONS(R.string.unit_gallons, "gal"),
    SQUARE_METERS(R.string.unit_square_meters, "m²"),
    SQUARE_FEET(R.string.unit_square_feet, "ft²"),
    CUBIC_METERS(R.string.unit_cubic_meters, "m³"),
    CUBIC_FEET(R.string.unit_cubic_feet, "ft³"),
    BAGS(R.string.unit_bags, "bags"),
    BOXES(R.string.unit_boxes, "boxes"),
    ROLLS(R.string.unit_rolls, "rolls"),
    SHEETS(R.string.unit_sheets, "sheets");

    fun getDisplayName(context: Context): String {
        return context.getString(displayNameRes)
    }

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
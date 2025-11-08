package com.br444n.constructionmaterialtrack.domain.model

import android.content.Context
import androidx.annotation.StringRes
import com.br444n.constructionmaterialtrack.R

enum class MaterialUnit(
    @param:StringRes val displayNameRes: Int,
    val shortName: String
) {

    UNITS(R.string.unit_units, "units"),

    PIECES(R.string.unit_pieces, "pcs"),

    METERS(R.string.unit_meters, "m"),

    CENTIMETERS(R.string.unit_centimeters, "cm"),

    MILLIMETERS(R.string.unit_millimeters, "mm"),

    INCHES(R.string.unit_inches, "in"),

    FEET(R.string.unit_feet, "ft"),

    SQUARE_METERS(R.string.unit_square_meters, "m²"),

    SQUARE_FEET(R.string.unit_square_feet, "ft²"),

    CUBIC_FEET(R.string.unit_cubic_feet, "ft³"),

    CUBIC_METERS(R.string.unit_cubic_meters, "m³"),

    LITERS(R.string.unit_liters, "L"),

    GALLONS(R.string.unit_gallons, "gal"),
    KILOGRAMS(R.string.unit_kilograms, "kg"),

    GRAMS(R.string.unit_grams, "g"),

    TONNE(R.string.unit_tonne, "t"),

    POUNDS(R.string.unit_pounds, "Lb"),

    BAGS(R.string.unit_bags, "Bags"),

    ROLLS(R.string.unit_rolls, "RL"),

    SET(R.string.unit_set, "Set"),

    PAIRS(R.string.unit_pairs, "Pairs"),

    BOXES(R.string.unit_boxes, "Boxes"),

    PERCENTAGE(R.string.unit_percentage, "%"),

    LOT(R.string.unit_lot, "Lot"),

    SHEETS(R.string.unit_sheets, "Sheets"),

    PLATE(R.string.unit_plate, "Pl"),

    PANEL(R.string.unit_panel, "Lá"),

    TUBE(R.string.unit_tube,"Tub"),

    PACK(R.string.unit_pack,"Pq"),

    HOUR(R.string.unit_hour,"h"),

    DAY(R.string.unit_day,"d"),

    WEEK(R.string.unit_week,"wk"),

    MONTH(R.string.unit_month,"mo")
    ;


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
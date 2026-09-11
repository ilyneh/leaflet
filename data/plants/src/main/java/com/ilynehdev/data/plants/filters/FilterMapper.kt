package com.ilynehdev.data.plants.filters

import com.ilynehdev.data.plants.model.Dimension

/**
 * Translates the app's filter taxonomy to API data values. Empty selections
 * match everything; within a group any selected option may match (OR);
 * unknown data (null) never matches a restrictive filter.
 */
object FilterMapper {

    private val lightValues = mapOf(
        LightFilter.DirectSun to setOf("full sun"),
        LightFilter.BrightIndirect to setOf("part sun/part shade", "sun-part shade"),
        LightFilter.Medium to setOf("part shade", "filtered shade"),
        LightFilter.LowLight to setOf("full shade", "deep shade"),
    )

    private val wateringValues = mapOf(
        WateringFilter.Weekly to setOf("frequent"),
        WateringFilter.EveryTwoWeeks to setOf("average"),
        WateringFilter.Monthly to setOf("minimum", "none"),
    )

    private val careLevelValues = mapOf(
        CareLevelFilter.Easy to setOf("easy"),
        CareLevelFilter.Moderate to setOf("medium", "moderate"),
        CareLevelFilter.Fussy to setOf("high"),
    )

    fun matchesLight(sunlight: List<String>?, selected: Set<LightFilter>): Boolean {
        if (selected.isEmpty()) return true
        val values = sunlight?.map { it.lowercase() } ?: return false
        return selected.any { filter ->
            values.any { it in lightValues.getValue(filter) }
        }
    }

    fun matchesWatering(
        benchmarkValue: String?,
        benchmarkUnit: String?,
        watering: String?,
        selected: Set<WateringFilter>,
    ): Boolean {
        if (selected.isEmpty()) return true

        val days = benchmarkDays(benchmarkValue, benchmarkUnit)
        if (days != null) {
            return selected.any { filter ->
                when (filter) {
                    WateringFilter.Weekly -> days <= 9
                    WateringFilter.EveryTwoWeeks -> days in 10.0..20.0
                    WateringFilter.Monthly -> days >= 21
                }
            }
        }

        val value = watering?.lowercase() ?: return false
        return selected.any { value in wateringValues.getValue(it) }
    }

    fun matchesSafety(
        poisonousToPets: Boolean?,
        poisonousToHumans: Boolean?,
        selected: Set<SafetyFilter>,
    ): Boolean {
        if (selected.isEmpty()) return true
        return selected.all { filter ->
            when (filter) {
                SafetyFilter.PetSafe -> poisonousToPets == false
                SafetyFilter.NonToxicToChildren -> poisonousToHumans == false
            }
        }
    }

    fun matchesCareLevel(careLevel: String?, selected: Set<CareLevelFilter>): Boolean {
        if (selected.isEmpty()) return true
        val value = careLevel?.lowercase() ?: return false
        return selected.any { value in careLevelValues.getValue(it) }
    }

    fun matchesMatureSize(dimensions: List<Dimension>?, selected: Set<MatureSizeFilter>): Boolean {
        if (selected.isEmpty()) return true
        val feet = heightInFeet(dimensions) ?: return false
        return selected.any { filter ->
            when (filter) {
                MatureSizeFilter.Tabletop -> feet < 1
                MatureSizeFilter.Shelf -> feet >= 1 && feet < 3
                MatureSizeFilter.Floor -> feet >= 3 && feet < 6
                MatureSizeFilter.Tall -> feet >= 6
            }
        }
    }

    // "5-7" ranges use their midpoint; single values parse directly.
    private fun benchmarkDays(value: String?, unit: String?): Double? {
        if (value == null) return null
        if (unit != null && "day" !in unit.lowercase()) return null
        val parts = value.split("-").mapNotNull { it.trim().toDoubleOrNull() }
        return when (parts.size) {
            1 -> parts[0]
            2 -> (parts[0] + parts[1]) / 2
            else -> null
        }
    }

    private fun heightInFeet(dimensions: List<Dimension>?): Double? {
        val height = dimensions?.firstOrNull { it.type?.equals("height", ignoreCase = true) == true }
            ?: dimensions?.firstOrNull()
            ?: return null
        val value = height.maxValue ?: height.minValue ?: return null
        return when (height.unit?.lowercase()) {
            "feet", "ft", "foot" -> value
            "meters", "meter", "m" -> value * 3.28084
            "centimeters", "cm" -> value * 0.0328084
            "inches", "inch", "in" -> value / 12
            else -> null
        }
    }
}
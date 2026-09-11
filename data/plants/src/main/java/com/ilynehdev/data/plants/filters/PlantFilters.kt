package com.ilynehdev.data.plants.filters

enum class LightFilter { LowLight, Medium, BrightIndirect, DirectSun }
enum class WateringFilter { Weekly, EveryTwoWeeks, Monthly }
enum class SafetyFilter { PetSafe, NonToxicToChildren }
enum class CareLevelFilter { Easy, Moderate, Fussy }
enum class MatureSizeFilter { Tabletop, Shelf, Floor, Tall }

data class PlantFilters(
    val light: Set<LightFilter> = emptySet(),
    val watering: Set<WateringFilter> = emptySet(),
    val safety: Set<SafetyFilter> = emptySet(),
    val careLevel: Set<CareLevelFilter> = emptySet(),
    val matureSize: Set<MatureSizeFilter> = emptySet(),
) {
    val isEmpty: Boolean
        get() = activeCount == 0

    val activeCount: Int
        get() = light.size + watering.size + safety.size + careLevel.size + matureSize.size
}

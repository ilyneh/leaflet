package com.ilynehdev.data.plants.filters

data class FilterQueryParams(
    val sunlight: String? = null,
    val watering: String? = null,
    val poisonous: Boolean? = null,
)

/**
 * Server-side pre-narrowing for a search fetch. Params may only over-fetch,
 * never exclude a plant the local predicates would keep, so a param is sent
 * only when a single selection maps to it unambiguously; local [FilterMapper]
 * remains the final arbiter on everything returned.
 */
fun PlantFilters.toQueryParams() = FilterQueryParams(
    sunlight = light.singleOrNull()?.let {
        when (it) {
            LightFilter.LowLight -> "full_shade"
            LightFilter.Medium -> "part_shade"
            LightFilter.BrightIndirect -> "sun-part_shade"
            LightFilter.DirectSun -> "full_sun"
        }
    },
    watering = watering.singleOrNull()?.let {
        when (it) {
            WateringFilter.Weekly -> "frequent"
            WateringFilter.EveryTwoWeeks -> "average"
            // Monthly locally accepts both "minimum" and "none"; the API takes
            // one value, so narrowing to either would under-fetch.
            WateringFilter.Monthly -> null
        }
    },
    // The API's poisonous flag is one combined boolean; only when BOTH safety
    // options are selected do the local predicates imply poisonous=false. With
    // one selected, poisonous=0 would drop e.g. pet-safe but human-toxic rows.
    poisonous = if (safety.size == SafetyFilter.entries.size) false else null,
)

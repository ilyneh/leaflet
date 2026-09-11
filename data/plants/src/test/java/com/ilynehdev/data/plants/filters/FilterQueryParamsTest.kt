package com.ilynehdev.data.plants.filters

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FilterQueryParamsTest {

    @Test
    fun `empty filters map to no params`() {
        assertEquals(FilterQueryParams(), PlantFilters().toQueryParams())
    }

    @Test
    fun `single light selection maps to its api value`() {
        assertEquals("full_shade", params(light = LightFilter.LowLight).sunlight)
        assertEquals("part_shade", params(light = LightFilter.Medium).sunlight)
        assertEquals("sun-part_shade", params(light = LightFilter.BrightIndirect).sunlight)
        assertEquals("full_sun", params(light = LightFilter.DirectSun).sunlight)
    }

    @Test
    fun `multiple light selections omit the param`() {
        val filters = PlantFilters(light = setOf(LightFilter.LowLight, LightFilter.DirectSun))
        assertNull(filters.toQueryParams().sunlight)
    }

    @Test
    fun `single watering selection maps except monthly`() {
        assertEquals("frequent", params(watering = WateringFilter.Weekly).watering)
        assertEquals("average", params(watering = WateringFilter.EveryTwoWeeks).watering)
        // Monthly spans minimum+none locally; one api value would under-fetch.
        assertNull(params(watering = WateringFilter.Monthly).watering)
    }

    @Test
    fun `multiple watering selections omit the param`() {
        val filters = PlantFilters(
            watering = setOf(WateringFilter.Weekly, WateringFilter.EveryTwoWeeks),
        )
        assertNull(filters.toQueryParams().watering)
    }

    @Test
    fun `poisonous sent only when every safety option is selected`() {
        // Single selection must not pre-narrow: the API flag is combined, so
        // poisonous=0 would drop pet-safe but human-toxic rows for PetSafe.
        assertNull(params(safety = SafetyFilter.PetSafe).poisonous)
        assertNull(params(safety = SafetyFilter.NonToxicToChildren).poisonous)
        assertEquals(
            false,
            PlantFilters(safety = setOf(SafetyFilter.PetSafe, SafetyFilter.NonToxicToChildren))
                .toQueryParams().poisonous,
        )
        assertNull(PlantFilters().toQueryParams().poisonous)
    }

    @Test
    fun `care level and mature size contribute no params`() {
        val filters = PlantFilters(
            careLevel = setOf(CareLevelFilter.Easy),
            matureSize = setOf(MatureSizeFilter.Tall),
        )
        assertEquals(FilterQueryParams(), filters.toQueryParams())
    }

    private fun params(
        light: LightFilter? = null,
        watering: WateringFilter? = null,
        safety: SafetyFilter? = null,
    ) = PlantFilters(
        light = setOfNotNull(light),
        watering = setOfNotNull(watering),
        safety = setOfNotNull(safety),
    ).toQueryParams()
}

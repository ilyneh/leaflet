package com.ilynehdev.data.plants.filters

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlantFiltersTest {

    @Test
    fun `activeCount counts traits not selections`() {
        val filters = PlantFilters(
            light = setOf(LightFilter.LowLight, LightFilter.DirectSun),
            watering = setOf(WateringFilter.Weekly),
        )
        assertEquals(2, filters.activeCount)
    }

    @Test
    fun `empty filters report empty`() {
        assertTrue(PlantFilters().isEmpty)
        assertEquals(0, PlantFilters().activeCount)
        assertFalse(PlantFilters(safety = setOf(SafetyFilter.PetSafe)).isEmpty)
    }
}

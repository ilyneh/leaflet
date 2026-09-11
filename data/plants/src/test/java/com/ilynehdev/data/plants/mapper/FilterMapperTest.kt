package com.ilynehdev.data.plants.mapper

import com.ilynehdev.data.plants.model.CareLevelFilter
import com.ilynehdev.data.plants.model.Dimension
import com.ilynehdev.data.plants.model.LightFilter
import com.ilynehdev.data.plants.model.MatureSizeFilter
import com.ilynehdev.data.plants.model.SafetyFilter
import com.ilynehdev.data.plants.model.WateringFilter
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FilterMapperTest {

    // ---- light ----
    @Test
    fun `empty light selection matches everything including null`() {
        assertTrue(FilterMapper.matchesLight(null, emptySet()))
        assertTrue(FilterMapper.matchesLight(listOf("full sun"), emptySet()))
    }

    @Test
    fun `light values map to their categories`() {
        assertTrue(FilterMapper.matchesLight(listOf("full sun"), setOf(LightFilter.DirectSun)))
        assertTrue(FilterMapper.matchesLight(listOf("part sun/part shade"), setOf(LightFilter.BrightIndirect)))
        assertTrue(FilterMapper.matchesLight(listOf("sun-part shade"), setOf(LightFilter.BrightIndirect)))
        assertTrue(FilterMapper.matchesLight(listOf("part shade"), setOf(LightFilter.Medium)))
        assertTrue(FilterMapper.matchesLight(listOf("filtered shade"), setOf(LightFilter.Medium)))
        assertTrue(FilterMapper.matchesLight(listOf("full shade"), setOf(LightFilter.LowLight)))
        assertTrue(FilterMapper.matchesLight(listOf("deep shade"), setOf(LightFilter.LowLight)))
    }

    @Test
    fun `light comparison ignores case`() {
        assertTrue(FilterMapper.matchesLight(listOf("Full sun"), setOf(LightFilter.DirectSun)))
        assertTrue(FilterMapper.matchesLight(listOf("FULL SHADE"), setOf(LightFilter.LowLight)))
    }

    @Test
    fun `any sunlight entry can match`() {
        assertTrue(
            FilterMapper.matchesLight(
                listOf("full sun", "part shade"),
                setOf(LightFilter.Medium),
            )
        )
    }

    @Test
    fun `unknown light never matches a selection`() {
        assertFalse(FilterMapper.matchesLight(null, setOf(LightFilter.DirectSun)))
        assertFalse(FilterMapper.matchesLight(emptyList(), setOf(LightFilter.DirectSun)))
        assertFalse(FilterMapper.matchesLight(listOf("full sun"), setOf(LightFilter.LowLight)))
    }

    // ---- watering ----
    @Test
    fun `benchmark days bucket into frequencies`() {
        assertTrue(FilterMapper.matchesWatering("7", "days", null, setOf(WateringFilter.Weekly)))
        assertTrue(FilterMapper.matchesWatering("14", "days", null, setOf(WateringFilter.EveryTwoWeeks)))
        assertTrue(FilterMapper.matchesWatering("30", "days", null, setOf(WateringFilter.Monthly)))
    }

    @Test
    fun `benchmark ranges use their midpoint`() {
        // "5-7" -> 6 -> Weekly
        assertTrue(FilterMapper.matchesWatering("5-7", "days", null, setOf(WateringFilter.Weekly)))
        // "7-14" -> 10.5 -> EveryTwoWeeks, not Weekly
        assertTrue(FilterMapper.matchesWatering("7-14", "days", null, setOf(WateringFilter.EveryTwoWeeks)))
        assertFalse(FilterMapper.matchesWatering("7-14", "days", null, setOf(WateringFilter.Weekly)))
    }

    @Test
    fun `benchmark takes precedence over the coarse enum`() {
        // benchmark says monthly even though enum says frequent
        assertTrue(FilterMapper.matchesWatering("30", "days", "FREQUENT", setOf(WateringFilter.Monthly)))
        assertFalse(FilterMapper.matchesWatering("30", "days", "FREQUENT", setOf(WateringFilter.Weekly)))
    }

    @Test
    fun `coarse enum used when benchmark missing, case-insensitive`() {
        assertTrue(FilterMapper.matchesWatering(null, null, "FREQUENT", setOf(WateringFilter.Weekly)))
        assertTrue(FilterMapper.matchesWatering(null, null, "Average", setOf(WateringFilter.EveryTwoWeeks)))
        assertTrue(FilterMapper.matchesWatering(null, null, "MINIMUM", setOf(WateringFilter.Monthly)))
        assertTrue(FilterMapper.matchesWatering(null, null, "NONE", setOf(WateringFilter.Monthly)))
    }

    @Test
    fun `non-day benchmark unit falls back to the enum`() {
        assertTrue(FilterMapper.matchesWatering("2", "weeks", "FREQUENT", setOf(WateringFilter.Weekly)))
    }

    @Test
    fun `unknown watering never matches a selection`() {
        assertFalse(FilterMapper.matchesWatering(null, null, null, setOf(WateringFilter.Weekly)))
        assertFalse(FilterMapper.matchesWatering(null, null, "UNKNOWN", setOf(WateringFilter.Weekly)))
    }

    // ---- safety ----
    @Test
    fun `pet safe requires an explicit false`() {
        assertTrue(FilterMapper.matchesSafety(false, null, setOf(SafetyFilter.PetSafe)))
        assertFalse(FilterMapper.matchesSafety(true, null, setOf(SafetyFilter.PetSafe)))
        assertFalse(FilterMapper.matchesSafety(null, null, setOf(SafetyFilter.PetSafe)))
    }

    @Test
    fun `child safe requires an explicit false`() {
        assertTrue(FilterMapper.matchesSafety(null, false, setOf(SafetyFilter.NonToxicToChildren)))
        assertFalse(FilterMapper.matchesSafety(null, true, setOf(SafetyFilter.NonToxicToChildren)))
        assertFalse(FilterMapper.matchesSafety(null, null, setOf(SafetyFilter.NonToxicToChildren)))
    }

    @Test
    fun `both safety filters must hold together`() {
        val both = setOf(SafetyFilter.PetSafe, SafetyFilter.NonToxicToChildren)
        assertTrue(FilterMapper.matchesSafety(false, false, both))
        assertFalse(FilterMapper.matchesSafety(false, true, both))
        assertFalse(FilterMapper.matchesSafety(true, false, both))
    }

    @Test
    fun `empty safety selection matches unknown toxicity`() {
        assertTrue(FilterMapper.matchesSafety(null, null, emptySet()))
    }

    // ---- care level ----
    @Test
    fun `care levels map with synonyms and case folding`() {
        assertTrue(FilterMapper.matchesCareLevel("Easy", setOf(CareLevelFilter.Easy)))
        assertTrue(FilterMapper.matchesCareLevel("Medium", setOf(CareLevelFilter.Moderate)))
        assertTrue(FilterMapper.matchesCareLevel("moderate", setOf(CareLevelFilter.Moderate)))
        assertTrue(FilterMapper.matchesCareLevel("High", setOf(CareLevelFilter.Fussy)))
        assertFalse(FilterMapper.matchesCareLevel("Easy", setOf(CareLevelFilter.Fussy)))
        assertFalse(FilterMapper.matchesCareLevel(null, setOf(CareLevelFilter.Easy)))
    }

    // ---- mature size ----
    private fun height(value: Double, unit: String) =
        listOf(Dimension(type = "Height", minValue = null, maxValue = value, unit = unit))

    @Test
    fun `height in feet buckets into sizes`() {
        assertTrue(FilterMapper.matchesMatureSize(height(0.5, "feet"), setOf(MatureSizeFilter.Tabletop)))
        assertTrue(FilterMapper.matchesMatureSize(height(2.0, "feet"), setOf(MatureSizeFilter.Shelf)))
        assertTrue(FilterMapper.matchesMatureSize(height(4.0, "feet"), setOf(MatureSizeFilter.Floor)))
        assertTrue(FilterMapper.matchesMatureSize(height(8.0, "feet"), setOf(MatureSizeFilter.Tall)))
    }

    @Test
    fun `metric and inch units normalize to feet`() {
        // 1m = 3.28ft -> Floor
        assertTrue(FilterMapper.matchesMatureSize(height(1.0, "meters"), setOf(MatureSizeFilter.Floor)))
        // 50cm = 1.64ft -> Shelf
        assertTrue(FilterMapper.matchesMatureSize(height(50.0, "cm"), setOf(MatureSizeFilter.Shelf)))
        // 6in = 0.5ft -> Tabletop
        assertTrue(FilterMapper.matchesMatureSize(height(6.0, "inches"), setOf(MatureSizeFilter.Tabletop)))
    }

    @Test
    fun `height entry preferred over other dimension types`() {
        val dimensions = listOf(
            Dimension(type = "Spread", minValue = null, maxValue = 10.0, unit = "feet"),
            Dimension(type = "height", minValue = null, maxValue = 2.0, unit = "feet"),
        )
        assertTrue(FilterMapper.matchesMatureSize(dimensions, setOf(MatureSizeFilter.Shelf)))
    }

    @Test
    fun `unknown size never matches a selection`() {
        assertFalse(FilterMapper.matchesMatureSize(null, setOf(MatureSizeFilter.Tall)))
        assertFalse(FilterMapper.matchesMatureSize(emptyList(), setOf(MatureSizeFilter.Tall)))
        assertFalse(
            FilterMapper.matchesMatureSize(
                listOf(Dimension(type = "Height", minValue = null, maxValue = 2.0, unit = "furlongs")),
                setOf(MatureSizeFilter.Shelf),
            )
        )
    }
}

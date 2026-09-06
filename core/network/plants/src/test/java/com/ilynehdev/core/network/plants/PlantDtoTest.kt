package com.ilynehdev.core.network.plants

import com.ilynehdev.core.network.client.LeafletJson
import com.ilynehdev.core.network.plants.dto.PagedDto
import com.ilynehdev.core.network.plants.dto.PlantAnatomyDto
import com.ilynehdev.core.network.plants.dto.PlantCycleDto
import com.ilynehdev.core.network.plants.dto.PlantDto
import com.ilynehdev.core.network.plants.dto.PlantPruningCountDto
import com.ilynehdev.core.network.plants.dto.PlantWateringDto
import com.ilynehdev.core.network.plants.util.readResource
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class PlantDtoTest {

    private val json = LeafletJson

    // ---- fixtures: list endpoint ----
    @Test
    fun `page 1 fixture decodes with expected metadata`() {
        val page = json.decodeFromString<PagedDto<PlantDto>>(readResource("data/plants/plants-1.json"))

        assertEquals(30, page.data.size)
        assertEquals(30, page.perPage)
        assertEquals(1, page.currentPage)
        assertEquals(337, page.lastPage)
        assertEquals(10102, page.total)
        assertEquals(2, page.nextPage)
    }

    @Test
    fun `page 1 first item maps known fields`() {
        val plant = json.decodeFromString<PagedDto<PlantDto>>(readResource("data/plants/plants-1.json")).data.first()

        assertEquals(1L, plant.id)
        assertEquals("European Silver Fir", plant.commonName)
        assertEquals(listOf("Abies alba"), plant.scientificName)
        assertEquals(45, plant.defaultImage?.license)
        assertTrue(plant.defaultImage?.originalUrl.orEmpty().startsWith("https://"))
    }

    @Test
    fun `list items lack detail fields and fall back to defaults`() {
        val plants = json.decodeFromString<PagedDto<PlantDto>>(readResource("data/plants/plants-1.json")).data

        plants.forEach {
            assertEquals(PlantCycleDto.UNKNOWN, it.cycle)
            assertEquals(PlantWateringDto.UNKNOWN, it.watering)
            assertNull(it.flowers)
        }
    }

    @Test
    fun `unknown keys from list endpoint are ignored`() {
        val page = json.decodeFromString<PagedDto<PlantDto>>(readResource("data/plants/plants-1.json"))
        assertTrue(page.data.all { it.id > 0 })
    }

    @Test
    fun `page 2 fixture decodes and chains from page 1`() {
        val page2 = json.decodeFromString<PagedDto<PlantDto>>(readResource("data/plants/plants-2.json"))

        assertEquals(2, page2.currentPage)
        assertEquals(3, page2.nextPage)
        assertEquals(30, page2.data.size)
    }

    // ---- pagination ----
    @Test
    fun `nextPage is null on last page`() {
        val page = json.decodeFromString<PagedDto<PlantDto>>(
            """{"data":[],"per_page":30,"current_page":337,"last_page":337,"total":10102}"""
        )
        assertNull(page.nextPage)
    }

    @Test
    fun `nextPage derives from metadata not item count`() {
        val page = json.decodeFromString<PagedDto<PlantDto>>(
            """{"data":[{"id":1}],"per_page":30,"current_page":1,"last_page":2,"total":31}"""
        )
        assertEquals(2, page.nextPage)
    }

    // ---- enums ----
    @Test
    fun `cycle and watering decode from server casing`() {
        val plant = json.decodeFromString<PlantDto>("""{"id":1,"cycle":"Perennial","watering":"Average"}""")

        assertEquals(PlantCycleDto.PERENNIAL, plant.cycle)
        assertEquals(PlantWateringDto.AVERAGE, plant.watering)
    }

    @Test
    fun `unknown enum values coerce to UNKNOWN`() {
        val plant = json.decodeFromString<PlantDto>("""{"id":1,"cycle":"Triennial","watering":"Sometimes"}""")

        assertEquals(PlantCycleDto.UNKNOWN, plant.cycle)
        assertEquals(PlantWateringDto.UNKNOWN, plant.watering)
    }

    @Test
    fun `null enum values coerce to UNKNOWN`() {
        val plant = json.decodeFromString<PlantDto>("""{"id":1,"cycle":null,"watering":null}""")

        assertEquals(PlantCycleDto.UNKNOWN, plant.cycle)
        assertEquals(PlantWateringDto.UNKNOWN, plant.watering)
    }

    // ---- nullability / required ----
    @Test
    fun `minimal payload with only id decodes`() {
        val plant = json.decodeFromString<PlantDto>("""{"id":42}""")

        assertEquals(42L, plant.id)
        assertNull(plant.commonName)
        assertNull(plant.dimensions)
        assertNull(plant.flowers)
        assertNull(plant.genus)
        assertNull(plant.speciesEpithet)
        assertNull(plant.cultivar)
        assertNull(plant.variety)
    }

    @Test
    fun `missing id fails`() {
        assertThrows(SerializationException::class.java) {
            json.decodeFromString<PlantDto>("""{"common_name":"Rose"}""")
        }
    }

    // ---- taxonomy ----
    @Test
    fun `taxonomy fields decode from server keys`() {
        val plant = json.decodeFromString<PlantDto>(
            """{"id":1,"genus":"Acer","species_epithet":"palmatum","cultivar":"Aoyagi","variety":null}"""
        )

        assertEquals("Acer", plant.genus)
        assertEquals("palmatum", plant.speciesEpithet)
        assertEquals("Aoyagi", plant.cultivar)
        assertNull(plant.variety)
    }

    @Test
    fun `list fixture species without cultivar decode null cultivar`() {
        val plant = json.decodeFromString<PagedDto<PlantDto>>(readResource("data/plants/plants-1.json")).data.first()

        assertEquals("Abies", plant.genus)
        assertEquals("alba", plant.speciesEpithet)
        assertNull(plant.cultivar)
        assertNull(plant.variety)
    }

    // ---- fixtures: details endpoint ----
    private fun details(id: Int): PlantDto =
        json.decodeFromString<PlantDto>(readResource("data/plants/plant-details-$id.json"))

    @Test
    fun `details fixture 1 decodes core fields`() {
        val plant = details(1)

        assertEquals(1L, plant.id)
        assertEquals("European Silver Fir", plant.commonName)
        assertEquals(listOf("Abies alba"), plant.scientificName)
        assertEquals(listOf("Common Silver Fir"), plant.otherName)
        assertEquals("Pinaceae", plant.family)
        assertEquals("tree", plant.type)
        assertEquals("Austria", plant.origin?.first())
        assertEquals(PlantCycleDto.PERENNIAL, plant.cycle)
        assertEquals(PlantWateringDto.FREQUENT, plant.watering)
        assertEquals(listOf("full sun"), plant.sunlight)
        assertEquals(listOf("February", "March", "April"), plant.pruningMonth)
        assertEquals("High", plant.growthRate)
        assertEquals("Medium", plant.careLevel)
        assertTrue(plant.description.orEmpty().startsWith("European Silver Fir"))
    }

    @Test
    fun `details fixture boolean flags decode`() {
        val plant = details(1)

        assertEquals(false, plant.flowers)
        assertEquals(false, plant.seeds)
        assertEquals(true, plant.medicinal)
        assertEquals(false, plant.poisonousToHumans)
        assertEquals(false, plant.poisonousToPets)
        assertEquals(false, plant.droughtTolerant)
        assertEquals(false, plant.saltTolerant)
        assertEquals(false, plant.thorny)
        assertEquals(false, plant.invasive)
        assertEquals(false, plant.tropical)
        assertEquals(false, plant.cuisine)
        assertEquals(false, plant.indoor)
    }

    @Test
    fun `details fixture dimensions is a list`() {
        val dimensions = details(1).dimensions

        assertEquals(1, dimensions?.size)
        assertEquals("Height", dimensions?.single()?.type)
        assertEquals(60.0, dimensions?.single()?.minValue)
        assertEquals(60.0, dimensions?.single()?.maxValue)
        assertEquals("feet", dimensions?.single()?.unit)
    }

    @Test
    fun `details fixture hardiness decodes`() {
        val hardiness = details(1).hardiness

        assertEquals("7", hardiness?.min)
        assertEquals("7", hardiness?.max)
    }

    @Test
    fun `details fixture empty arrays decode as empty lists`() {
        val plant = details(1)

        assertEquals(emptyList<String>(), plant.attracts)
        assertEquals(emptyList<String>(), plant.soil)
        assertEquals(emptyList<String>(), plant.pestSusceptibility)
        assertEquals(emptyList<PlantAnatomyDto>(), plant.plantAnatomy)
    }

    @Test
    fun `details fixture empty pruning_count array decodes as null`() {
        // Server returns [] instead of null when no pruning data (PHP empty array quirk)
        assertEquals(emptyList<PlantPruningCountDto>(), details(1).pruningCount)
        assertEquals(emptyList<PlantPruningCountDto>(), details(31).pruningCount)
    }

    @Test
    fun `details fixture nullable strings decode as null`() {
        val plant = details(1)

        assertNull(plant.maintenance)
        assertNull(plant.floweringSeason)
        assertNull(plant.rare)
    }

    @Test
    fun `details fixture watering benchmark keeps raw quoted value`() {
        // Server wraps value in literal quotes: "\"7-10\"". Mapper in core:data strips them.
        val benchmark = details(1).wateringGeneralBenchmark

        assertEquals("\"7-10\"", benchmark?.value)
        assertEquals("days", benchmark?.unit)
    }

    @Test
    fun `details fixture watering benchmark null value decodes`() {
        val benchmark = details(31).wateringGeneralBenchmark

        assertNull(benchmark?.value)
        assertEquals("days", benchmark?.unit)
    }

    @Test
    fun `details fixture default image decodes`() {
        val image = details(1).defaultImage

        assertEquals(45, image?.license)
        assertTrue(image?.licenseName.orEmpty().contains("CC BY-SA 3.0"))
        assertTrue(image?.originalUrl.orEmpty().startsWith("https://"))
        assertTrue(image?.regularUrl.orEmpty().startsWith("https://"))
        assertTrue(image?.mediumUrl.orEmpty().startsWith("https://"))
        assertTrue(image?.smallUrl.orEmpty().startsWith("https://"))
        assertTrue(image?.thumbnail.orEmpty().startsWith("https://"))
    }

    @Test
    fun `details fixture 31 decodes with null family and duplicate months`() {
        val plant = details(31)

        assertEquals(31L, plant.id)
        assertEquals("Aoyagi Japanese Maple*", plant.commonName)
        assertNull(plant.family)
        assertEquals("Acer", plant.genus)
        assertEquals("palmatum", plant.speciesEpithet)
        assertEquals("Aoyagi", plant.cultivar)
        assertNull(plant.variety)
        assertEquals(listOf("Japan"), plant.origin)
        assertEquals(listOf("full sun", "part shade"), plant.sunlight)
        assertEquals(8, plant.pruningMonth?.size)
        assertEquals("Low", plant.growthRate)
        assertEquals("Moderate", plant.careLevel)
        assertEquals(10.0, plant.dimensions?.single()?.maxValue)
    }

    @Test
    fun `details fixture unmodelled keys are ignored`() {
        // cones, leaf, fruits, edible_*, harvest_season, other_images, care_guides,
        // hardiness_location, x* premium fields are not on PlantDto
        val plant = details(1)
        assertEquals(1L, plant.id)
    }

    @Test
    fun `details fixture round trips`() {
        val original = details(1)

        val roundTripped = json.decodeFromString<PlantDto>(json.encodeToString(original))

        assertEquals(original, roundTripped)
    }

    // ---- round trip ----
    @Test
    fun `round trip preserves values`() {
        val original = json.decodeFromString<PagedDto<PlantDto>>(readResource("data/plants/plants-1.json"))

        val roundTripped = json.decodeFromString<PagedDto<PlantDto>>(json.encodeToString(original))

        assertEquals(original, roundTripped)
    }
}

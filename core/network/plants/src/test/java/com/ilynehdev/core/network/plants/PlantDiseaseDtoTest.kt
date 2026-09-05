package com.ilynehdev.core.network.plants

import com.ilynehdev.core.network.client.LeafletJson
import com.ilynehdev.core.network.plants.dto.PagedDto
import com.ilynehdev.core.network.plants.dto.PlantDiseaseDto
import com.ilynehdev.core.network.plants.dto.PlantDiseaseSectionDto
import com.ilynehdev.core.network.plants.dto.PlantImageDto
import com.ilynehdev.core.network.plants.util.readResource
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class PlantDiseaseDtoTest {

    private val json = LeafletJson

    private fun page(n: Int): PagedDto<PlantDiseaseDto> =
        json.decodeFromString<PagedDto<PlantDiseaseDto>>(readResource("data/plantdiseases/plant-diseases-$n.json"))

    private fun byId(id: Long): PlantDiseaseDto =
        (page(1).data + page(2).data).first { it.id == id }

    // ---- fixtures: pagination ----
    @Test
    fun `page 1 fixture decodes with expected metadata`() {
        val page = page(1)

        assertEquals(30, page.data.size)
        assertEquals(30, page.perPage)
        assertEquals(1, page.currentPage)
        assertEquals(4, page.lastPage)
        assertEquals(96, page.total)
        assertEquals(2, page.nextPage)
    }

    @Test
    fun `page 2 fixture decodes and chains from page 1`() {
        val page = page(2)

        assertEquals(30, page.data.size)
        assertEquals(2, page.currentPage)
        assertEquals(3, page.nextPage)
        assertEquals(31L, page.data.first().id)
    }

    // ---- fixtures: fields ----
    @Test
    fun `first disease decodes core fields`() {
        val disease = page(1).data.first()

        assertEquals(1L, disease.id)
        assertEquals("Fairy ring", disease.commonName)
        assertEquals("Agrocybe", disease.scientificName)
        assertEquals(listOf("all lawn grasses"), disease.host)
        assertNull(disease.family)
        assertNull(disease.otherName)
    }

    @Test
    fun `scientific_name is a single string not a list`() {
        val disease = byId(31)

        assertEquals("Botrytis  cinerea", disease.scientificName) // double space present in server data
        assertEquals(listOf("artichoke", "lettuce", "strawberry"), disease.host)
    }

    @Test
    fun `description decodes as list of sections`() {
        val sections = page(1).data.first().description

        assertEquals(2, sections?.size)
        assertEquals("What is Fairy Ring (Agrocybe)?", sections?.first()?.subtitle)
        assertTrue(sections?.first()?.description.orEmpty().startsWith("A fairy ring is a lawn issue"))
    }

    @Test
    fun `solution decodes as list of sections`() {
        val sections = page(1).data.first().solution

        assertEquals(2, sections?.size)
        assertEquals("Cultural Practices", sections?.first()?.subtitle)
        assertTrue(sections?.first()?.description.orEmpty().isNotBlank())
    }

    @Test
    fun `empty description and solution decode as empty lists`() {
        val disease = byId(15) // "Scab" has [] for both

        assertEquals("Scab", disease.commonName)
        assertEquals(emptyList<PlantDiseaseSectionDto>(), disease.description)
        assertEquals(emptyList<PlantDiseaseSectionDto>(), disease.solution)
    }

    @Test
    fun `other_name decodes when present`() {
        val disease = byId(2)

        assertEquals("Fungi Nuisance", disease.commonName)
        assertEquals(listOf("Nuisance fungi"), disease.otherName)
    }

    @Test
    fun `family is null across both fixture pages`() {
        (page(1).data + page(2).data).forEach { assertNull(it.family) }
    }

    @Test
    fun `host is never empty across both fixture pages`() {
        (page(1).data + page(2).data).forEach { assertTrue("id=${it.id}", it.host.orEmpty().isNotEmpty()) }
    }

    // ---- fixtures: images ----
    @Test
    fun `images decode with license and urls`() {
        val images = page(1).data.first().images

        assertEquals(5, images?.size)
        val first = images?.first()
        assertEquals(45, first?.license)
        assertTrue(first?.licenseName.orEmpty().contains("CC BY-SA 3.0"))
        assertTrue(first?.originalUrl.orEmpty().startsWith("https://"))
        assertTrue(first?.regularUrl.orEmpty().startsWith("https://"))
        assertTrue(first?.mediumUrl.orEmpty().startsWith("https://"))
        assertTrue(first?.smallUrl.orEmpty().startsWith("https://"))
        assertTrue(first?.thumbnail.orEmpty().startsWith("https://"))
    }

    @Test
    fun `empty images decode as empty list`() {
        assertEquals(emptyList<PlantImageDto>(), byId(13).images)
    }

    // ---- inline: nullability / required ----
    @Test
    fun `minimal payload with only id decodes`() {
        val disease = json.decodeFromString<PlantDiseaseDto>("""{"id":7}""")

        assertEquals(7L, disease.id)
        assertNull(disease.commonName)
        assertNull(disease.scientificName)
        assertNull(disease.description)
        assertNull(disease.solution)
        assertNull(disease.host)
        assertNull(disease.images)
    }

    @Test
    fun `missing id fails`() {
        assertThrows(SerializationException::class.java) {
            json.decodeFromString<PlantDiseaseDto>("""{"common_name":"Rust"}""")
        }
    }

    @Test
    fun `section with null subtitle decodes`() {
        val disease = json.decodeFromString<PlantDiseaseDto>(
            """{"id":1,"description":[{"subtitle":null,"description":"text"}]}"""
        )

        assertNull(disease.description?.single()?.subtitle)
        assertEquals("text", disease.description?.single()?.description)
    }

    @Test
    fun `unknown keys are ignored`() {
        val disease = json.decodeFromString<PlantDiseaseDto>("""{"id":1,"severity":"high","x_premium":"nope"}""")
        assertEquals(1L, disease.id)
    }

    // ---- round trip ----
    @Test
    fun `round trip preserves values`() {
        val original = page(1)

        val roundTripped = json.decodeFromString<PagedDto<PlantDiseaseDto>>(json.encodeToString(original))

        assertEquals(original, roundTripped)
    }
}

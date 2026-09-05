package com.ilynehdev.core.network.plants.contract

import com.ilynehdev.core.network.client.LeafletJson
import com.ilynehdev.core.network.client.NetworkConfig
import com.ilynehdev.core.network.client.createPlantHttpClient
import com.ilynehdev.core.network.plants.api.PlantsApiImpl
import com.ilynehdev.core.network.plants.dto.PagedDto
import com.ilynehdev.core.network.plants.dto.PlantCycleDto
import com.ilynehdev.core.network.plants.dto.PlantDto
import com.ilynehdev.core.network.plants.dto.PlantWateringDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test

/**
 * Live contract tests against the Perenual API. Detect upstream drift, not our logic.
 *
 * Skipped when PLANTS_API_KEY is absent. Budget: 4 calls per run.
 * Run: PLANTS_API_KEY=... ./gradlew :core:network:plants:contractTest
 */
class PlantsContractTest {

    private companion object {
        const val BASE_URL = "https://perenual.com/api/"
        const val SERVER_PAGE_SIZE = 30

        val apiKey: String = System.getenv("PLANTS_API_KEY").orEmpty()

        fun client(key: String): HttpClient = createPlantHttpClient(
            engine = OkHttp.create(),
            json = LeafletJson,
            config = NetworkConfig(baseUrl = BASE_URL, apiKey = key, isDebug = false),
        )

        val client: HttpClient by lazy { client(apiKey) }
        val api: PlantsApiImpl by lazy { PlantsApiImpl(client) }
    }

    @Before
    fun requireKey() {
        assumeTrue("PLANTS_API_KEY not set; skipping live contract tests", apiKey.isNotBlank())
    }

    @Test
    fun `species list page 1 decodes and points to page 2`() {
        runBlocking {
            val page = api.getPlants(page = 1)

            assertEquals(SERVER_PAGE_SIZE, page.items.size)
            assertEquals(2, page.nextKey)
            assertTrue(page.items.all { it.id > 0 })
            assertTrue(page.items.all { !it.commonName.isNullOrBlank() })
        }
    }

    @Test
    fun `last page reports no next page`() {
        runBlocking {
            val first = client.get("v2/species-list") { parameter("page", 1) }.body<PagedDto<PlantDto>>()
            assertEquals(SERVER_PAGE_SIZE, first.perPage)
            assertTrue(first.lastPage > 1)

            val last = client.get("v2/species-list") { parameter("page", first.lastPage) }.body<PagedDto<PlantDto>>()
            assertEquals(first.lastPage, last.currentPage)
            assertNull(last.nextPage)
            assertTrue(last.data.isNotEmpty())
        }
    }

    @Test
    fun `species details decodes with known enum values`() {
        runBlocking {
            val plant = api.getPlant(id = 1)

            assertEquals(1L, plant.id)
            assertEquals("European Silver Fir", plant.commonName)
            // UNKNOWN here means the server introduced a value we do not model.
            assertNotEquals(PlantCycleDto.UNKNOWN, plant.cycle)
            assertNotEquals(PlantWateringDto.UNKNOWN, plant.watering)
            assertNotNull(plant.dimensions)
            assertNotNull(plant.defaultImage?.originalUrl)
        }
    }

    @Test
    fun `invalid key is rejected with a client error`() {
        runBlocking {
            val error = runCatching { PlantsApiImpl(client("invalid")).getPlants(page = 1) }
                .exceptionOrNull()

            assertTrue("expected ClientRequestException, got $error", error is ClientRequestException)
            val status = (error as ClientRequestException).response.status
            // Perenual answers 404 for a bad key today; 401/403 would be the conventional replies.
            // Pin this so NetworkError mapping in core:data knows what "bad key" looks like.
            assertTrue(
                "unexpected status $status",
                status in setOf(HttpStatusCode.NotFound, HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden),
            )
        }
    }
}

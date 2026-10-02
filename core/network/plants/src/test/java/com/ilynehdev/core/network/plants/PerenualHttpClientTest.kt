package com.ilynehdev.core.network.plants

import com.ilynehdev.core.network.client.LeafletJson
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class PerenualHttpClientTest {

    private var captured: HttpRequestData? = null
    private val engine = MockEngine { request ->
        captured = request
        respond("{}", HttpStatusCode.OK)
    }

    private fun client() = createPerenualHttpClient(
        engine = engine,
        json = LeafletJson,
        apiKey = "test-key",
        isDebug = false,
    )

    @Test
    fun `resolves paths against the perenual base url`() = runTest {
        client().get("v2/species-list")

        assertEquals(
            "https://perenual.com/api/v2/species-list",
            captured!!.url.toString().substringBefore('?'),
        )
    }

    @Test
    fun `adds the api key as a query parameter`() = runTest {
        client().get("v2/species-list")

        assertEquals("test-key", captured!!.url.parameters["key"])
    }
}

package com.ilynehdev.core.network.client

import com.ilynehdev.core.network.client.di.createPlantHttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HttpClientFactoryTest {

    private val config = NetworkConfig(
        baseUrl = "https://example.test/api/",
        apiKey = "test-key",
        isDebug = false,
    )

    private var captured: HttpRequestData? = null
    private val engine = MockEngine { request ->
        captured = request
        respond("{}", HttpStatusCode.OK)
    }

    @Test
    fun `plant client resolves paths against the base url and adds the api key`() = runTest {
        createPlantHttpClient(engine, LeafletJson, config).get("v2/species-list")

        val url = captured!!.url
        assertEquals("https://example.test/api/v2/species-list", url.toString().substringBefore('?'))
        assertEquals("test-key", url.parameters["key"])
    }

    @Test
    fun `base client does not add the api key`() = runTest {
        createHttpClient(engine, LeafletJson, config).get("v2/species-list")

        assertNull(captured!!.url.parameters["key"])
    }
}

package com.ilynehdev.core.network.client

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
    fun `resolves paths against the base url`() = runTest {
        createHttpClient(engine, LeafletJson, config).get("v2/species-list")

        val url = captured!!.url
        assertEquals("https://example.test/api/v2/species-list", url.toString().substringBefore('?'))
    }

    // Authentication is a per-backend concern; see PerenualHttpClientTest.
    @Test
    fun `base client adds no auth params`() = runTest {
        createHttpClient(engine, LeafletJson, config).get("v2/species-list")

        assertNull(captured!!.url.parameters["key"])
    }
}

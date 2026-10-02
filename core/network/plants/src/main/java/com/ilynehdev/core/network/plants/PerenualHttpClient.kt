package com.ilynehdev.core.network.plants

import com.ilynehdev.core.network.client.NetworkConfig
import com.ilynehdev.core.network.client.createHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.defaultRequest
import kotlinx.serialization.json.Json


private const val PERENUAL_BASE_URL = "https://perenual.com/api/"
// Perenual authenticates with a query parameter
private const val API_KEY_PARAM = "key"

internal fun createPerenualHttpClient(
    engine: HttpClientEngine,
    json: Json,
    apiKey: String,
    isDebug: Boolean,
): HttpClient =
    createHttpClient(
        engine = engine,
        json = json,
        config = NetworkConfig(
            baseUrl = PERENUAL_BASE_URL,
            apiKey = apiKey,
            isDebug = isDebug,
        ),
        redactedQueryParams = setOf(API_KEY_PARAM),
    ).config {
        defaultRequest {
            url { parameters.append(API_KEY_PARAM, apiKey) }
        }
    }

package com.ilynehdev.core.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


fun createHttpClient(engine: HttpClientEngine, json: Json, config: NetworkConfig): HttpClient =
    HttpClient(engine) {

        expectSuccess = true

        install(ContentNegotiation) {
            json(json)
        }

        defaultRequest {
            url(config.baseUrl)
            contentType(ContentType.Application.Json)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 30_000
        }

        install(HttpRequestRetry) {
            maxRetries = 2
            retryOnServerErrors()          // 5xx only, never 4xx
            retryOnException(retryOnTimeout = true)
            exponentialDelay()             // 1s, 2s
            // Only retry safe methods. Prevents double POST later when app gains writes.
            retryIf { request, _ -> request.method.value == "GET" }
        }

        if (config.isDebug) {
            install(Logging) {
                logger = Logger.ANDROID          // routes to logcat, tag "Ktor Client"
                level = LogLevel.HEADERS         // BODY is verbose for list endpoints; bump when debugging parse issues
                sanitizeHeader { it == HttpHeaders.Authorization }
                // Query-param API key still appears in URL line. Acceptable in debug builds only.
            }
        }
    }

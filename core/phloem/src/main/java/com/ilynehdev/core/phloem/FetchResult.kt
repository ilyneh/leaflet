package com.ilynehdev.core.phloem

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import java.io.IOException

sealed class FetchResult {
    data class Success(val hasMore: Boolean) : FetchResult()
    data class Error(val error: FetchError, val cause: Throwable) : FetchResult()
}

sealed class FetchError {
    data object General : FetchError()

    /** Device could not reach the server: no network, DNS failure, dropped connection. */
    data object Offline : FetchError()

    /** Server was reached but responded 5xx. */
    data object ServerDown : FetchError()

    /** 429: back off and let the next trigger resume the crawl. */
    data object RateLimited : FetchError()

    data object Forbidden : FetchError()
    data object StorageError : FetchError()
}

internal fun Throwable.toFetchError(): FetchError = when (this) {
    is ClientRequestException -> when (response.status) {
        HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden -> FetchError.Forbidden
        HttpStatusCode.TooManyRequests -> FetchError.RateLimited
        else -> FetchError.General
    }
    is ServerResponseException -> FetchError.ServerDown
    is IOException -> FetchError.Offline
    else -> FetchError.General
}

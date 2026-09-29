package com.ilynehdev.core.phloem

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import java.io.IOException


/** Fetch failures as values, so callers never see raw exceptions. Cancellation is always rethrown. */
sealed class FetchError {
    /** Anything unclassified, including 4xx responses other than 401, 403 and 429. */
    data object General : FetchError()

    /** Device could not reach the server: no network, DNS failure, dropped connection. */
    data object Offline : FetchError()

    /** Server was reached but responded 5xx. */
    data object ServerDown : FetchError()

    /** 429: back off and let the next trigger resume the crawl. */
    data object RateLimited : FetchError()

    /** 401 or 403: credentials are missing or rejected. */
    data object Forbidden : FetchError()

    /** The fetch succeeded but persisting the result failed. */
    data object StorageError : FetchError()
}

fun Throwable.toFetchError(): FetchError = when (this) {
    is ClientRequestException -> when (response.status) {
        HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden -> FetchError.Forbidden
        HttpStatusCode.TooManyRequests -> FetchError.RateLimited
        else -> FetchError.General
    }
    is ServerResponseException -> FetchError.ServerDown
    is IOException -> FetchError.Offline
    else -> FetchError.General
}

package com.ilynehdev.data.common

sealed interface RefreshResult {
    /** Network fetch succeeded; data refreshed. */
    data object Refreshed : RefreshResult

    /** Row was within TTL; no request made. */
    data object AlreadyFresh : RefreshResult

    /** Fetch failed; return cached data if any. */
    data class Failed(val reason: RefreshError) : RefreshResult
}

/** Data-layer failure reasons. */
enum class RefreshError { Offline, RateLimited, ServerDown, Unauthorized, StorageError, Unknown }

package com.ilynehdev.data.common

import com.ilynehdev.core.phloem.FetchError

sealed interface RefreshResult {
    /** Network fetch succeeded; the observed row now holds fresh data. */
    data object Refreshed : RefreshResult

    /** Row was within TTL; no request made. Never returned for forced refreshes. */
    data object AlreadyFresh : RefreshResult

    /** Fetch failed; cached data, if any, still stands. */
    data class Failed(val error: FetchError) : RefreshResult
}

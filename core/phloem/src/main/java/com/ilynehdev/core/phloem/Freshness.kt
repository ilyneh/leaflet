package com.ilynehdev.core.phloem

import com.ilynehdev.core.time.TimeProvider
import kotlin.time.Duration

/** TTL policy for a resource. [newTimestamp] stamps a write and [isFresh] checks it. */
class Freshness(
    private val ttl: Duration,
    private val timeProvider: TimeProvider,
) {
    fun isFresh(syncedAt: Long?): Boolean =
        syncedAt != null && timeProvider.currentTimeMillis() - syncedAt < ttl.inWholeMilliseconds

    fun newTimestamp(): Long = timeProvider.currentTimeMillis()
}

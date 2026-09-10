package com.ilynehdev.core.phloem

import kotlin.time.Duration

class Freshness(
    private val ttl: Duration,
    private val now: () -> Long = System::currentTimeMillis
) {
    fun isFresh(syncedAt: Long?): Boolean =
        syncedAt != null && now() - syncedAt < ttl.inWholeMilliseconds

    fun newTimestamp(): Long = now()
}

package com.ilynehdev.core.time

interface TimeProvider {
    fun currentTimeMillis(): Long
}

internal class SystemTimeProvider : TimeProvider {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}

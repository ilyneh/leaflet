package com.ilynehdev.core.time

import org.junit.Assert.assertTrue
import org.junit.Test

class SystemTimeProviderTest {

    @Test
    fun `currentTimeMillis reads the system clock`() {
        val before = System.currentTimeMillis()
        val actual = SystemTimeProvider().currentTimeMillis()
        val after = System.currentTimeMillis()
        assertTrue("expected $actual in $before..$after", actual in before..after)
    }
}

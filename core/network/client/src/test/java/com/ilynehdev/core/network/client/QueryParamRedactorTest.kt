package com.ilynehdev.core.network.client

import org.junit.Assert.assertEquals
import org.junit.Test

class QueryParamRedactorTest {

    private val redactor = QueryParamRedactor(setOf("key"))

    @Test
    fun `masks the value of a named param`() {
        assertEquals(
            "GET https://example.test/api/v2/species-list?key=[REDACTED]&page=1",
            redactor.redact("GET https://example.test/api/v2/species-list?key=s3cret&page=1"),
        )
    }

    @Test
    fun `masks a param that is not first`() {
        assertEquals(
            "https://example.test/api/?page=1&key=[REDACTED]",
            redactor.redact("https://example.test/api/?page=1&key=s3cret"),
        )
    }

    @Test
    fun `keeps the param name instead of rewriting it`() {
        assertEquals(
            "https://example.test/api/?token=[REDACTED]",
            QueryParamRedactor(setOf("token")).redact("https://example.test/api/?token=s3cret"),
        )
    }

    @Test
    fun `does not match a name nested in a longer one`() {
        val line = "https://example.test/api/?monkey=banana"
        assertEquals(line, redactor.redact(line))
    }

    @Test
    fun `masks every named param`() {
        assertEquals(
            "https://example.test/api/?key=[REDACTED]&token=[REDACTED]",
            QueryParamRedactor(setOf("key", "token"))
                .redact("https://example.test/api/?key=s3cret&token=other"),
        )
    }

    @Test
    fun `masks regardless of case`() {
        assertEquals(
            "https://example.test/api/?KEY=[REDACTED]",
            redactor.redact("https://example.test/api/?KEY=s3cret"),
        )
    }

    @Test
    fun `handles a param with an empty value`() {
        assertEquals(
            "https://example.test/api/?key=[REDACTED]&page=1",
            redactor.redact("https://example.test/api/?key=&page=1"),
        )
    }

    @Test
    fun `leaves the message untouched when nothing is redacted`() {
        val line = "https://example.test/api/?key=s3cret"
        assertEquals(line, QueryParamRedactor(emptySet()).redact(line))
    }
}

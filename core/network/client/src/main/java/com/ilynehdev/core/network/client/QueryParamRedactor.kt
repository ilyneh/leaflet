package com.ilynehdev.core.network.client

/**
 * Masks the values of named query parameters in log lines.
 *
 * @param paramNames The names of the query parameters to mask. Passed in because they are unique to each backend.
 */
internal class QueryParamRedactor(paramNames: Set<String>) {

    // Anchored to ?/& so a short name cannot match inside a longer one
    // (`key` must not redact `monkey=`). Compiled once: redact runs per request.
    private val patterns = paramNames.map { name ->
        Regex(
            pattern = """(?<=[?&])${Regex.escape(name)}=[^&\s]*""",
            option = RegexOption.IGNORE_CASE
        )
    }

    fun redact(message: String): String =
        patterns.fold(message) { acc, pattern ->
            pattern.replace(acc) { match ->
                "${match.value.substringBefore('=')}=$MASK"
            }
        }

    private companion object {
        const val MASK = "[REDACTED]"
    }
}

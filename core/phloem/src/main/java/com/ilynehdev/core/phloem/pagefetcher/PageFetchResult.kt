package com.ilynehdev.core.phloem.pagefetcher

import com.ilynehdev.core.phloem.FetchError

sealed class FetchResult {
    data class Success(val hasMore: Boolean) : FetchResult()
    data class Error(val error: FetchError, val cause: Throwable) : FetchResult()
}

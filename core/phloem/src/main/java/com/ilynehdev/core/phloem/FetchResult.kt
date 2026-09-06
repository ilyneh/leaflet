package com.ilynehdev.core.phloem

sealed class FetchResult {
    data object Success : FetchResult()
    data class Error(val error: FetchError) : FetchResult()
}

sealed class FetchError {
    data class General(val error: Throwable? = null) : FetchError()
    data object ServerDown : FetchError()
    data object Forbidden : FetchError()
    data object StorageError : FetchError()
}
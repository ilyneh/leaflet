package com.ilynehdev.core.network

sealed interface NetworkError {
    data object NoConnection : NetworkError
    data object Timeout : NetworkError
    data class Http(val code: Int) : NetworkError
    data object Serialization : NetworkError
    data object Unknown : NetworkError
}
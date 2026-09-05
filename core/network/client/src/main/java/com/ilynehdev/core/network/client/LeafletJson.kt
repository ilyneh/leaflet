package com.ilynehdev.core.network.client

import kotlinx.serialization.json.Json

val LeafletJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = false
    coerceInputValues = true
}

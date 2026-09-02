package com.ilynehdev.core.network.di

import com.ilynehdev.core.network.NetworkConfig
import com.ilynehdev.core.network.createHttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
            coerceInputValues = true
        }
    }
    single {
        NetworkConfig(
            baseUrl = "testurl",
            apiKey = "testapikey",
            isDebug = true
        )
    }
    single<HttpClientEngine> { OkHttp.create() }
    single { createHttpClient(get(), get(), get()) }
}
package com.ilynehdev.core.network.client.di

import com.ilynehdev.core.network.client.LeafletJson
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.dsl.module

val networkModule = module {
    single { LeafletJson }
    single<HttpClientEngine> { OkHttp.create() }
}

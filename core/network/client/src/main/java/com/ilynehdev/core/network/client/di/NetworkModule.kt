package com.ilynehdev.core.network.client.di

import com.ilynehdev.core.network.client.LeafletJson
import com.ilynehdev.core.network.client.NetworkConfig
import com.ilynehdev.core.network.client.createHttpClient
import com.ilynehdev.core.network.client.createPlantHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.module

object PlantsClient : Qualifier { override val value = "plants-client" }

val networkModule = module {
    single { LeafletJson }

    single<HttpClientEngine> { OkHttp.create() }

    // PlantsClient
    single<NetworkConfig>(PlantsClient) {
        NetworkConfig(
            baseUrl = "https://perenual.com/api/",
            apiKey = "TO-REPLACE",
            isDebug = true
        )
    }

    single<HttpClient>(PlantsClient) {
        createPlantHttpClient(
            engine = get(),
            json = get(),
            config = get(PlantsClient)
        )
    }
}
package com.ilynehdev.core.network.plants.di

import com.ilynehdev.core.network.plants.api.PlantDiseaseApiImpl
import com.ilynehdev.core.network.plants.api.PlantDiseasesApi
import com.ilynehdev.core.network.plants.api.PlantsApi
import com.ilynehdev.core.network.plants.api.PlantsApiImpl
import com.ilynehdev.core.network.plants.createPerenualHttpClient
import io.ktor.client.HttpClient
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.module

internal object PlantsClient : Qualifier { override val value = "plants-client" }

fun plantsNetworkModule(
    perenualApiKey: String,
    isDebug: Boolean,
) = module {
    single<HttpClient>(PlantsClient) {
        createPerenualHttpClient(
            engine = get(),
            json = get(),
            apiKey = perenualApiKey,
            isDebug = isDebug,
        )
    }

    single<PlantsApi> { PlantsApiImpl(get(PlantsClient)) }
    single<PlantDiseasesApi> { PlantDiseaseApiImpl(get(PlantsClient)) }
}

package com.ilynehdev.core.network.plants.di

import com.ilynehdev.core.network.client.di.PlantsClient
import com.ilynehdev.core.network.client.di.networkModule
import com.ilynehdev.core.network.plants.api.PlantDiseaseApiImpl
import com.ilynehdev.core.network.plants.api.PlantDiseasesApi
import com.ilynehdev.core.network.plants.api.PlantsApi
import com.ilynehdev.core.network.plants.api.PlantsApiImpl
import org.koin.dsl.module

val plantsNetworkModule = module {
    includes(networkModule)
    single<PlantsApi> { PlantsApiImpl(get(PlantsClient)) }
    single<PlantDiseasesApi> { PlantDiseaseApiImpl(get(PlantsClient)) }
}

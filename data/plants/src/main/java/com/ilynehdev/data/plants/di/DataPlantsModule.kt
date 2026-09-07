package com.ilynehdev.data.plants.di

import com.ilynehdev.core.database.di.databaseModule
import com.ilynehdev.core.network.plants.di.plantsNetworkModule
import com.ilynehdev.data.common.di.dataCommonModule
import com.ilynehdev.data.plants.repository.PlantsRepository
import com.ilynehdev.data.plants.repository.PlantsRepositoryImpl
import org.koin.dsl.module

val dataPlantsModule = module {
    includes(databaseModule)
    includes(dataCommonModule)
    includes(plantsNetworkModule)

    single<PlantsRepository> {
        PlantsRepositoryImpl(
            dao = get(),
            api = get(),
            transactor = get(),
            metadataStore = get()
        )
    }
}

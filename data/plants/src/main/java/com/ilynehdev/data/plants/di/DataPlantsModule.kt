package com.ilynehdev.data.plants.di

import com.ilynehdev.core.database.di.databaseModule
import com.ilynehdev.core.network.plants.di.plantsNetworkModule
import com.ilynehdev.core.phloem.Freshness
import com.ilynehdev.core.phloem.itemfetcher.PhloemItemFetcherImpl
import com.ilynehdev.data.common.di.dataCommonModule
import com.ilynehdev.data.plants.repository.PagedPlantsRepository
import com.ilynehdev.data.plants.repository.PagedPlantsRepositoryImpl
import com.ilynehdev.data.plants.repository.PlantsRepository
import com.ilynehdev.data.plants.repository.PlantsRepositoryImpl
import org.koin.dsl.module
import kotlin.time.Duration.Companion.days

// How long a fetched plant detail row is served without re-hitting the API.
private val PLANT_DETAILS_TTL = 7.days

val dataPlantsModule = module {
    includes(databaseModule)
    includes(dataCommonModule)
    includes(plantsNetworkModule)

    single<PagedPlantsRepository> {
        PagedPlantsRepositoryImpl(
            dao = get(),
            api = get(),
            transactor = get(),
            metadataStore = get()
        )
    }

    val clock: () -> Long = System::currentTimeMillis
    single<PlantsRepository> {
        PlantsRepositoryImpl(
            dao = get(),
            savedDao = get(),
            api = get(),
            fetcher = PhloemItemFetcherImpl(),
            freshness = Freshness(ttl = PLANT_DETAILS_TTL, now = clock),
            now = clock
        )
    }
}

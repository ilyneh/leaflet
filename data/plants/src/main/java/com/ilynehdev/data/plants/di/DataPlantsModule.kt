package com.ilynehdev.data.plants.di

import com.ilynehdev.core.phloem.Freshness
import com.ilynehdev.core.phloem.itemfetcher.PhloemItemFetcherImpl
import com.ilynehdev.data.plants.repository.PagedPlantsRepository
import com.ilynehdev.data.plants.repository.PagedPlantsRepositoryImpl
import com.ilynehdev.data.plants.repository.PlantsRepository
import com.ilynehdev.data.plants.repository.PlantsRepositoryImpl
import com.ilynehdev.data.plants.usecase.ObserveFilteredPlantsUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.time.Duration.Companion.days

// How long a fetched plant detail row is served without re-hitting the API.
private val PLANT_DETAILS_TTL = 7.days

val dataPlantsModule = module {
    singleOf(::PagedPlantsRepositoryImpl) { bind<PagedPlantsRepository>() }

    factoryOf(::ObserveFilteredPlantsUseCase)

    single<PlantsRepository> {
        PlantsRepositoryImpl(
            dao = get(),
            savedDao = get(),
            api = get(),
            fetcher = PhloemItemFetcherImpl(),
            freshness = Freshness(ttl = PLANT_DETAILS_TTL, timeProvider = get()),
            timeProvider = get(),
        )
    }
}

package com.ilynehdev.leaflet

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ilynehdev.data.plants.repository.PagedPlantsRepository
import com.ilynehdev.data.plants.repository.PlantsRepository
import com.ilynehdev.data.plants.usecase.ObserveFilteredPlantsUseCase
import com.ilynehdev.feature.plants.list.PlantsListViewModel
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import org.koin.core.context.stopKoin
import org.robolectric.annotation.Config


@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class KoinGraphTest {

    @After
    fun tearDown() = stopKoin()

    @Test
    fun `production graph resolves`() {
        val koin = GlobalContext.get()
        koin.get<PagedPlantsRepository>()
        koin.get<PlantsRepository>()
        koin.get<ObserveFilteredPlantsUseCase>()
        koin.get<PlantsListViewModel>()
    }
}

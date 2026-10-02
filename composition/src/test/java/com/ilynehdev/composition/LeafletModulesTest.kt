package com.ilynehdev.composition

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ilynehdev.core.network.plants.api.PlantDiseasesApi
import com.ilynehdev.core.network.plants.api.PlantsApi
import com.ilynehdev.data.plants.model.PlantId
import com.ilynehdev.data.plants.repository.PagedPlantsRepository
import com.ilynehdev.data.plants.repository.PlantsRepository
import com.ilynehdev.data.plants.usecase.ObserveFilteredPlantsUseCase
import com.ilynehdev.feature.plants.detail.PlantsDetailViewModel
import com.ilynehdev.feature.plants.list.PlantsListViewModel
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf


@RunWith(AndroidJUnit4::class)
class LeafletModulesTest {

    @After
    fun tearDown() = stopKoin()

    @Test
    fun `production graph resolves every entry point`() {
        val koin = startGraph()

        koin.get<PlantsApi>()
        koin.get<PlantDiseasesApi>()
        koin.get<PagedPlantsRepository>()
        koin.get<PlantsRepository>()
        koin.get<ObserveFilteredPlantsUseCase>()
        koin.get<PlantsListViewModel>()
    }

    @Test
    fun `detail view model resolves with its route argument`() {
        val koin = startGraph()

        koin.get<PlantsDetailViewModel> { parametersOf(PlantId(1)) }
    }

    private fun startGraph(): Koin = startKoin {
        androidContext(ApplicationProvider.getApplicationContext())
        modules(
            leafletModules(
                AppConfig(perenualApiKey = "test-key", isDebug = false)
            )
        )
    }.koin
}

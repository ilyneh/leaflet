package com.ilynehdev.feature.plants.di

import com.ilynehdev.data.plants.model.PlantId
import com.ilynehdev.feature.plants.detail.PlantsDetailViewModel
import com.ilynehdev.feature.plants.list.PlantsListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featurePlantsModule = module {
    viewModelOf(::PlantsListViewModel)
    viewModel { (plantId: PlantId) -> PlantsDetailViewModel(plantId, get()) }
}

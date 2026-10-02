package com.ilynehdev.feature.plants.presentation.di

import com.ilynehdev.feature.plants.data.model.PlantId
import com.ilynehdev.feature.plants.presentation.detail.PlantsDetailViewModel
import com.ilynehdev.feature.plants.presentation.list.PlantsListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val plantsPresentationModule = module {
    viewModelOf(::PlantsListViewModel)
    viewModel { (plantId: PlantId) -> PlantsDetailViewModel(plantId, get()) }
}

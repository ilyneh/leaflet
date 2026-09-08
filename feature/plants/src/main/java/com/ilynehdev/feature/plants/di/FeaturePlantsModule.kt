package com.ilynehdev.feature.plants.di

import com.ilynehdev.data.plants.di.dataPlantsModule
import com.ilynehdev.feature.plants.list.PlantsListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featurePlantsModule = module {
    includes(dataPlantsModule)

    viewModelOf(::PlantsListViewModel)
}

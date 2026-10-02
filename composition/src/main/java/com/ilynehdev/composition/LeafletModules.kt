package com.ilynehdev.composition

import com.ilynehdev.core.database.di.databaseModule
import com.ilynehdev.core.network.client.di.networkModule
import com.ilynehdev.core.network.plants.di.plantsNetworkModule
import com.ilynehdev.core.time.di.timeModule
import com.ilynehdev.core.data.di.coreDataModule
import com.ilynehdev.feature.plants.data.di.plantsDataModule
import com.ilynehdev.feature.plants.presentation.di.plantsPresentationModule

data class AppConfig(
    val perenualApiKey: String,
    val isDebug: Boolean,
)

fun leafletModules(config: AppConfig) = listOf(
    // core
    databaseModule,
    networkModule,
    plantsNetworkModule(
        perenualApiKey = config.perenualApiKey,
        isDebug = config.isDebug
    ),
    timeModule,
    // data
    coreDataModule,
    plantsDataModule,
    // feature
    plantsPresentationModule
)

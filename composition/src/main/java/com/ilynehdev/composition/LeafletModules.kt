package com.ilynehdev.composition

import com.ilynehdev.core.database.di.databaseModule
import com.ilynehdev.core.network.client.di.networkModule
import com.ilynehdev.core.network.plants.di.plantsNetworkModule
import com.ilynehdev.core.time.di.timeModule
import com.ilynehdev.data.common.di.dataCommonModule
import com.ilynehdev.data.plants.di.dataPlantsModule
import com.ilynehdev.feature.plants.di.featurePlantsModule

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
    dataCommonModule,
    dataPlantsModule,
    // feature
    featurePlantsModule
)

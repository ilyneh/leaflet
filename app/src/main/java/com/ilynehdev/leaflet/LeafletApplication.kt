package com.ilynehdev.leaflet

import android.app.Application
import com.ilynehdev.core.network.client.NetworkConfig
import com.ilynehdev.core.network.client.di.PlantsClient
import com.ilynehdev.feature.plants.di.featurePlantsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class LeafletApplication : Application() {

    // Composition root: config that needs BuildConfig lives here, not in library modules.
    private val appModule = module {
        single<NetworkConfig>(PlantsClient) {
            NetworkConfig(
                baseUrl = "https://perenual.com/api/",
                apiKey = BuildConfig.PERENUAL_API_KEY,
                isDebug = BuildConfig.DEBUG,
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@LeafletApplication)
            // featurePlantsModule transitively includes data, database, common and network modules.
            modules(featurePlantsModule, appModule)
        }
    }
}

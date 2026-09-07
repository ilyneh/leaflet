package com.ilynehdev.leaflet

import android.app.Application
import com.ilynehdev.feature.plants.di.featurePlantsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class LeafletApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@LeafletApplication)
            // featurePlantsModule transitively includes data, database, common and network modules.
            modules(featurePlantsModule)
        }
    }
}

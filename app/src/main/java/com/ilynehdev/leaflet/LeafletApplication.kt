package com.ilynehdev.leaflet

import android.app.Application
import com.ilynehdev.data.plants.di.dataPlantsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class LeafletApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@LeafletApplication)
            // dataPlantsModule transitively includes database, common and network modules.
            modules(dataPlantsModule)
        }
    }
}

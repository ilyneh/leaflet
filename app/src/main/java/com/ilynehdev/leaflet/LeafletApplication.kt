package com.ilynehdev.leaflet

import android.app.Application
import com.ilynehdev.composition.AppConfig
import com.ilynehdev.composition.leafletModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class LeafletApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@LeafletApplication)

            val appConfig = AppConfig(
                perenualApiKey = BuildConfig.PERENUAL_API_KEY,
                isDebug = BuildConfig.DEBUG
            )
            modules(
                leafletModules(appConfig)
            )
        }
    }
}

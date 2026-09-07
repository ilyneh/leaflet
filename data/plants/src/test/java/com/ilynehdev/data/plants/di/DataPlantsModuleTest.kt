package com.ilynehdev.data.plants.di

import android.content.Context
import com.ilynehdev.core.network.client.NetworkConfig
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify

class DataPlantsModuleTest {

    /**
     * Static graph check: every definition's dependencies must be resolvable
     * from the module tree. Catches binding mistakes (e.g. a concrete type
     * bound where an interface is requested) without building anything.
     * Context is provided at runtime by startKoin { androidContext(...) }.
     */
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `dataPlantsModule graph is complete`() {
        dataPlantsModule.verify(
            extraTypes = listOf(
                Context::class,
                // NetworkConfig is provided by the app composition root
                // (needs BuildConfig for the API key), so it is absent here.
                NetworkConfig::class,
                String::class,
                Boolean::class,
            )
        )
    }
}

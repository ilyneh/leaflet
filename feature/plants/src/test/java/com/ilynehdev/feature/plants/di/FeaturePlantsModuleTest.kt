package com.ilynehdev.feature.plants.di

import android.content.Context
import com.ilynehdev.core.network.client.NetworkConfig
import org.junit.Test
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.test.verify.verify

class FeaturePlantsModuleTest {

    /**
     * Static graph check: every definition's dependencies must be resolvable
     * from the module tree. Only sees constructors of declared types, so
     * `single<Interface> { Impl(get()) }` definitions are covered by
     * KoinGraphTest in :app instead.
     * Context is provided at runtime by startKoin { androidContext(...) }.
     */
    @OptIn(KoinExperimentalAPI::class)
    @Test
    fun `featurePlantsModule graph is complete`() {
        featurePlantsModule.verify(
            extraTypes = listOf(
                Context::class,
                // NetworkConfig is provided by the app composition root
                // (needs BuildConfig for the API key), so it is absent here.
                NetworkConfig::class,
                String::class,
                Boolean::class,
            ),
        )
    }
}

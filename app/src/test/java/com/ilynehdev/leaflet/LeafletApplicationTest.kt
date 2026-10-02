package com.ilynehdev.leaflet

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ilynehdev.data.plants.repository.PlantsRepository
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import org.koin.core.context.stopKoin


@RunWith(AndroidJUnit4::class)
class LeafletApplicationTest {

    @After
    fun tearDown() = stopKoin()

    @Test
    fun `starts koin with a graph the app can resolve from`() {
        val koin = GlobalContext.get()

        // Verifying DB & API hookups
        koin.get<PlantsRepository>()
    }
}

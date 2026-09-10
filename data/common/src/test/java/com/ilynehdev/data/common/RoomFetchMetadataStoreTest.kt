package com.ilynehdev.data.common

import android.content.Context
import androidx.room3.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ilynehdev.core.database.LeafletDatabase
import com.ilynehdev.core.phloem.pagefetcher.FetchMetadata
import com.ilynehdev.core.phloem.pagefetcher.PhloemModel
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class RoomFetchMetadataStoreTest {

    private lateinit var db: LeafletDatabase
    private lateinit var store: RoomFetchMetadataStore

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, LeafletDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        store = RoomFetchMetadataStore(db.fetchMetadataDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `get returns null for unknown model`() = runTest {
        assertNull(store.get(PhloemModel.PlantCatalog))
    }

    @Test
    fun `save then get round trips both fields`() = runTest {
        store.save(PhloemModel.PlantCatalog, FetchMetadata(cursor = "7", completedAt = 123L))

        assertEquals(
            FetchMetadata(cursor = "7", completedAt = 123L),
            store.get(PhloemModel.PlantCatalog),
        )
    }

    @Test
    fun `null fields survive round trip`() = runTest {
        store.save(PhloemModel.PlantCatalog, FetchMetadata(cursor = null, completedAt = null))

        assertEquals(
            FetchMetadata(cursor = null, completedAt = null),
            store.get(PhloemModel.PlantCatalog),
        )
    }

    @Test
    fun `save upserts instead of duplicating and models stay isolated`() = runTest {
        store.save(PhloemModel.PlantCatalog, FetchMetadata(cursor = "2", completedAt = null))
        store.save(PhloemModel.PlantDiseaseCatalog, FetchMetadata(cursor = null, completedAt = 99L))
        store.save(PhloemModel.PlantCatalog, FetchMetadata(cursor = null, completedAt = 555L))

        assertEquals(
            FetchMetadata(cursor = null, completedAt = 555L),
            store.get(PhloemModel.PlantCatalog),
        )
        assertEquals(
            FetchMetadata(cursor = null, completedAt = 99L),
            store.get(PhloemModel.PlantDiseaseCatalog),
        )
    }
}

package com.ilynehdev.core.database

import android.content.Context
import androidx.paging.PagingSource
import androidx.room3.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ilynehdev.core.database.dao.PlantsDao
import com.ilynehdev.core.database.entities.HardinessColumn
import com.ilynehdev.core.database.entities.ImageColumn
import com.ilynehdev.core.database.entities.DimensionsColumn
import com.ilynehdev.core.database.entities.PlantEntity
import kotlinx.coroutines.flow.first
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
class PlantDaoTest {

    private lateinit var db: LeafletDatabase
    private lateinit var dao: PlantsDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, LeafletDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.plantsDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun plant(
        id: Long,
        commonName: String? = "Plant $id",
    ) = PlantEntity(
        id = id,
        commonName = commonName,
        scientificName = listOf("Abies alba"),
        family = "Pinaceae",
        type = "tree",
        cycle = "PERENNIAL",
        watering = "FREQUENT",
        genus = "Abies",
        speciesEpithet = "alba",
        cultivar = null,
        variety = null,
        wateringBenchmark = null,
    )

    // ---- round trip ----
    @Test
    fun `upsert then getById returns equal entity`() = runTest {
        val original = plant(1).copy(
            sunlight = listOf("full sun", "part shade"),
            dimensions = listOf(DimensionsColumn("Height", 60.0, 60.0, "feet")),
            hardiness = HardinessColumn(min = "7", max = "7"),
            defaultImage = ImageColumn(thumbnail = "https://img/thumb.jpg"),
        )

        dao.upsertPlant(original)

        assertEquals(original, dao.getById(1))
    }

    @Test
    fun `null lists and embedded objects survive round trip`() = runTest {
        dao.upsertPlant(plant(1))

        val loaded = dao.getById(1)

        assertNull(loaded?.sunlight)
        assertNull(loaded?.dimensions)
        assertNull(loaded?.hardiness)
        assertNull(loaded?.defaultImage)
    }

    @Test
    fun `getById returns null for missing row`() = runTest {
        assertNull(dao.getById(99))
    }

    // ---- upsert semantics ----
    @Test
    fun `upsert with same id updates instead of duplicating`() = runTest {
        dao.upsertPlant(plant(1, commonName = "Old name"))
        dao.upsertPlant(plant(1, commonName = "New name"))

        assertEquals("New name", dao.getById(1)?.commonName)
        assertEquals(1, dao.observeSummaries().first().size)
    }

    @Test
    fun `upsertPlants inserts all rows`() = runTest {
        dao.upsertPlants(listOf(plant(1), plant(2), plant(3)))

        assertEquals(3, dao.observeSummaries().first().size)
    }

    // ---- summaries ----
    @Test
    fun `observeSummaries maps columns and orders by common name`() = runTest {
        dao.upsertPlants(
            listOf(
                plant(1, commonName = "Zebra Plant")
                    .copy(defaultImage = ImageColumn(thumbnail = "https://img/1.jpg")),
                plant(2, commonName = "Aloe Vera"),
            )
        )

        val summaries = dao.observeSummaries().first()

        assertEquals(listOf("Aloe Vera", "Zebra Plant"), summaries.map { it.commonName })
        val zebra = summaries.single { it.id == 1L }
        assertEquals(listOf("Abies alba"), zebra.scientificName)
        assertEquals("FREQUENT", zebra.watering)
        assertEquals("https://img/1.jpg", zebra.thumbnail)
        assertNull(summaries.single { it.id == 2L }.thumbnail)
    }

    // ---- lists / paging ----
    @Test
    fun `observePlants returns full entities ordered by common name`() = runTest {
        val zebra = plant(1, commonName = "Zebra Plant")
        val aloe = plant(2, commonName = "Aloe Vera")
        dao.upsertPlants(listOf(zebra, aloe))

        assertEquals(listOf(aloe, zebra), dao.observePlants().first())
    }

    @Test
    fun `pagedPlants loads pages in order`() = runTest {
        dao.upsertPlants((1L..5L).map { plant(it, commonName = "Plant %02d".format(it)) })

        val page = dao.pagedPlants().load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 3, placeholdersEnabled = false)
        ) as PagingSource.LoadResult.Page

        assertEquals(listOf(1L, 2L, 3L), page.data.map { it.id })
        assertEquals(3, page.nextKey)
    }

    @Test
    fun `pagedSummaries loads summary rows in order`() = runTest {
        dao.upsertPlants((1L..5L).map { plant(it, commonName = "Plant %02d".format(it)) })

        val page = dao.pagedSummaries().load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 3, placeholdersEnabled = false)
        ) as PagingSource.LoadResult.Page

        assertEquals(listOf("Plant 01", "Plant 02", "Plant 03"), page.data.map { it.commonName })
        assertEquals(3, page.nextKey)
    }

    @Test
    fun `observeSummaries re-emits after upsert`() = runTest {
        dao.upsertPlant(plant(1))
        assertEquals(1, dao.observeSummaries().first().size)

        dao.upsertPlant(plant(2))
        assertEquals(2, dao.observeSummaries().first().size)
    }
}

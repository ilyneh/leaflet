package com.ilynehdev.core.database

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room3.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ilynehdev.core.database.entities.PlantEntity
import com.ilynehdev.core.database.entities.SavedPlantEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [34])
class SavedPlantsDaoTest {

    private lateinit var db: LeafletDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, LeafletDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun plant(id: Long, commonName: String) = PlantEntity(
        id = id,
        commonName = commonName,
        scientificName = listOf("$commonName latinus"),
        family = null,
        type = null,
        cycle = null,
        watering = "Average",
        sunlight = listOf("full sun"),
        wateringBenchmark = null,
    )

    private suspend fun seedPlants(vararg ids: Long) {
        db.plantsDao().upsertPlants(ids.map { plant(it, "Plant $it") })
    }

    @Test
    fun `observeIsSaved is false for unsaved plant`() = runTest {
        seedPlants(1)

        assertFalse(db.savedPlantsDao().observeIsSaved(1L).first())
    }

    @Test
    fun `saving a plant flips observeIsSaved`() = runTest {
        seedPlants(1)

        db.savedPlantsDao().upsertSavedPlant(SavedPlantEntity(plantId = 1, savedAt = 100))

        assertTrue(db.savedPlantsDao().observeIsSaved(1L).first())
    }

    @Test
    fun `saving twice keeps a single row and updates savedAt`() = runTest {
        seedPlants(1)
        val dao = db.savedPlantsDao()

        dao.upsertSavedPlant(SavedPlantEntity(plantId = 1, savedAt = 100))
        dao.upsertSavedPlant(SavedPlantEntity(plantId = 1, savedAt = 200))

        val rows = dao.observeSavedPlants().first()
        assertEquals(1, rows.size)
        assertEquals(200L, rows.single().savedAt)
    }

    @Test
    fun `observeSavedPlants joins plant fields for saved rows only`() = runTest {
        seedPlants(1, 2, 3)
        val dao = db.savedPlantsDao()

        dao.upsertSavedPlants(
            listOf(
                SavedPlantEntity(plantId = 1, savedAt = 100),
                SavedPlantEntity(plantId = 3, savedAt = 200),
            )
        )

        val rows = dao.observeSavedPlants().first()

        assertEquals(setOf(1L, 3L), rows.map { it.id }.toSet())
        val row = rows.single { it.id == 1L }
        assertEquals("Plant 1", row.commonName)
        assertEquals(listOf("Plant 1 latinus"), row.scientificName)
        assertEquals("Average", row.watering)
        assertEquals(listOf("full sun"), row.sunlight)
        assertEquals(100L, row.savedAt)
    }

    @Test
    fun `observeSavedPlants is empty with no saves`() = runTest {
        seedPlants(1)

        assertTrue(db.savedPlantsDao().observeSavedPlants().first().isEmpty())
    }

    @Test
    fun `saving a plant that is not in the catalog violates the foreign key`() = runTest {
        val result = runCatching {
            db.savedPlantsDao().upsertSavedPlant(SavedPlantEntity(plantId = 99, savedAt = 100))
        }

        assertTrue(result.exceptionOrNull() is SQLiteConstraintException)
    }
}

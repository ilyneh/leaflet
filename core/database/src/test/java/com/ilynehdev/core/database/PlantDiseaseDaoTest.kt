package com.ilynehdev.core.database

import android.content.Context
import androidx.paging.PagingSource
import androidx.room3.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ilynehdev.core.database.dao.PlantDiseaseDao
import com.ilynehdev.core.database.entities.ImageColumn
import com.ilynehdev.core.database.entities.PlantDiseaseEntity
import com.ilynehdev.core.database.entities.PlantDiseaseSectionColumn
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
class PlantDiseaseDaoTest {

    private lateinit var db: LeafletDatabase
    private lateinit var dao: PlantDiseaseDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, LeafletDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.plantDiseaseDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun disease(
        id: Long,
        commonName: String? = "Disease $id",
        images: List<ImageColumn>? = null,
    ) = PlantDiseaseEntity(
        id = id,
        commonName = commonName,
        scientificName = "Agrocybe",
        host = listOf("all lawn grasses"),
        images = images,
    )

    // ---- round trip ----
    @Test
    fun `upsert then getById returns equal entity`() = runTest {
        val original = disease(1).copy(
            otherName = listOf("Nuisance fungi"),
            description = listOf(PlantDiseaseSectionColumn("What is it?", "A lawn issue")),
            solution = listOf(PlantDiseaseSectionColumn("Cultural Practices", "Aerate the soil")),
            images = listOf(ImageColumn(thumbnail = "https://img/thumb.jpg", license = 45)),
        )

        dao.upsertPlantDisease(original)

        assertEquals(original, dao.getById(1))
    }

    @Test
    fun `null lists survive round trip`() = runTest {
        dao.upsertPlantDisease(disease(1))

        val loaded = dao.getById(1)

        assertNull(loaded?.otherName)
        assertNull(loaded?.description)
        assertNull(loaded?.solution)
        assertNull(loaded?.images)
    }

    @Test
    fun `empty section lists stay empty not null`() = runTest {
        dao.upsertPlantDisease(disease(1).copy(description = emptyList(), solution = emptyList()))

        val loaded = dao.getById(1)

        assertEquals(emptyList<PlantDiseaseSectionColumn>(), loaded?.description)
        assertEquals(emptyList<PlantDiseaseSectionColumn>(), loaded?.solution)
    }

    // ---- upsert semantics ----
    @Test
    fun `upsert with same id updates instead of duplicating`() = runTest {
        dao.upsertPlantDisease(disease(1, commonName = "Old"))
        dao.upsertPlantDisease(disease(1, commonName = "New"))

        assertEquals("New", dao.getById(1)?.commonName)
        assertEquals(1, dao.observeSummaries().first().size)
    }

    // ---- summaries ----
    @Test
    fun `observeSummaries extracts first image thumbnail from json`() = runTest {
        dao.upsertPlantDiseases(
            listOf(
                disease(
                    1,
                    commonName = "Fairy ring",
                    images = listOf(
                        ImageColumn(thumbnail = "https://img/first.jpg"),
                        ImageColumn(thumbnail = "https://img/second.jpg"),
                    ),
                ),
                disease(2, commonName = "Anthracnose", images = null),
                disease(3, commonName = "Blight", images = emptyList()),
            )
        )

        val summaries = dao.observeSummaries().first()

        assertEquals(listOf("Anthracnose", "Blight", "Fairy ring"), summaries.map { it.commonName })
        assertEquals("https://img/first.jpg", summaries.single { it.id == 1L }.thumbnail)
        assertNull(summaries.single { it.id == 2L }.thumbnail)
        assertNull(summaries.single { it.id == 3L }.thumbnail)
    }

    // ---- lists / paging ----
    @Test
    fun `observePlantDiseases returns full entities ordered by common name`() = runTest {
        val rust = disease(1, commonName = "Rust")
        val blight = disease(2, commonName = "Blight")
        dao.upsertPlantDiseases(listOf(rust, blight))

        assertEquals(listOf(blight, rust), dao.observePlantDiseases().first())
    }

    @Test
    fun `pagedPlantDiseases loads pages in order`() = runTest {
        dao.upsertPlantDiseases((1L..5L).map { disease(it, commonName = "Disease %02d".format(it)) })

        val page = dao.pagedPlantDiseases().load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 3, placeholdersEnabled = false)
        ) as PagingSource.LoadResult.Page

        assertEquals(listOf(1L, 2L, 3L), page.data.map { it.id })
        assertEquals(3, page.nextKey)
    }

    @Test
    fun `pagedSummaries loads summary rows with derived thumbnail`() = runTest {
        dao.upsertPlantDiseases(
            (1L..5L).map {
                disease(
                    it,
                    commonName = "Disease %02d".format(it),
                    images = listOf(ImageColumn(thumbnail = "https://img/$it.jpg")),
                )
            }
        )

        val page = dao.pagedSummaries().load(
            PagingSource.LoadParams.Refresh(key = null, loadSize = 3, placeholdersEnabled = false)
        ) as PagingSource.LoadResult.Page

        assertEquals(listOf("Disease 01", "Disease 02", "Disease 03"), page.data.map { it.commonName })
        assertEquals("https://img/1.jpg", page.data.first().thumbnail)
        assertEquals(3, page.nextKey)
    }

    @Test
    fun `observeSummaries maps scalar and list columns`() = runTest {
        dao.upsertPlantDisease(disease(1))

        val summary = dao.observeSummaries().first().single()

        assertEquals(1L, summary.id)
        assertEquals("Disease 1", summary.commonName)
        assertEquals("Agrocybe", summary.scientificName)
        assertEquals(listOf("all lawn grasses"), summary.host)
    }
}

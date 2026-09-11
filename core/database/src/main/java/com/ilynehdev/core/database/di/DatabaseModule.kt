package com.ilynehdev.core.database.di

import androidx.room3.Room
import com.ilynehdev.core.database.LeafletDatabase
import com.ilynehdev.core.database.MIGRATION_1_2
import com.ilynehdev.core.database.MIGRATION_2_3
import com.ilynehdev.core.database.MIGRATION_3_4
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            context = androidContext(),
            klass = LeafletDatabase::class.java,
            name = "leaflet.db"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .build()
    }

    single { get<LeafletDatabase>().fetchMetadataDao() }

    single { get<LeafletDatabase>().plantsDao() }
    single { get<LeafletDatabase>().plantDiseasesDao() }
    single { get<LeafletDatabase>().savedPlantsDao() }
}
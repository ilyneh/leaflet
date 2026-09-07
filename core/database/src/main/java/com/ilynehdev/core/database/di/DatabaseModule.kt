package com.ilynehdev.core.database.di

import androidx.room3.Room
import com.ilynehdev.core.database.LeafletDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            context = androidContext(),
            klass = LeafletDatabase::class.java,
            name = "leaflet.db"
        ).build()
    }

    single { get<LeafletDatabase>().fetchMetadataDao() }

    single { get<LeafletDatabase>().plantDao() }
    single { get<LeafletDatabase>().plantDiseaseDao() }
}
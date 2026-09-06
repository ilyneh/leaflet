package com.ilynehdev.core.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import com.ilynehdev.core.database.entities.FetchMetadataEntity

@Dao
interface FetchMetadataDao {

    @Query("SELECT * FROM fetch_metadata WHERE model = :model")
    suspend fun get(model: String): FetchMetadataEntity?

    @Upsert
    suspend fun upsert(entity: FetchMetadataEntity)
}

package com.ilynehdev.core.database.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "fetch_metadata")
data class FetchMetadataEntity(
    @PrimaryKey val model: String,
    val cursor: String?,
    @ColumnInfo("completed_at") val completedAt: Long?,
)

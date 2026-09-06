package com.ilynehdev.core.database.converters

import androidx.room3.ColumnTypeConverter
import com.ilynehdev.core.database.entities.AnatomyColumn
import com.ilynehdev.core.database.entities.DimensionsColumn
import com.ilynehdev.core.database.entities.ImageColumn
import com.ilynehdev.core.database.entities.PlantDiseaseSectionColumn
import com.ilynehdev.core.database.entities.PruningCountColumn
import kotlinx.serialization.json.Json

class PlantConverters(private val json: Json = Json) {
    @ColumnTypeConverter fun stringListToJson(value: List<String>): String = json.encodeToString(value)
    @ColumnTypeConverter fun jsonToStringList(value: String): List<String> = json.decodeFromString(value)

    @ColumnTypeConverter fun dimensionsToJson(value: List<DimensionsColumn>): String = json.encodeToString(value)
    @ColumnTypeConverter fun jsonToDimensions(value: String): List<DimensionsColumn> = json.decodeFromString(value)

    @ColumnTypeConverter fun pruningToJson(value: List<PruningCountColumn>): String = json.encodeToString(value)
    @ColumnTypeConverter fun jsonToPruning(value: String): List<PruningCountColumn> = json.decodeFromString(value)

    @ColumnTypeConverter fun anatomyToJson(value: List<AnatomyColumn>): String = json.encodeToString(value)
    @ColumnTypeConverter fun jsonToAnatomy(value: String): List<AnatomyColumn> = json.decodeFromString(value)

    @ColumnTypeConverter fun sectionsToJson(value: List<PlantDiseaseSectionColumn>): String = json.encodeToString(value)
    @ColumnTypeConverter fun jsonToSections(value: String): List<PlantDiseaseSectionColumn> = json.decodeFromString(value)

    @ColumnTypeConverter fun imagesToJson(value: List<ImageColumn>): String = json.encodeToString(value)
    @ColumnTypeConverter fun jsonToImages(value: String): List<ImageColumn> = json.decodeFromString(value)
}
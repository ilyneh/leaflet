package com.ilynehdev.data.common

import com.ilynehdev.core.database.dao.FetchMetadataDao
import com.ilynehdev.core.database.entities.FetchMetadataEntity
import com.ilynehdev.core.phloem.pagefetcher.FetchMetadata
import com.ilynehdev.core.phloem.pagefetcher.FetchMetadataStore
import com.ilynehdev.core.phloem.pagefetcher.PhloemModel

class RoomFetchMetadataStore(private val dao: FetchMetadataDao) : FetchMetadataStore {

    override suspend fun get(model: PhloemModel): FetchMetadata? =
        dao.get(model.key)?.let {
            FetchMetadata(cursor = it.cursor, completedAt = it.completedAt)
        }

    override suspend fun save(model: PhloemModel, metadata: FetchMetadata) {
        dao.upsert(
            FetchMetadataEntity(
                model = model.key,
                cursor = metadata.cursor,
                completedAt = metadata.completedAt,
            )
        )
    }
}

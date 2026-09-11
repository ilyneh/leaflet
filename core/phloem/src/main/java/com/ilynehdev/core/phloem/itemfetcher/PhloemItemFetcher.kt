package com.ilynehdev.core.phloem.itemfetcher

import com.ilynehdev.core.phloem.FetchError
import com.ilynehdev.core.phloem.toFetchError
import kotlin.coroutines.cancellation.CancellationException

interface PhloemItemFetcher {
    suspend fun <Dto> fetchItem(
        fetch: suspend () -> Dto,
        persist: suspend (Dto) -> Unit
    ): FetchError?
}

class PhloemItemFetcherImpl : PhloemItemFetcher {
    override suspend fun <Dto> fetchItem(
        fetch: suspend () -> Dto,
        persist: suspend (Dto) -> Unit
    ) : FetchError? {
        val dto = try {
            fetch()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return e.toFetchError()
        }

        return try {
            persist(dto)
            null
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            FetchError.StorageError
        }
    }
}

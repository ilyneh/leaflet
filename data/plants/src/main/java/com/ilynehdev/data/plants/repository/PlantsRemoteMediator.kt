package com.ilynehdev.data.plants.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.ilynehdev.core.database.projections.PlantSummaryRow
import com.ilynehdev.core.network.plants.dto.PlantDto
import com.ilynehdev.core.phloem.PhloemFetcher
import kotlinx.coroutines.CancellationException

@OptIn(ExperimentalPagingApi::class)
class PlantsRemoteMediator(
    private val fetcher: PhloemFetcher<PlantDto>,
) : RemoteMediator<Int, PlantSummaryRow>() {

    override suspend fun initialize(): InitializeAction =
        if (fetcher.isFresh()) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PlantSummaryRow>
    ): MediatorResult = when (loadType) {
        // List only grows at the end; nothing to load above the first row.
        LoadType.PREPEND -> MediatorResult.Success(endOfPaginationReached = true)
        LoadType.REFRESH,
        LoadType.APPEND -> try {
            val hasMore = fetcher.pullNextPage()
            MediatorResult.Success(endOfPaginationReached = !hasMore)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}

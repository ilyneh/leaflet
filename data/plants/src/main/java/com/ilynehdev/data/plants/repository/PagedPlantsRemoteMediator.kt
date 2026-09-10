package com.ilynehdev.data.plants.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.ilynehdev.core.database.projections.PlantSummaryRow
import com.ilynehdev.core.network.plants.dto.PlantDto
import com.ilynehdev.core.phloem.pagefetcher.FetchResult
import com.ilynehdev.core.phloem.pagefetcher.PhloemPageFetcher

@OptIn(ExperimentalPagingApi::class)
internal class PagedPlantsRemoteMediator(
    private val fetcher: PhloemPageFetcher<PlantDto>,
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
        LoadType.APPEND -> if (fetcher.isFresh()) {
            // Completed catalog within TTL: the local table IS the full list.
            // Without this, APPEND at the list end would restart the crawl
            // from page 1 (completion leaves the cursor null).
            MediatorResult.Success(endOfPaginationReached = true)
        } else {
            when (val result = fetcher.pullNextPage()) {
                is FetchResult.Success -> MediatorResult.Success(endOfPaginationReached = !result.hasMore)
                is FetchResult.Error -> MediatorResult.Error(result.cause)
            }
        }
    }
}

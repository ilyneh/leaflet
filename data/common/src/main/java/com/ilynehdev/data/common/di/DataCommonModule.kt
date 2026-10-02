package com.ilynehdev.data.common.di

import com.ilynehdev.core.phloem.Transactor
import com.ilynehdev.core.phloem.pagefetcher.FetchMetadataStore
import com.ilynehdev.data.common.RoomFetchMetadataStore
import com.ilynehdev.data.common.RoomTransactor
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataCommonModule = module {
    singleOf(::RoomTransactor) { bind<Transactor>() }
    singleOf(::RoomFetchMetadataStore) { bind<FetchMetadataStore>() }
}

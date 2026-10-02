package com.ilynehdev.core.data.di

import com.ilynehdev.core.phloem.Transactor
import com.ilynehdev.core.phloem.pagefetcher.FetchMetadataStore
import com.ilynehdev.core.data.RoomFetchMetadataStore
import com.ilynehdev.core.data.RoomTransactor
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val coreDataModule = module {
    singleOf(::RoomTransactor) { bind<Transactor>() }
    singleOf(::RoomFetchMetadataStore) { bind<FetchMetadataStore>() }
}

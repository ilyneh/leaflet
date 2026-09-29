package com.ilynehdev.data.common.di

import com.ilynehdev.core.database.di.databaseModule
import com.ilynehdev.core.phloem.pagefetcher.FetchMetadataStore
import com.ilynehdev.core.phloem.Transactor
import com.ilynehdev.core.time.di.timeModule
import com.ilynehdev.data.common.RoomFetchMetadataStore
import com.ilynehdev.data.common.RoomTransactor
import org.koin.dsl.module

val dataCommonModule = module {
    includes(databaseModule, timeModule)

    single<FetchMetadataStore> { RoomFetchMetadataStore(get()) }
    single<Transactor> { RoomTransactor(get()) }
}

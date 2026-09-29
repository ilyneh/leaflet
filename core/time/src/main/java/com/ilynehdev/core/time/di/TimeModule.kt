package com.ilynehdev.core.time.di

import com.ilynehdev.core.time.SystemTimeProvider
import com.ilynehdev.core.time.TimeProvider
import org.koin.dsl.module

val timeModule = module {
    single<TimeProvider> { SystemTimeProvider() }
}

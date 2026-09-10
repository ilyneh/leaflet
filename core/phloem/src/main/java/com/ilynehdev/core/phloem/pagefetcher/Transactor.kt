package com.ilynehdev.core.phloem.pagefetcher

fun interface Transactor {
    suspend fun transaction(block: suspend () -> Unit)
}
package com.ilynehdev.core.phloem

fun interface Transactor {
    suspend fun transaction(block: suspend () -> Unit)
}
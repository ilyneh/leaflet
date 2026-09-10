package com.ilynehdev.data.common

import androidx.room3.withWriteTransaction
import com.ilynehdev.core.database.LeafletDatabase
import com.ilynehdev.core.phloem.pagefetcher.Transactor

class RoomTransactor(private val db: LeafletDatabase) : Transactor {

    override suspend fun transaction(block: suspend () -> Unit) {
        db.withWriteTransaction { block() }
    }
}

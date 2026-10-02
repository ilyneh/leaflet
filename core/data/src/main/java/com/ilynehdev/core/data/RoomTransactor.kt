package com.ilynehdev.core.data

import androidx.room3.withWriteTransaction
import com.ilynehdev.core.database.LeafletDatabase
import com.ilynehdev.core.phloem.Transactor

internal class RoomTransactor(private val db: LeafletDatabase) : Transactor {

    override suspend fun transaction(block: suspend () -> Unit) {
        db.withWriteTransaction { block() }
    }
}

package com.ilynehdev.core.database

import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRATION_1_2 = object : Migration(1, 2) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.execSQL("ALTER TABLE plants ADD COLUMN details_synced_at INTEGER")
    }
}

// pruning_count changed from a JSON array to a single JSON object; stored
// arrays can't deserialize, so clear the cached values and let detail
// refreshes repopulate them.
val MIGRATION_2_3 = object : Migration(2, 3) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.execSQL("UPDATE plants SET pruning_count = NULL")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `saved_plants` (
                `plant_id` INTEGER NOT NULL,
                `saved_at` INTEGER NOT NULL,
                PRIMARY KEY(`plant_id`),
                FOREIGN KEY(`plant_id`) REFERENCES `plants`(`id`)
                    ON UPDATE NO ACTION ON DELETE NO ACTION
            )
            """.trimIndent()
        )
    }
}

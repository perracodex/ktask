/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.persistence.entity

import kotlinx.serialization.Serializable
import ktask.base.database.schema.base.TimestampedTable
import ktask.base.persistence.serializers.OffsetTimestamp
import org.jetbrains.exposed.sql.ResultRow

/**
 * Represents the metadata of a record.
 *
 * @property createdAt The timestamp when the record was created.
 * @property updatedAt The timestamp when the record was last updated.
 */
@Serializable
public data class Meta(
    val createdAt: OffsetTimestamp,
    val updatedAt: OffsetTimestamp
) {
    public companion object {
        /**
         * Maps a [ResultRow] to a [Meta] instance.
         *
         * @param row The [ResultRow] to map.
         * @param table The [TimestampedTable] from which the [ResultRow] was obtained.
         * @return The mapped [Meta] instance.
         */
        public fun from(row: ResultRow, table: TimestampedTable): Meta {
            return Meta(
                createdAt = row[table.createdAt],
                updatedAt = row[table.updatedAt]
            )
        }
    }
}

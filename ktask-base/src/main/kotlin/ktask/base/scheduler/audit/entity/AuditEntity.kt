/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.scheduler.audit.entity

import kotlinx.serialization.Serializable
import ktask.base.database.schema.SchedulerAuditTable
import ktask.base.persistence.serializers.UuidS
import ktask.base.scheduler.service.task.TaskOutcome
import ktask.base.utils.KLocalDateTime
import org.jetbrains.exposed.sql.ResultRow
import kotlin.uuid.toKotlinUuid

/**
 * Represents a scheduler audit log entity.
 *
 * @property id The unique identifier of the audit log.
 * @property taskName The name of the task.
 * @property taskGroup The group of the task.
 * @property fireTime The actual time the trigger fired.
 * @property runTime The amount of time the task ran for, in milliseconds.
 * @property outcome The log outcome status.
 * @property log The audit log information.
 * @property detail The detail that provides more information about the audit log.
 * @property createdAt The creation date of the audit log.
 */
@Serializable
data class AuditEntity(
    val id: UuidS,
    val taskName: String,
    val taskGroup: String,
    val fireTime: KLocalDateTime,
    val runTime: Long,
    val outcome: TaskOutcome,
    val log: String?,
    val detail: String?,
    val createdAt: KLocalDateTime,
) {
    companion object {
        /**
         * Maps a [ResultRow] to a [AuditEntity] instance.
         *
         * @param row The [ResultRow] to map.
         * @return The mapped [AuditEntity] instance.
         */
        fun from(row: ResultRow): AuditEntity {
            return AuditEntity(
                id = row[SchedulerAuditTable.id].toKotlinUuid(),
                taskName = row[SchedulerAuditTable.taskName],
                taskGroup = row[SchedulerAuditTable.taskGroup],
                fireTime = row[SchedulerAuditTable.fireTime],
                runTime = row[SchedulerAuditTable.runTime],
                outcome = row[SchedulerAuditTable.outcome],
                log = row[SchedulerAuditTable.log],
                detail = row[SchedulerAuditTable.detail],
                createdAt = row[SchedulerAuditTable.createdAt]
            )
        }
    }
}

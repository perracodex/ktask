/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.core.scheduler.model.audit

import kotlinx.datetime.LocalDateTime
import ktask.core.scheduler.service.annotation.SchedulerApi
import ktask.core.scheduler.service.task.TaskOutcome

/**
 * Represents a log request to be stored in the database.
 *
 * @property groupId The group of the task.
 * @property taskId The unique identifier of the task.
 * @property snowflakeId The unique snowflake ID to identify the cluster node that executed the task.
 * @property fireTime The actual time the trigger fired.
 * @property runTime The amount of time the job ran for, in milliseconds.
 * @property outcome The log [TaskOutcome] status.
 * @property log The log information.
 * @property detail The detail that provides more information about the log.
 */
@SchedulerApi
public data class AuditLogRequest(
    val groupId: String,
    val taskId: String,
    val snowflakeId: String,
    val fireTime: LocalDateTime,
    val runTime: Long,
    val outcome: TaskOutcome,
    val log: String?,
    val detail: String?,
)

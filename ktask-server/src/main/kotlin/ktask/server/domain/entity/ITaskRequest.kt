/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.entity

import kotlinx.serialization.Serializable
import ktask.base.persistence.serializers.SUUID
import ktask.base.utils.DateTimeUtils
import ktask.base.utils.KLocalDateTime
import ktask.server.domain.entity.ITaskRequest.Schedule
import ktask.server.domain.service.consumer.AbsTaskConsumer

/**
 * Represents a request to schedule a task.
 *
 * @property id The unique identifier of the task request.
 * @property schedule Optional [Schedule] for the task.
 * @property recipients List of target recipients.
 */
interface ITaskRequest {
    val id: SUUID
    val schedule: Schedule?
    val recipients: List<String>

    /**
     * Represents a schedule for a task.
     *
     * The cron expression is composed of the following fields:
     * ```
     * ┌───────────── second (0-59)
     * │ ┌───────────── minute (0-59)
     * │ │ ┌───────────── hour (0-23)
     * │ │ │ ┌───────────── day of month (1-31)
     * │ │ │ │ ┌───────────── month (1-12 or JAN-DEC)
     * │ │ │ │ │ ┌───────────── day of week (0-7, SUN-SAT. Both 0 & 7 = Sunday)
     * │ │ │ │ │ │ ┌───────────── year (optional)
     * │ │ │ │ │ │ │
     * │ │ │ │ │ │ │
     * * * * * * * *
     * ```
     *
     * ```
     * Sample cron expressions:
     *   - "0 0 0 * * ?" - At midnight every day.
     *   - "0 0 12 ? * MON-FRI" - At noon every weekday.
     *   - "0 0/30 9-17 * * ?" - Every 30 minutes between 9 AM to 5 PM.
     *   - "0 0 0 1 * ?" - At midnight on the first day of every month.
     *   - "0 0 6 ? * SUN" - At 6 AM every Sunday.
     *   - "0 0 14 * * ?" - At 2 PM every day.
     *   - "0 15 10 ? * *" - At 10:15 AM every day.
     *   - "0 0/15 * * * ?" - Every 15 minutes.
     *   - "0 0 0 ? * MON#1" - At midnight on the first Monday of every month.
     *   - "30 0 0 * * ?" - At 00:00:30 (30 seconds past midnight) every day.
     *   - "0 * * * * ?" - Every minute.
     * ```
     *
     * @property startAt Optional date/time when the task must start. Null to start immediately.
     * @property interval Optional [DateTimeUtils.Interval] repetition. Null to send only once or if cron is set.
     * @property cron Optional cron expression to schedule the task. Null if interval is set.
     */
    @Serializable
    data class Schedule(
        val startAt: KLocalDateTime? = null,
        val interval: DateTimeUtils.Interval? = null,
        val cron: String? = null
    ) {
        init {
            require(interval == null || cron.isNullOrBlank()) { "Either interval or cron can be set, but not both." }
        }
    }

    /**
     * Converts the notification request into a map of parameters suitable for task processing.
     * This base implementation converts only the common fields shared across all notification types.
     *
     * Subclasses should override this method to include additional type-specific parameters.
     */
    fun toTaskParameters(recipient: String): MutableMap<String, Any> {
        return mutableMapOf(
            AbsTaskConsumer.TASK_ID_KEY to id,
            AbsTaskConsumer.RECIPIENT_KEY to recipient,
        )
    }
}

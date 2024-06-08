/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ktask.base.env.Tracer
import ktask.base.scheduler.service.SchedulerRequest
import ktask.base.scheduler.service.TaskStartAt
import ktask.base.utils.DateTimeUtils
import ktask.base.utils.KLocalDateTime
import ktask.server.domain.entity.ITaskRequest
import ktask.server.domain.entity.notification.EmailTaskRequest
import ktask.server.domain.entity.notification.SlackTaskRequest
import ktask.server.domain.service.consumer.AbsTaskConsumer
import ktask.server.domain.service.consumer.notifications.EmailTaskConsumer
import ktask.server.domain.service.consumer.notifications.SlackTaskConsumer

/**
 * Notification service for managing scheduling related operations.
 */
internal object NotificationService {
    private val tracer = Tracer<NotificationService>()

    /**
     * Submits a new notification request to the task scheduler.
     *
     * It ensures that tasks are scheduled promptly even if the specified time is in the past,
     * leveraging the Task Scheduler Service's ability to handle such cases gracefully.
     *
     * For notifications with multiple recipients, this function schedules a separate task
     * for each recipient, ensuring that all recipients receive their notifications.
     *
     * @param request The [ITaskRequest] instance representing the notification to be scheduled.
     * @return The ID of the scheduled notification if successful.
     * @throws IllegalArgumentException if the notification request type is unsupported.
     */
    suspend fun schedule(request: ITaskRequest): Unit = withContext(Dispatchers.IO) {
        tracer.debug("Scheduling new notification for ID: ${request.id}")

        // Identify the target consumer class.
        val taskClass: Class<out AbsTaskConsumer> = when (request) {
            is EmailTaskRequest -> {
                EmailTaskRequest.verifyRecipients(request = request)
                EmailTaskConsumer::class.java
            }

            is SlackTaskRequest -> SlackTaskConsumer::class.java
            else -> throw IllegalArgumentException("Unsupported notification request: $request")
        }

        // Determine the start date/time for the task.
        // Note: If the scheduled time is in the past, the Task Scheduler Service
        // will automatically start the task as soon as it becomes possible.
        val taskStartAt: TaskStartAt = request.schedule?.let { schedule ->
            val startDateTime: KLocalDateTime = schedule.startAt ?: DateTimeUtils.currentUTCDateTime()
            TaskStartAt.AtDateTime(datetime = startDateTime)
        } ?: TaskStartAt.Immediate

        // Iterate over each recipient and schedule a task for each one.
        request.recipients.forEach { recipient ->
            // Configure the task parameters specific to the current recipient.
            val taskParameters: MutableMap<String, Any> = request.toTaskParameters(recipient = recipient)

            // Prepare the scheduling request.
            val scheduleRequest = SchedulerRequest(
                taskId = request.id,
                taskClass = taskClass,
                startAt = taskStartAt,
                parameters = taskParameters
            )

            // Send the scheduling request.
            val interval: DateTimeUtils.Interval? = request.schedule?.interval
            val cron: String? = request.schedule?.cron
            val taskKey = if (interval != null) {
                scheduleRequest.send(interval = interval)
            } else if (cron != null) {
                scheduleRequest.send(cron = cron)
            } else {
                scheduleRequest.send()
            }

            tracer.debug("Scheduled ${taskClass.name} for recipient: $recipient. Task key: $taskKey")
        }
    }
}

/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ktask.base.env.Tracer
import ktask.base.events.SEEService
import ktask.base.scheduler.service.schedule.TaskStartAt
import ktask.base.scheduler.service.task.TaskDispatch
import ktask.base.utils.DateTimeUtils
import ktask.base.utils.KLocalDateTime
import ktask.server.consumer.notification.AbsNotificationConsumer
import ktask.server.consumer.notification.task.EmailConsumer
import ktask.server.consumer.notification.task.SlackConsumer
import ktask.server.entity.notification.INotificationRequest
import ktask.server.entity.notification.request.EmailRequest
import ktask.server.entity.notification.request.SlackRequest

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
     * @param request The [INotificationRequest] instance representing the notification to be scheduled.
     * @return The ID of the scheduled notification if successful.
     * @throws IllegalArgumentException if the notification request type is unsupported.
     */
    suspend fun schedule(request: INotificationRequest): Unit = withContext(Dispatchers.IO) {
        tracer.debug("Scheduling new notification for ID: ${request.id}")

        // Identify the target consumer class.
        val consumerClass: Class<out AbsNotificationConsumer> = when (request) {
            is EmailRequest -> {
                EmailRequest.verifyRecipients(request = request)
                EmailConsumer::class.java
            }

            is SlackRequest -> SlackConsumer::class.java
            else -> throw IllegalArgumentException("Unsupported notification request: $request")
        }

        // Determine the start date/time for the task.
        // Note: If the scheduled time is in the past, the Task Scheduler Service
        // will automatically start the task as soon as it becomes possible.
        val taskStartAt: TaskStartAt = request.schedule?.let { schedule ->
            val startDateTime: KLocalDateTime = schedule.start ?: DateTimeUtils.currentUTCDateTime()
            TaskStartAt.AtDateTime(datetime = startDateTime)
        } ?: TaskStartAt.Immediate

        // Iterate over each recipient and schedule a task for each one.
        request.recipients.forEach { recipient ->

            // Dispatch the task based on the specified schedule type.
            request.toMap(recipient = recipient).let { parameters ->
                TaskDispatch(
                    taskId = request.id,
                    consumerClass = consumerClass,
                    startAt = taskStartAt,
                    parameters = parameters
                ).run {
                    request.schedule?.let { schedule ->
                        send(schedule = schedule)
                    } ?: send()
                }.also { taskKey ->
                    tracer.debug("Scheduled ${consumerClass.name} for recipient: $recipient. Task key: $taskKey")
                }
            }
        }

        // Send a message to the SSE endpoint.
        val schedule: String = request.schedule?.toString() ?: "--"
        SEEService.push(
            "New notification task (${request.recipients.size})| $schedule | ${consumerClass.simpleName} | ID: ${request.id}"
        )
    }
}

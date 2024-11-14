/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import ktask.core.env.Tracer
import ktask.core.event.SseService
import ktask.core.scheduler.service.schedule.TaskStartAt
import ktask.core.scheduler.service.task.TaskDispatch
import ktask.core.scheduler.service.task.TaskKey
import ktask.core.util.DateTimeUtils.current
import ktask.notification.consumer.message.AbsNotificationConsumer
import ktask.notification.consumer.message.task.EmailConsumer
import ktask.notification.consumer.message.task.SlackConsumer
import ktask.notification.model.message.IMessageRequest
import ktask.notification.model.message.request.EmailRequest
import ktask.notification.model.message.request.SlackRequest

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
     * @param request The [IMessageRequest] instance representing the notification to be scheduled.
     * @return A [TaskKey] list of the scheduled tasks.
     * @throws IllegalArgumentException if the notification request type is unsupported.
     */
    suspend fun schedule(request: IMessageRequest): List<TaskKey> = withContext(Dispatchers.IO) {
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
            val startDateTime: LocalDateTime = schedule.start ?: LocalDateTime.current()
            TaskStartAt.AtDateTime(datetime = startDateTime)
        } ?: TaskStartAt.Immediate

        val outputKeys: MutableList<TaskKey> = mutableListOf()

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
                    outputKeys.add(taskKey)
                }
            }
        }

        // Send a message to the SSE endpoint.
        val schedule: String = request.schedule?.toString() ?: "--"
        SseService.push(
            message = "New notification task (${request.recipients.size})| $schedule | ${consumerClass.simpleName} | ID: ${request.id}"
        )

        return@withContext outputKeys
    }
}

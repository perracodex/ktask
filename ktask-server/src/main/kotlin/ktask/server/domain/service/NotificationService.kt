/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ktask.base.env.Tracer
import ktask.base.errors.SystemError
import ktask.base.persistence.validators.IValidator
import ktask.base.persistence.validators.impl.EmailValidator
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

        // Identify the appropriate consumer class based on the notification type.
        val taskClass: Class<out AbsTaskConsumer> = when (request) {
            is EmailTaskRequest -> EmailTaskConsumer::class.java
            is SlackTaskRequest -> SlackTaskConsumer::class.java
            else -> throw IllegalArgumentException("Unsupported notification request: $request")
        }

        verifyRecipients(request = request)

        // Determine the start date/time for the task.
        // Note: If the scheduled time is in the past, the Task Scheduler Service
        // will automatically start the task as soon as it becomes possible,
        // It is configured to handle past schedules gracefully.
        val taskStartAt: TaskStartAt = request.schedule?.let {
            val scheduledDateTime: KLocalDateTime = request.schedule ?: DateTimeUtils.currentUTCDateTime()
            TaskStartAt.AtDateTime(datetime = scheduledDateTime)
        } ?: TaskStartAt.Immediate

        // Iterate over each recipient and schedule a task for each of them.
        request.recipients.forEach { recipient ->
            // Configure the task parameters specific to the current recipient.
            val taskParameters: MutableMap<String, Any> = request.toTaskParameters(recipient = recipient)

            // Send the scheduling request for the task.
            SchedulerRequest.send(taskId = request.id, taskClass = taskClass) {
                startAt = taskStartAt
                parameters = taskParameters
            }.also { taskKey ->
                tracer.debug("Scheduled notification of type ${taskClass.name}. Task key: $taskKey")
            }
        }
    }

    /**
     * Verifies the recipients of the notification request.
     *
     * @param request The [ITaskRequest] instance to be verified.
     */
    private fun verifyRecipients(request: ITaskRequest) {
        if (request is EmailTaskRequest) {
            // Validate the email addresses of the recipients before proceeding.
            // This verification must be done at this point and not in the actual
            // request dataclass init block, so the error is handled by the route
            // scope allowing the to return a custom error response.
            // If done in the init block, the error would be a generic 400 Bad Request.
            request.recipients.forEach {
                val result: IValidator.Result = EmailValidator.validate(value = it)
                if (result is IValidator.Result.Failure) {
                    SystemError.InvalidEmailFormat(id = request.id, email = it).raise()
                }
            }
        }
    }
}

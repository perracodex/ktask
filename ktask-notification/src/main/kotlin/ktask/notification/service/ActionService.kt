/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import ktask.core.env.Tracer
import ktask.core.events.SEEService
import ktask.core.scheduler.service.schedule.TaskStartAt
import ktask.core.scheduler.service.task.TaskDispatch
import ktask.core.scheduler.service.task.TaskKey
import ktask.core.utils.DateTimeUtils.current
import ktask.notification.consumer.action.AbsActionConsumer
import ktask.notification.consumer.action.task.ActionConsumer
import ktask.notification.model.action.IActionRequest

/**
 * Custom action service for managing scheduling related operations.
 */
internal object ActionService {
    private val tracer = Tracer<ActionService>()

    /**
     * Submits a new custom action request to the task scheduler.
     *
     * It ensures that tasks are scheduled promptly even if the specified time is in the past,
     * leveraging the Task Scheduler Service's ability to handle such cases gracefully.
     *
     * @param request The [IActionRequest] instance representing the action  to be scheduled.
     * @return The [TaskKey] of the scheduled task.
     * @throws IllegalArgumentException if the action request type is unsupported.
     */
    suspend fun schedule(request: IActionRequest): TaskKey = withContext(Dispatchers.IO) {
        tracer.debug("Scheduling new custom action for ID: ${request.id}")

        // Resolve the target consumer class.
        val consumerClass: Class<out AbsActionConsumer> = ActionConsumer::class.java

        // Determine the start date/time for the task.
        // Note: If the scheduled time is in the past, the Task Scheduler Service
        // will automatically start the task as soon as it becomes possible.
        val taskStartAt: TaskStartAt = request.schedule?.let { schedule ->
            val startDateTime: LocalDateTime = schedule.start ?: LocalDateTime.current()
            TaskStartAt.AtDateTime(datetime = startDateTime)
        } ?: TaskStartAt.Immediate

        lateinit var outputKey: TaskKey

        // Dispatch the task based on the specified schedule type.
        request.toMap().let { parameters ->
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
                tracer.debug("Scheduled ${consumerClass.name}. Task key: $taskKey")
                outputKey = taskKey
            }
        }

        // Send a message to the SSE endpoint.
        val schedule: String = request.schedule?.toString() ?: "--"
        SEEService.push("New action task | $schedule | ID: ${request.id}")

        return@withContext outputKey
    }
}

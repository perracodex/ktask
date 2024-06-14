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
import ktask.server.consumer.action.AbsActionConsumer
import ktask.server.consumer.action.task.ActionConsumer
import ktask.server.entity.action.IActionRequest

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
     * @return The ID of the scheduled action if successful.
     * @throws IllegalArgumentException if the action request type is unsupported.
     */
    suspend fun schedule(request: IActionRequest): Unit = withContext(Dispatchers.IO) {
        tracer.debug("Scheduling new custom action for ID: ${request.id}")

        // Resolve the target consumer class.
        val taskConsumerClass: Class<out AbsActionConsumer> = ActionConsumer::class.java

        // Determine the start date/time for the task.
        // Note: If the scheduled time is in the past, the Task Scheduler Service
        // will automatically start the task as soon as it becomes possible.
        val taskStartAt: TaskStartAt = request.schedule?.let { schedule ->
            val startDateTime: KLocalDateTime = schedule.start ?: DateTimeUtils.currentUTCDateTime()
            TaskStartAt.AtDateTime(datetime = startDateTime)
        } ?: TaskStartAt.Immediate

        // Prepare the task parameters.
        val taskParameters: MutableMap<String, Any> = request.toTaskParameters()

        // Prepare the task dispatch object.
        val taskDispatch = TaskDispatch(
            taskId = request.id,
            taskConsumerClass = taskConsumerClass,
            startAt = taskStartAt,
            parameters = taskParameters
        )

        // Dispatch the task based on the specified schedule type.
        val taskKey = request.schedule?.let {
            taskDispatch.send(schedule = it)
        } ?: taskDispatch.send()

        tracer.debug("Scheduled ${taskConsumerClass.name}. Task key: $taskKey")

        // Send a message to the SSE endpoint.
        val schedule: String = request.schedule?.toString() ?: "--"
        SEEService.push("New action task | $schedule | ID: ${request.id}")
    }
}

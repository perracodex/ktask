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
import ktask.core.snowflake.SnowflakeFactory
import ktask.core.util.DateTimeUtils.current
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
        tracer.debug("Scheduling new custom action for ID: ${request.groupId}")

        // Normalize the group ID.
        val groupId: String = request.groupId.trim().lowercase()
        if (groupId.isEmpty()) {
            throw IllegalArgumentException("Group ID cannot be empty.")
        }

        // Check if the group already exists.
        if (TaskDispatch.groupExists(groupId = groupId)) {
            if (!request.replace) {
                val message = "Group already exists. Skipping Re-schedule. Group ID: $groupId"
                tracer.warning(message)
                SseService.push(message = message)
                return@withContext TaskDispatch.groupsTaskKeys(groupId = groupId).first()
            } else {
                val message = "Group already exists. Will be replaced, recreating all tasks. Group ID: $groupId"
                tracer.warning(message)
                SseService.push(message = message)
                TaskDispatch.deleteGroup(groupId = groupId)
            }
        }

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

        val taskId: String = SnowflakeFactory.nextId()

        // Dispatch the task based on the specified schedule type.
        request.toMap(taskId = taskId).let { parameters ->
            TaskDispatch(
                groupId = groupId,
                taskId = taskId,
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
        val schedule: String = request.schedule?.toString() ?: "Immediate"
        SseService.push(message = "New 'action' task | $schedule | Group Id: $groupId | Task Id: $taskId")

        return@withContext outputKey
    }
}

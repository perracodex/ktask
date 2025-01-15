/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package taskmanager.notification.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import taskmanager.base.env.Tracer
import taskmanager.base.event.SseService
import taskmanager.base.snowflake.SnowflakeFactory
import taskmanager.base.util.DateTimeUtils.current
import taskmanager.notification.consumer.action.AbsActionConsumer
import taskmanager.notification.consumer.action.task.ActionConsumer
import taskmanager.notification.model.action.IActionRequest
import taskmanager.scheduler.scheduling.TaskDispatcher
import taskmanager.scheduler.scheduling.TaskStartAt
import taskmanager.scheduler.task.TaskKey

/**
 * Custom action service for managing scheduling related operations.
 */
internal object ActionService {
    private val tracer: Tracer = Tracer<ActionService>()

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

        // Check if the group already exists.
        if (TaskDispatcher.groupExists(groupId = request.groupId)) {
            if (!request.replace) {
                SseService.push(message = "Group already exists. Skipping Re-schedule. Group ID: ${request.groupId}")
                return@withContext TaskDispatcher.groupsTaskKeys(groupId = request.groupId).first()
            } else {
                SseService.push(message = "Group already exists. Will be replaced, recreating all tasks. Group ID: ${request.groupId}")
                TaskDispatcher.deleteGroup(groupId = request.groupId)
            }
        }

        // Resolve the target consumer class.
        val consumerClass: Class<out AbsActionConsumer> = ActionConsumer::class.java

        // Determine the start date/time for the task.
        // Note: If the scheduled time is in the past, the Task Scheduler Service
        // will automatically start the task as soon as it becomes possible.
        val taskStartAt: TaskStartAt = request.scheduleType?.let { schedule ->
            val startDateTime: LocalDateTime = schedule.start ?: LocalDateTime.current()
            TaskStartAt.AtDateTime(datetime = startDateTime)
        } ?: TaskStartAt.Immediate

        val taskId: String = SnowflakeFactory.nextId()

        // Dispatch the task based on the specified schedule type.
        val outputKey: TaskKey = request.toMap(taskId = taskId).let { parameters ->
            TaskDispatcher(
                groupId = request.groupId,
                taskId = taskId,
                consumerClass = consumerClass,
                startAt = taskStartAt,
                parameters = parameters
            ).run {
                request.scheduleType?.let { schedule ->
                    send(scheduleType = schedule)
                } ?: send()
            }.also { taskKey ->
                tracer.debug("Scheduled ${consumerClass.name}. Task key: $taskKey")
                taskKey
            }
        }

        // Send a message to the SSE endpoint.
        val schedule: String = request.scheduleType?.toString() ?: "Immediate"
        SseService.push(message = "New 'action' task | $schedule | Group Id: ${request.groupId} | Task Id: $taskId")

        return@withContext outputKey
    }
}

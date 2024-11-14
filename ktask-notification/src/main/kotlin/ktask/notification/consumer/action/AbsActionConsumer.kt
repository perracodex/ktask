/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.consumer.action

import kotlinx.datetime.LocalDateTime
import ktask.core.event.SseService
import ktask.core.scheduler.service.task.TaskConsumer
import ktask.core.util.DateTimeUtils.current
import ktask.core.util.DateTimeUtils.formatted
import kotlin.uuid.Uuid

/**
 * Abstract base class for processing scheduled tasks, providing common steps for task execution,
 * handling the extraction, pre-processing, and consumption of task-related data, including
 * the loading and processing of template files. Extending classes must implement the [consume]
 * method to define task-specific behavior.
 */
internal abstract class AbsActionConsumer : TaskConsumer() {

    /**
     * Represents the properties used in the task payload.
     * These are the common properties shared by all task consumers.
     */
    enum class Property(val key: String) {
        TASK_ID(key = "TASK_ID"),
        DESCRIPTION(key = "DESCRIPTION")
    }

    /**
     * Represents the data necessary for task processing, encapsulating task-specific parameters.
     *
     * @property taskId The unique identifier of the task.
     * @property additionalParameters A map of additional parameters required for the task.
     */
    data class TaskPayload(
        val taskId: Uuid,
        val additionalParameters: Map<String, Any> = emptyMap()
    ) {
        companion object {
            fun build(properties: Map<String, Any>): TaskPayload {
                return properties.filterKeys { key ->
                    // Consumer-specific properties, which are not part of the common payload.
                    key !in Property.entries.map { it.key }
                }.let { additionalParameters ->
                    TaskPayload(
                        taskId = properties[Property.TASK_ID.key] as Uuid,
                        additionalParameters = additionalParameters
                    )
                }
            }
        }
    }

    override fun start(properties: Map<String, Any>) {
        val payload: TaskPayload = properties.let { TaskPayload.build(properties = it) }

        runCatching {
            consume(payload = payload)
        }.onFailure {
            SseService.push(
                message = "${LocalDateTime.current().formatted()} | Failed to consume `action` task: `${payload.taskId}`" +
                        " | Error: ${it.message.orEmpty()}"
            )

            // Rethrow the exception to allow it to propagate.
            throw it
        }.onSuccess {
            SseService.push(
                message = "${LocalDateTime.current().formatted()} | Consumed `action` task: `${payload.taskId}`"
            )
        }
    }

    /**
     * Processes the task with the provided payload.
     * Extending classes must implement this method to define
     * the task-specific consumption behavior.
     *
     * @param payload The [TaskPayload] containing the data required to process the task.
     */
    protected abstract fun consume(payload: TaskPayload)
}

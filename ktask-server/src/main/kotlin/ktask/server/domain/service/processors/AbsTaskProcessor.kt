/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.service.processors

import ktask.base.env.Tracer
import ktask.base.persistence.serializers.SUUID
import ktask.base.scheduler.service.SchedulerTask

/**
 * Abstract base class for processing scheduled tasks, implying the handling of task execution
 * by abstracting common task-related data extraction and pre-processing tasks.
 */
internal abstract class AbsTaskProcessor : SchedulerTask() {
    private val tracer = Tracer<AbsTaskProcessor>()

    /**
     * Represents the data necessary for task processing, encapsulating task-specific parameters.
     *
     * @param taskId The unique identifier of the task.
     * @param recipient The intended recipient of the notification, such as an email address.
     * @param message The content of the notification to be sent.
     * @param additionalParams A map of additional parameters required for the task.
     */
    data class TaskPayload(
        val taskId: SUUID,
        val recipient: String,
        val message: String,
        val additionalParams: Map<String, Any> = emptyMap()
    ) {
        companion object {
            fun map(properties: Map<String, Any>): TaskPayload {
                return TaskPayload(
                    taskId = properties[TASK_ID_KEY] as SUUID,
                    recipient = properties[RECIPIENT_KEY] as String,
                    message = properties[MESSAGE_KEY] as String,
                    additionalParams = properties.filterKeys { key ->
                        key !in setOf(TASK_ID_KEY, RECIPIENT_KEY, MESSAGE_KEY)
                    }
                )
            }
        }
    }

    override fun start(properties: Map<String, Any>) {
        runCatching {
            val payload: TaskPayload = properties.let { TaskPayload.map(properties = it) }
            runTask(payload = payload)
        }.onFailure { error ->
            tracer.error("Failed to process task: $error")
        }
    }

    /**
     * Processes the task with the provided data.
     *
     * @param payload The [TaskPayload] containing the data required to process the task.
     */
    protected abstract fun runTask(payload: TaskPayload)

    companion object {
        const val TASK_ID_KEY: String = "TASK_ID_KEY"
        const val RECIPIENT_KEY: String = "RECIPIENT"
        const val MESSAGE_KEY: String = "MESSAGE"
    }
}

/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package taskmanager.notification.consumer.action

import taskmanager.scheduler.task.TaskConsumer
import kotlin.uuid.Uuid

/**
 * Abstract base class for processing scheduled tasks, providing common steps for task execution,
 * handling the extraction, pre-processing, and consumption of task-related data, including
 * the loading and processing of template files. Extending classes must implement the [consume]
 * method to define task-specific behavior.
 */
internal abstract class AbsActionConsumer : TaskConsumer<AbsActionConsumer.ConsumerPayload>() {

    /**
     * Represents the properties used in the task payload.
     * These are the common properties shared by all task consumers.
     */
    enum class Property(val key: String) {
        GROUP_ID(key = "GROUP_ID"),
        TASK_ID(key = "TASK_ID"),
        DESCRIPTION(key = "DESCRIPTION")
    }

    override fun buildPayload(properties: Map<String, Any>): ConsumerPayload {
        return ConsumerPayload.build(properties = properties)
    }

    /**
     * Represents the data necessary for task processing, encapsulating task-specific parameters.
     *
     * @property additionalParameters A map of additional parameters required for the task.
     */
    data class ConsumerPayload(
        override val groupId: Uuid,
        override val taskId: String,
        override val taskType: String = TASK_TYPE,
        val additionalParameters: Map<String, Any> = emptyMap()
    ) : Payload {
        companion object {
            fun build(properties: Map<String, Any>): ConsumerPayload {
                val groupId: Uuid = properties[Property.GROUP_ID.key] as? Uuid
                    ?: throw IllegalArgumentException("GROUP_ID is missing or invalid.")
                val taskId: String = properties[Property.TASK_ID.key] as? String
                    ?: throw IllegalArgumentException("TASK_ID is missing or invalid.")

                return properties.filterKeys { key ->
                    // Consumer-specific properties, which are not part of the common payload.
                    key !in Property.entries.map { it.key }
                }.let { additionalParameters ->
                    ConsumerPayload(
                        groupId = groupId,
                        taskId = taskId,
                        additionalParameters = additionalParameters
                    )
                }
            }
        }
    }

    private companion object {
        const val TASK_TYPE: String = "action"
    }
}

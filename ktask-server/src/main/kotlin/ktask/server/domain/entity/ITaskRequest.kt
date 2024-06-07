/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.entity

import ktask.base.persistence.serializers.SUUID
import ktask.base.utils.KLocalDateTime
import ktask.server.domain.service.consumer.AbsTaskConsumer

/**
 * Represents a request to schedule a task.
 *
 * @property id The unique identifier of the task request.
 * @property schedule Optional date/time when the task must be sent. Null to send immediately.
 * @property interval Optional interval in seconds at which the task should repeat. In minutes.
 * @property recipients List of target recipients.
 */
interface ITaskRequest {
    val id: SUUID
    val schedule: KLocalDateTime?
    val interval: UInt?
    val recipients: List<String>

    /**
     * Converts the notification request into a map of parameters suitable for task processing.
     * This base implementation converts only the common fields shared across all notification types.
     *
     * Subclasses should override this method to include additional type-specific parameters.
     */
    fun toTaskParameters(recipient: String): MutableMap<String, Any> {
        return mutableMapOf(
            AbsTaskConsumer.TASK_ID_KEY to id,
            AbsTaskConsumer.RECIPIENT_KEY to recipient,
        )
    }
}

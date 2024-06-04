/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.entity

import ktask.base.persistence.serializers.SUUID
import ktask.base.utils.KLocalDateTime
import ktask.server.domain.service.processors.AbsTaskProcessor

/**
 * Represents a request to send a notification.
 *
 * @property id The unique identifier of the notification request.
 * @property schedule Optional date/time when the notification is scheduled to be sent. Null to send immediately.
 * @property recipients List of recipients of the notification.
 * @property message The content or information to be included in the notification.
 */
interface INotificationRequest {
    val id: SUUID
    val schedule: KLocalDateTime?
    val recipients: List<String>
    val message: String

    /**
     * Converts the notification request into a map of parameters suitable for task processing.
     * This base implementation converts only the common fields shared across all notification types.
     *
     * Subclasses should override this method to include additional type-specific parameters.
     */
    fun toTaskParameters(recipient: String): MutableMap<String, Any> {
        return mutableMapOf(
            AbsTaskProcessor.TASK_ID_KEY to id,
            AbsTaskProcessor.RECIPIENT_KEY to recipient,
            AbsTaskProcessor.MESSAGE_KEY to message
        )
    }
}

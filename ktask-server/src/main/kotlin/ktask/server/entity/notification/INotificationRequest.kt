/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.entity.notification

import ktask.base.persistence.serializers.SUUID
import ktask.base.scheduler.service.schedule.Schedule
import ktask.server.consumer.notification.AbsNotificationConsumer

/**
 * Represents a request to schedule a task.
 *
 * @property id The unique identifier of the task request.
 * @property schedule Optional [Schedule] for the task.
 * @property recipients List of target recipients.
 * @property template The template to be used for the notification.
 * @property fields Optional fields to be included in the template.
 * @property attachments Optional list of file paths to be attached to the notification.
 */
interface INotificationRequest {
    val id: SUUID
    val schedule: Schedule?
    val recipients: List<Recipient>
    val template: String
    val fields: Map<String, String>?
    val attachments: List<String>?

    /**
     * Converts the notification request into a map of parameters suitable for task processing.
     * This base implementation converts only the common fields shared across all notification types.
     *
     * Subclasses should override this method to include additional type-specific parameters.
     */
    fun toTaskParameters(recipient: Recipient): MutableMap<String, Any> {
        return mutableMapOf(
            AbsNotificationConsumer.Property.TASK_ID.key to id,
            AbsNotificationConsumer.Property.RECIPIENT_TARGET.key to recipient.target,
            AbsNotificationConsumer.Property.RECIPIENT_NAME.key to recipient.name,
            AbsNotificationConsumer.Property.RECIPIENT_LOCALE.key to recipient.locale,
            AbsNotificationConsumer.Property.TEMPLATE.key to template,
            AbsNotificationConsumer.Property.FIELDS.key to (fields ?: emptyMap()),
            AbsNotificationConsumer.Property.ATTACHMENTS.key to (attachments ?: emptyList())
        )
    }
}

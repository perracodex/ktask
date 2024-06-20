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
 * @property description Optional description of the task.
 * @property schedule Optional [Schedule] for the task.
 * @property recipients List of target recipients.
 * @property template The template to be used for the notification.
 * @property fields Optional fields to be included in the template.
 */
interface INotificationRequest {
    val id: SUUID
    val description: String?
    val schedule: Schedule?
    val recipients: List<Recipient>
    val template: String
    val fields: Map<String, String>?

    fun verify() {
        require(recipients.isNotEmpty()) { "Recipients must not be empty." }
        require(template.isNotBlank()) { "Template must not be blank." }
    }

    /**
     * Converts the notification request fields into a map of parameters suitable
     * for task serialization and consumption. This is required as the scheduler
     * requires a map of parameters.
     */
    fun toMap(recipient: Recipient): MutableMap<String, Any?> {
        return mutableMapOf(
            AbsNotificationConsumer.Property.TASK_ID.key to id,
            AbsNotificationConsumer.Property.DESCRIPTION.key to description,
            AbsNotificationConsumer.Property.RECIPIENT_TARGET.key to recipient.target,
            AbsNotificationConsumer.Property.RECIPIENT_NAME.key to recipient.name,
            AbsNotificationConsumer.Property.RECIPIENT_LOCALE.key to recipient.locale,
            AbsNotificationConsumer.Property.TEMPLATE.key to template,
            AbsNotificationConsumer.Property.FIELDS.key to fields,
        )
    }
}

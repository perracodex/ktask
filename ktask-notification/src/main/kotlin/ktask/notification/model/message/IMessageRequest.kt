/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.model.message

import ktask.core.persistence.serializer.Uuid
import ktask.core.scheduler.service.schedule.Schedule
import ktask.notification.consumer.message.AbsNotificationConsumer

/**
 * Base interface for all message based requests
 *
 * @property id The unique identifier of the task request.
 * @property description Optional description of the task.
 * @property schedule Optional [Schedule] for the task.
 * @property recipients List [Recipient] targets.
 * @property template The template to be used for the notification.
 * @property fields Optional fields to be included in the template.
 */
public interface IMessageRequest {
    public val id: Uuid
    public val description: String?
    public val schedule: Schedule?
    public val recipients: List<Recipient>
    public val template: String
    public val fields: Map<String, String>?

    /**
     * Verifies the integrity of the task request.
     */
    public fun verify() {
        require(recipients.isNotEmpty()) { "Recipients must not be empty." }
        require(template.isNotBlank()) { "Template must not be blank." }
    }

    /**
     * Converts the notification request fields into a map of parameters suitable
     * for task serialization and consumption. This is required as the scheduler
     * requires a map of parameters.
     */
    public fun toMap(taskId: String, recipient: Recipient): MutableMap<String, Any?> {
        return mutableMapOf(
            AbsNotificationConsumer.Property.GROUP_ID.key to id,
            AbsNotificationConsumer.Property.TASK_ID.key to taskId,
            AbsNotificationConsumer.Property.DESCRIPTION.key to description,
            AbsNotificationConsumer.Property.RECIPIENT_TARGET.key to recipient.target,
            AbsNotificationConsumer.Property.RECIPIENT_NAME.key to recipient.name,
            AbsNotificationConsumer.Property.RECIPIENT_LOCALE.key to recipient.locale,
            AbsNotificationConsumer.Property.TEMPLATE.key to template,
            AbsNotificationConsumer.Property.FIELDS.key to fields,
        )
    }
}

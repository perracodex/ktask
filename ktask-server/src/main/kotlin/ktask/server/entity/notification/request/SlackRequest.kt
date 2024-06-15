/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.entity.notification.request

import kotlinx.serialization.Serializable
import ktask.base.persistence.serializers.SUUID
import ktask.base.scheduler.service.schedule.Schedule
import ktask.server.consumer.notification.task.SlackConsumer
import ktask.server.entity.notification.INotificationRequest
import ktask.server.entity.notification.Recipient

/**
 * Represents a request to send a Slack notification task.
 *
 * @property id The unique identifier of the task request.
 * @property schedule Optional [Schedule] for the task.
 * @property recipients List of target recipients.
 * @property template The template to be used for the notification.
 * @property fields Optional fields to be included in the template.
 * @property attachments Optional list of file paths to be attached to the notification.
 * @property channel The channel to send the notification to.
 */
@Serializable
data class SlackRequest(
    override val id: SUUID,
    override val schedule: Schedule? = null,
    override val recipients: List<Recipient>,
    override val template: String,
    override val fields: Map<String, String>? = null,
    override val attachments: List<String>? = null,
    val channel: String,
) : INotificationRequest {

    init {
        verify()
    }

    override fun toTaskParameters(recipient: Recipient): MutableMap<String, Any> {
        return super.toTaskParameters(recipient = recipient).also { parameter ->
            parameter[SlackConsumer.Property.CHANNEL.key] = channel
        }
    }
}

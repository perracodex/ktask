/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.entity.message.request

import kotlinx.serialization.Serializable
import ktask.base.persistence.serializers.UuidS
import ktask.base.scheduler.service.schedule.Schedule
import ktask.notification.consumer.message.task.SlackConsumer
import ktask.notification.entity.message.IMessageRequest
import ktask.notification.entity.message.Recipient

/**
 * Represents a request to send a Slack notification task.
 *
 * @property id The unique identifier of the task request.
 * @property description Optional description of the task.
 * @property schedule Optional [Schedule] for the task.
 * @property recipients List of target recipients.
 * @property template The template to be used for the notification.
 * @property fields Optional fields to be included in the template.
 * @property channel The channel to send the notification to.
 */
@Serializable
data class SlackRequest(
    override val id: UuidS,
    override val description: String? = null,
    override val schedule: Schedule? = null,
    override val recipients: List<Recipient>,
    override val template: String,
    override val fields: Map<String, String>? = null,
    val channel: String,
) : IMessageRequest {

    init {
        verify()
    }

    override fun toMap(recipient: Recipient): MutableMap<String, Any?> {
        return super.toMap(recipient = recipient).apply {
            this[SlackConsumer.Property.CHANNEL.key] = channel
        }
    }
}

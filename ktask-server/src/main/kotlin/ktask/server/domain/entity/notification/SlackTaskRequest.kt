/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.entity.notification

import kotlinx.serialization.Serializable
import ktask.base.persistence.serializers.SUUID
import ktask.server.domain.entity.ITaskRequest
import ktask.server.domain.service.consumer.notifications.SlackTaskConsumer

/**
 * Represents a request to send a Slack notification task.
 *
 * @property id The unique identifier of the task request.
 * @property schedule Optional [ITaskRequest.Schedule] for the task.
 * @property recipients List of target recipients.
 * @property channel The Slack channel to send the notification to.
 * @property message The message or information contained in the notification.
 */
@Serializable
data class SlackTaskRequest(
    override val id: SUUID,
    override val schedule: ITaskRequest.Schedule? = null,
    override val recipients: List<String>,
    val channel: String,
    val message: String
) : ITaskRequest {
    init {
        require(channel.isNotBlank()) { "Channel cannot be blank." }
        require(message.isNotBlank()) { "Message cannot be blank." }
        require(recipients.isNotEmpty()) { "At least one recipient must be specified." }
        recipients.forEach { recipient ->
            require(recipient.isNotBlank()) { "Recipient cannot be blank." }
        }
    }

    override fun toTaskParameters(recipient: String): MutableMap<String, Any> {
        return super.toTaskParameters(recipient).also {
            it[SlackTaskConsumer.CHANNEL_KEY] = channel
            it[SlackTaskConsumer.MESSAGE_KEY] = message
        }
    }
}

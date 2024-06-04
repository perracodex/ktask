/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.entity

import kotlinx.serialization.Serializable
import ktask.base.persistence.serializers.SUUID
import ktask.base.utils.KLocalDateTime
import ktask.server.domain.service.processors.SlackTaskProcessor

/**
 * Represents a request to send a Slack notification.
 *
 * @property id The unique identifier of the notification request.
 * @property schedule Optional date/time when the notification is scheduled to be sent. Null to send immediately.
 * @property recipients The Slack recipients of the notification.
 * @property message The message or information contained in the notification.
 * @property channel The Slack channel to send the notification to.
 */
@Serializable
data class SlackNotificationRequest(
    override val id: SUUID,
    override val schedule: KLocalDateTime? = null,
    override val recipients: List<String>,
    override val message: String,
    val channel: String,
) : INotificationRequest {
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
            it[SlackTaskProcessor.CHANNEL_KEY] = channel
        }
    }
}

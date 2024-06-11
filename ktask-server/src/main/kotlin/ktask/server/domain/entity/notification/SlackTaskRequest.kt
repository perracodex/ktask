/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.entity.notification

import kotlinx.serialization.Serializable
import ktask.base.persistence.serializers.SUUID
import ktask.base.scheduler.service.schedule.Schedule
import ktask.server.consumer.notification.SlackTaskConsumer
import ktask.server.domain.entity.ITaskRequest
import ktask.server.domain.entity.Recipient

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
data class SlackTaskRequest(
    override val id: SUUID,
    override val schedule: Schedule? = null,
    override val recipients: List<Recipient>,
    override val template: String,
    override val fields: Map<String, String>? = null,
    override val attachments: List<String>? = null,
    val channel: String,
) : ITaskRequest {
    init {
        require(recipients.isNotEmpty()) { "At least one recipient must be specified." }
    }

    override fun toTaskParameters(recipient: Recipient): MutableMap<String, Any> {
        return super.toTaskParameters(recipient = recipient).also { parameter ->
            parameter[SlackTaskConsumer.CHANNEL_KEY] = channel
        }
    }
}

/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.entity.notification

import kotlinx.serialization.Serializable
import ktask.base.persistence.serializers.SUUID
import ktask.base.utils.KLocalDateTime
import ktask.server.domain.entity.ITaskRequest
import ktask.server.domain.service.consumer.notifications.EmailTaskConsumer

/**
 * Represents a request to send an Email notification task.
 *
 * @property id The unique identifier of the task request.
 * @property schedule Optional date/time when the task must be sent. Null to send immediately.
 * @property recipients List of target recipients.
 * @property subject The subject or title of the email notification.
 * @property message The message or information contained in the email notification.
 */
@Serializable
data class EmailTaskRequest(
    override val id: SUUID,
    override val schedule: KLocalDateTime? = null,
    override val recipients: List<String>,
    val subject: String,
    val message: String
) : ITaskRequest {
    init {
        require(subject.isNotBlank()) { "Subject cannot be blank." }
        require(message.isNotBlank()) { "Message cannot be blank." }
        require(recipients.isNotEmpty()) { "At least one recipient must be specified." }
    }

    override fun toTaskParameters(recipient: String): MutableMap<String, Any> {
        return super.toTaskParameters(recipient).also {
            it[EmailTaskConsumer.SUBJECT_KEY] = subject
            it[EmailTaskConsumer.MESSAGE_KEY] = message
        }
    }
}

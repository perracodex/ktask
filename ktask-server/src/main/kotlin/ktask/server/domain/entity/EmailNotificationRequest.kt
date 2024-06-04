/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.entity

import kotlinx.serialization.Serializable
import ktask.base.persistence.serializers.SUUID
import ktask.base.utils.KLocalDateTime
import ktask.server.domain.service.processors.EmailTaskProcessor

/**
 * Represents a request to send an Email notification.
 *
 * @property id The unique identifier of the notification request.
 * @property schedule Optional date/time when the notification is scheduled to be sent. Null to send immediately.
 * @property recipients The recipients of the email notification.
 * @property message The message or information contained in the email notification.
 * @property subject The subject or title of the email notification.
 */
@Serializable
data class EmailNotificationRequest(
    override val id: SUUID,
    override val schedule: KLocalDateTime? = null,
    override val recipients: List<String>,
    override val message: String,
    val subject: String,
) : INotificationRequest {
    init {
        require(subject.isNotBlank()) { "Subject cannot be blank." }
        require(message.isNotBlank()) { "Message cannot be blank." }
        require(recipients.isNotEmpty()) { "At least one recipient must be specified." }
    }

    override fun toTaskParameters(recipient: String): MutableMap<String, Any> {
        return super.toTaskParameters(recipient).also {
            it[EmailTaskProcessor.SUBJECT_KEY] = subject
        }
    }
}

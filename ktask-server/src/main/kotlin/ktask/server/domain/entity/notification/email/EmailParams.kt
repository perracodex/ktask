/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.entity.notification.email

import kotlinx.serialization.Serializable

/**
 * Represents the target parameters for an email notification.
 *
 * @property template The template to be used for the notification. Null to send a plain message.
 * @property sender The sender of the notification.
 * @property cc List of recipients to be copied on the notification.
 * @property subject The subject or title of the notification.
 * @property message Optional plain message to be sent. If the template is provided, this field is embedded into it.
 */
@Serializable
data class EmailParams(
    val template: String? = null,
    val sender: String,
    val cc: List<String> = emptyList(),
    val subject: String,
    val message: String? = null,
) : java.io.Serializable {
    init {
        require(sender.isNotBlank()) { "Sender cannot be blank." }
        require(subject.isNotBlank()) { "Subject cannot be blank." }
        require(!message.isNullOrBlank() || !template.isNullOrBlank()) {
            "Either a message or a template must be provided."
        }
    }
}

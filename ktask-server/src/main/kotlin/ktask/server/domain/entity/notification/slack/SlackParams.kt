/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.entity.notification.slack

import kotlinx.serialization.Serializable

/**
 * Represents the target parameters for a Slack notification.
 *
 * @property template The template to be used for the notification. Null to send a plain message.
 * @property sender The sender of the notification.
 * @property channel The channel to send the notification to.
 * @property message Optional plain message to be sent. If the template is provided, this field is embedded into it.
 */
@Serializable
data class SlackParams(
    val template: String? = null,
    val sender: String,
    val channel: String,
    val message: String? = null
) : java.io.Serializable {
    init {
        require(sender.isNotBlank()) { "Sender cannot be blank." }
        require(channel.isNotBlank()) { "Channel cannot be blank." }
        require(!message.isNullOrBlank() || !template.isNullOrBlank()) {
            "Either a message or a template must be provided."
        }
    }
}

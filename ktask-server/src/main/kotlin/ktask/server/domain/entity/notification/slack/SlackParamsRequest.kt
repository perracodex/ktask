/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.entity.notification.slack

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Represents the target parameters for a Slack notification.
 *
 * @property template The template to be used for the notification.
 * @property channel The channel to send the notification to.
 * @property fields Optional fields to be included in the template.
 */
@Serializable
data class SlackParamsRequest(
    val template: String,
    val channel: String,
    val fields: Map<String, String>? = null
) {
    init {
        require(template.isNotBlank()) { "Template cannot be blank." }
        require(channel.isNotBlank()) { "Channel cannot be blank." }
    }

    fun serialize(): String {
        return Json.encodeToString<SlackParamsRequest>(value = this)
    }

    companion object {
        fun deserialize(string: String): SlackParamsRequest {
            return Json.decodeFromString<SlackParamsRequest>(string)
        }
    }
}

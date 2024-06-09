/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.entity.notification.email

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Represents the target parameters for an email notification.
 *
 * @property template The template to be used for the notification.
 * @property cc List of recipients to be copied on the notification.
 * @property subject The subject or title of the notification.
 * @property attachments Optional list of file paths to be attached to the notification.
 * @property fields Optional fields to be included in the template.
 */
@Serializable
data class EmailParams(
    val template: String,
    val cc: List<String> = emptyList(),
    val subject: String,
    val attachments: List<String>? = null,
    val fields: Map<String, String>? = null
) {
    init {
        require(template.isNotBlank()) { "Template cannot be blank." }
        require(subject.isNotBlank()) { "Subject cannot be blank." }
    }

    fun serialize(): String {
        return Json.encodeToString<EmailParams>(value = this)
    }

    companion object {
        fun deserialize(string: String): EmailParams {
            return Json.decodeFromString<EmailParams>(string)
        }
    }
}

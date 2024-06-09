/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

/**
 * Represents a recipient of a notification.
 *
 * @property target The target address of the recipient.
 * @property name The name of the recipient.
 * @property language The language of the recipient. Must be a valid ISO 639-1 language code.
 */
@Serializable
data class Recipient(
    val target: String,
    val name: String,
    val language: String
) {
    init {
        require(target.isNotBlank()) { "Target cannot be blank." }
        require(name.isNotBlank()) { "Target: $target. Name cannot be blank." }

        require(language.isNotBlank()) { "Target: $target. Language cannot be blank." }
        require(language.length == 2) { "Target: $target. Language code must be 2 characters long. Got: $language." }
        require(Locale.getISOLanguages().contains(language.lowercase())) {
            "Target: $target. Language code must be a valid ISO 639-1 language code. Got: $language."
        }
    }

    fun serialize(): String {
        return Json.encodeToString<Recipient>(value = this)
    }

    companion object {
        fun deserialize(string: String): Recipient {
            return Json.decodeFromString<Recipient>(string)
        }
    }
}

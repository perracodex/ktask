/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.entity

import kotlinx.serialization.Serializable

/**
 * Represents a recipient of a notification.
 *
 * @property target The target address of the recipient.
 * @property name The name of the recipient.
 * @property language Optional language of the recipient. Should be a valid ISO 639-1 language code.
 */
@Serializable
data class Recipient(
    val target: String,
    val name: String,
    val language: String? = null
) : java.io.Serializable {
    init {
        require(target.isNotBlank()) { "Target cannot be blank." }
        require(name.isNotBlank()) { "Name cannot be blank." }
    }
}

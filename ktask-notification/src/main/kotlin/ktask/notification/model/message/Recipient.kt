/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.model.message

import kotlinx.serialization.Serializable
import ktask.core.utils.LocaleUtils

/**
 * Represents a recipient of a notification.
 *
 * @property target The target address of the recipient.
 * @property name The name of the recipient.
 * @property locale The recipient language locale.
 */
@Serializable
public data class Recipient(
    val target: String,
    val name: String,
    val locale: String
) {
    init {
        require(target.isNotBlank()) { "Target cannot be blank." }
        require(name.isNotBlank()) { "Target: $target. Name cannot be blank." }
        require(LocaleUtils.isValidLocale(string = locale)) { "Target: $target. Invalid locale: $locale." }
    }
}

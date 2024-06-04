/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.settings.config.sections.security

import kotlinx.serialization.Serializable
import ktask.base.settings.config.parser.IConfigSection
import ktask.base.settings.config.sections.security.sections.ConstraintsSettings
import ktask.base.settings.config.sections.security.sections.EncryptionSettings

/**
 * Top level section for the Security related settings.
 *
 * @property isEnabled Whether to enable Basic and JWT authentication.
 * @property useSecureConnection Whether to use a secure connection or not.
 * @property encryption Settings related to encryption, such as the encryption keys.
 * @property constraints Settings related to security constraints, such endpoints rate limits.
 */
@Serializable
data class SecuritySettings(
    val isEnabled: Boolean,
    val useSecureConnection: Boolean,
    val encryption: EncryptionSettings,
    val constraints: ConstraintsSettings,
) : IConfigSection {
    companion object {
        /** The minimum length for a security key, such as encryption and secret keys. */
        const val MIN_KEY_LENGTH: Int = 12
    }
}

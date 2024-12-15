/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.settings.catalog.section.security

import ktask.base.settings.catalog.section.security.node.BasicAuthSettings
import ktask.base.settings.catalog.section.security.node.ConstraintsSettings

/**
 * Top level section for the Security related settings.
 *
 * @property useSecureConnection Whether to use a secure connection or not.
 * @property constraints Settings related to security constraints, such endpoints rate limits.
 * @property basicAuth Settings related to basic authentication, such as the realm and provider name.
 */
public data class SecuritySettings internal constructor(
    val useSecureConnection: Boolean,
    val constraints: ConstraintsSettings,
    val basicAuth: BasicAuthSettings,
)

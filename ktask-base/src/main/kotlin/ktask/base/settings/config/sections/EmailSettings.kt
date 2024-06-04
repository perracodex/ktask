/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.settings.config.sections

import kotlinx.serialization.Serializable
import ktask.base.settings.config.parser.IConfigSection

/**
 * Contains settings related to email communication.
 *
 * @property hostName The hostname of the outgoing mail server.
 * @property setSmtpPort The non-SSL port number of the outgoing mail server.
 * @property isSSLOnConnect Whether SSL/TLS encryption should be enabled for the SMTP transport upon connection (SMTPS/ POPS).
 * @property username The username to use when authentication is requested from the mail server.
 * @property password The password to use when authentication is requested from the mail server.
 */
@Serializable
data class EmailSettings(
    val hostName: String,
    val setSmtpPort: Int,
    val isSSLOnConnect: Boolean,
    val username: String,
    val password: String,
) : IConfigSection

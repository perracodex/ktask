/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.settings.config.sections

import kotlinx.serialization.Serializable
import ktask.base.settings.config.parser.IConfigSection

/**
 * Contains settings related to Slack communication.
 *
 * @property token The Slack API token.
 */
@Serializable
data class SlackSettings(
    val token: String,
) : IConfigSection

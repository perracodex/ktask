/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.settings.config.sections

import ktask.base.env.EnvironmentType
import ktask.base.settings.config.parser.IConfigSection

/**
 * Contains settings related to server runtime.
 *
 * @property machineId The unique machine ID. Used for generating unique IDs for call traceability.
 * @property environment The environment type. Not to be confused with the development mode flag.
 * @property doubleReceive Whether to enable the DoubleReceive plugin.
 * @property workingDir The working directory where files are stored.
 */
@kotlinx.serialization.Serializable
data class RuntimeSettings(
    val machineId: Int,
    val environment: EnvironmentType,
    val doubleReceive: Boolean,
    val workingDir: String
) : IConfigSection

/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.core.settings.catalog.sections

import io.github.perracodex.ktor.config.IConfigCatalogSection
import ktask.core.env.EnvironmentType

/**
 * Contains settings related to server runtime.
 *
 * @property machineId The unique machine ID. Used for generating unique IDs for call traceability.
 * @property environment The environment type. Not to be confused with the development mode flag.
 * @property doubleReceiveEnvironments The list of environments where the double receive plugin is enabled.
 * @property workingDir The working directory where files are stored.
 */
@kotlinx.serialization.Serializable
public data class RuntimeSettings(
    val machineId: Int,
    val environment: EnvironmentType,
    val doubleReceiveEnvironments: List<EnvironmentType>,
    val workingDir: String
) : IConfigCatalogSection

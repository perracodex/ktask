/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.core.env.health.check

import kotlinx.serialization.Serializable
import ktask.core.env.health.annotation.HealthCheckApi
import ktask.core.settings.AppSettings
import ktask.core.settings.catalog.section.security.node.ConstraintsSettings

/**
 * Used to check the security configuration of the application.
 *
 * @property errors List of errors found during the health check.
 * @property useSecureConnection Flag indicating if secure connections are used.
 * @property privateApi The rate limit specification for private API endpoints.
 */
@HealthCheckApi
@Serializable
public data class SecurityHealth(
    val errors: MutableList<String>,
    val useSecureConnection: Boolean,
    val privateApi: ConstraintsSettings.LimitSpec,
) {
    internal constructor() : this(
        errors = mutableListOf(),
        useSecureConnection = AppSettings.security.useSecureConnection,
        privateApi = AppSettings.security.constraints.privateApi
    )
}

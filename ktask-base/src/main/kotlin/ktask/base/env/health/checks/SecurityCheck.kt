/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.env.health.checks

import kotlinx.serialization.Serializable
import ktask.base.env.health.annotation.HealthCheckAPI
import ktask.base.settings.AppSettings
import ktask.base.settings.config.sections.security.sections.ConstraintsSettings

/**
 * Used to check the security configuration of the application.
 *
 * @property errors List of errors found during the health check.
 * @property useSecureConnection Flag indicating if secure connections are used.
 * @property privateApi The rate limit specification for private API endpoints.
 */
@HealthCheckAPI
@Serializable
public data class SecurityCheck(
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

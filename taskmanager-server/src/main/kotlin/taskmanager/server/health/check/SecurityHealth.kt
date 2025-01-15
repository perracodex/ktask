/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package taskmanager.server.health.check

import kotlinx.serialization.Serializable
import taskmanager.base.env.HealthCheckApi
import taskmanager.base.settings.AppSettings
import taskmanager.base.settings.catalog.section.security.node.ConstraintsSettings

/**
 * Used to check the security configuration of the application.
 *
 * @property errors List of errors found during the health check.
 * @property useSecureConnection Flag indicating if secure connections are used.
 * @property privateApi The rate limit specification for private API endpoints.
 */
@HealthCheckApi
@Serializable
public data class SecurityHealth private constructor(
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

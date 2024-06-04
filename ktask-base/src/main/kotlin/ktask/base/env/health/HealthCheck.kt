/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.env.health

import io.ktor.server.application.*
import kotlinx.serialization.Serializable
import ktask.base.database.service.DatabaseService
import ktask.base.env.health.annotation.HealthCheckAPI
import ktask.base.env.health.checks.*
import ktask.base.env.health.utils.collectRoutes

/**
 * Data class representing the overall health check for the system.
 *
 * @property health List of errors found during any of the health checks.
 * @property runtime The [RuntimeCheck] health check.
 * @property deployment The [DeploymentCheck] health check.
 * @property security The [SecurityCheck] health check.
 * @property database The [DatabaseCheck] health check.
 * @property application The [ApplicationCheck] health check.
 * @property snowflake The [SnowflakeCheck] health check.
 * @property endpoints The list of endpoints detected by the application.
 */
@OptIn(HealthCheckAPI::class)
@Serializable
data class HealthCheck(
    val health: MutableList<String>,
    val runtime: RuntimeCheck,
    val deployment: DeploymentCheck,
    val security: SecurityCheck,
    val database: DatabaseCheck,
    val application: ApplicationCheck,
    val snowflake: SnowflakeCheck,
    val endpoints: List<String>
) {
    constructor(call: ApplicationCall?) : this(
        health = mutableListOf(),
        runtime = RuntimeCheck(call = call),
        deployment = DeploymentCheck(call = call),
        security = SecurityCheck(),
        database = DatabaseService.getHealthCheck(),
        application = ApplicationCheck(),
        snowflake = SnowflakeCheck(),
        endpoints = call?.application?.collectRoutes() ?: emptyList()
    )

    init {
        health.addAll(runtime.errors)
        health.addAll(deployment.errors)
        health.addAll(security.errors)
        health.addAll(database.errors)
        health.addAll(application.errors)
        health.addAll(snowflake.errors)

        if (endpoints.isEmpty()) {
            health.add("No Endpoints Detected.")
        }

        if (health.isEmpty()) {
            health.add("No Errors Detected.")
        }
    }
}

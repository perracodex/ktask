/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.env.health.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.base.env.health.HealthCheck

/**
 * Defines the health check endpoints.
 *
 * The current implementation checks the basic readiness of the application. Future
 * enhancements could include more complex health checks, like database connectivity,
 * external service availability, or other critical component checks.
 */
public fun Route.healthCheckRoute() {
    // Healthcheck providing the current operational status.
    get("/health") {
        val healthCheck = HealthCheck(call = call)
        call.respond(status = HttpStatusCode.OK, message = healthCheck)
    }
}

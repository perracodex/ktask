/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package taskmanager.server.plugins

import io.github.perracodex.kopapi.dsl.operation.api
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import taskmanager.base.event.sseRoutes
import taskmanager.base.plugins.RateLimitScope
import taskmanager.base.settings.AppSettings
import taskmanager.base.snowflake.snowflakeRoute
import taskmanager.notification.api.actionTaskRoute
import taskmanager.notification.api.emailTaskRoute
import taskmanager.notification.api.slackTaskRoute
import taskmanager.scheduler.api.schedulerRoutes
import taskmanager.server.health.healthCheckRoute

/**
 * Initializes and sets up routing for the application.
 *
 * Routing is the core Ktor plugin for handling incoming requests in a server application.
 * When the client makes a request to a specific URL (for example, /hello), the routing
 * mechanism allows us to define how we want this request to be served.
 *
 * #### References
 * - [Ktor Routing Documentation](https://ktor.io/docs/server-routing.html)
 * - [Application Structure](https://ktor.io/docs/server-application-structure.html) for examples
 * of how to organize routes in diverse ways.
 * - [Ktor Rate Limit](https://ktor.io/docs/server-rate-limit.html)
 */
internal fun Application.configureRoutes() {

    routing {
        rateLimit(configuration = RateLimitName(name = RateLimitScope.PRIVATE_API.key)) {
            authenticate(AppSettings.security.basicAuth.providerName) {
                // Notification routes.
                actionTaskRoute()
                emailTaskRoute()
                slackTaskRoute()

                // System events related routes.
                sseRoutes()

                // Scheduled notification routes.
                schedulerRoutes()

                // Infrastructure routes.
                snowflakeRoute()
                healthCheckRoute()
            }
        }

        // Server root endpoint.
        get("/") {
            call.respondText(text = "Hello World.")
        } api {
            tags = setOf("Root")
            summary = "Root endpoint."
            description = "The root endpoint of the server."
            operationId = "root"
            response<String>(status = HttpStatusCode.OK) {
                description = "Root endpoint response."
            }
        }
    }
}

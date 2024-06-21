/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.routing.*
import ktask.base.env.health.routing.healthCheckRoute
import ktask.base.events.sseRoute
import ktask.base.plugins.RateLimitScope
import ktask.base.scheduler.routing.schedulerRoutes
import ktask.base.settings.AppSettings
import ktask.base.snowflake.snowflakeRoute
import ktask.notification.routing.notificationRoutes

/**
 * Initializes and sets up routing for the application.
 *
 * Routing is the core Ktor plugin for handling incoming requests in a server application.
 * When the client makes a request to a specific URL (for example, /hello), the routing
 * mechanism allows us to define how we want this request to be served.
 *
 * See: [Ktor Routing Documentation](https://ktor.io/docs/server-routing.html)
 *
 * See [Application Structure](https://ktor.io/docs/server-application-structure.html) for examples
 * of how to organize routes in diverse ways.
 *
 * See: [Ktor Rate Limit](https://ktor.io/docs/server-rate-limit.html)
 */
fun Application.configureRoutes() {

    routing {
        rateLimit(configuration = RateLimitName(name = RateLimitScope.PRIVATE_API.key)) {
            authenticate(AppSettings.security.basicAuth.providerName) {
                notificationRoutes()

                schedulerRoutes()

                snowflakeRoute()

                healthCheckRoute()

                healthCheckRoute()

                sseRoute()
            }
        }
    }
}

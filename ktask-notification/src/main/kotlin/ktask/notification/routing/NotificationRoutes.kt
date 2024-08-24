/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.routing

import io.ktor.server.routing.*
import ktask.notification.routing.endpoints.actionTaskRoute
import ktask.notification.routing.endpoints.emailTaskRoute
import ktask.notification.routing.endpoints.slackTaskRoute

/**
 * Initializes and sets up routing for the notification module.
 */
public fun Route.notificationRoutes() {

    route("push") {
        actionTaskRoute()
        emailTaskRoute()
        slackTaskRoute()
    }
}

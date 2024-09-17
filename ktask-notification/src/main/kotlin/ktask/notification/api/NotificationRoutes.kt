/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.api

import io.ktor.server.routing.*
import ktask.notification.api.endpoints.actionTaskRoute
import ktask.notification.api.endpoints.emailTaskRoute
import ktask.notification.api.endpoints.slackTaskRoute

/**
 * Initializes and sets up routing for the notification module.
 */
public fun Route.notificationRoutes() {
    actionTaskRoute()
    emailTaskRoute()
    slackTaskRoute()
}

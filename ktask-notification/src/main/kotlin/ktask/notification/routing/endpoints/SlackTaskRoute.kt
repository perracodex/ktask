/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.routing.endpoints

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.notification.entity.message.request.SlackRequest
import ktask.notification.service.NotificationService

/**
 * Creates a new scheduled Slack notification task.
 */
fun Route.slackTaskRoute() {

    // Create a new scheduled Slack notification task.
    post<SlackRequest>("slack") { request ->
        NotificationService.schedule(request = request)

        call.respond(
            status = HttpStatusCode.Created,
            message = "New Slack notification scheduled. ID: ${request.id}"
        )
    }
}

/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.server.domain.entity.notification.slack.SlackTaskRequest
import ktask.server.domain.service.NotificationService

/**
 * Creates a new scheduled Slack notification.
 */
fun Route.slackTaskRoute() {

    route("slack") {
        // Create a new scheduled Slack notification task.
        post<SlackTaskRequest> { request ->
            NotificationService.schedule(request = request)

            call.respond(
                status = HttpStatusCode.Created,
                message = "New Slack notification scheduled. ID: ${request.id}"
            )
        }
    }
}

/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.server.entity.notification.request.EmailRequest
import ktask.server.service.NotificationService

/**
 * Creates a new scheduled Email notification task.
 */
fun Route.emailTaskRoute() {

    // Create a new scheduled Email notification task.
    post<EmailRequest>("email") { request ->
        NotificationService.schedule(request = request)

        call.respond(
            status = HttpStatusCode.Created,
            message = "New Email notification scheduled. ID: ${request.id}"
        )
    }
}

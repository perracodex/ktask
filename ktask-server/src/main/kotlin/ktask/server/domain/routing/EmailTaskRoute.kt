/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.server.domain.entity.notification.email.EmailTaskRequest
import ktask.server.domain.service.NotificationService

/**
 * Creates a new scheduled Email notification.
 */
fun Route.emailTaskRoute() {

    route("email") {
        // Create a new scheduled Email notification task.
        post {
            val request = call.receive<EmailTaskRequest>()
            NotificationService.schedule(request = request)

            call.respond(
                status = HttpStatusCode.Created,
                message = "New Email notification scheduled. ID: ${request.id}"
            )
        }
    }
}

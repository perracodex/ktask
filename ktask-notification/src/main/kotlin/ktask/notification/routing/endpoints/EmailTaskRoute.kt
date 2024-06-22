/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.routing.endpoints

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.base.scheduler.service.task.TaskKey
import ktask.notification.entity.message.request.EmailRequest
import ktask.notification.service.NotificationService

/**
 * Creates a new scheduled Email notification task.
 */
fun Route.emailTaskRoute() {

    // Create a new scheduled Email notification task.
    post<EmailRequest>("email") { request ->
        val keys: List<TaskKey> = NotificationService.schedule(request = request)

        call.respond(
            status = HttpStatusCode.Created,
            message = keys
        )
    }
}

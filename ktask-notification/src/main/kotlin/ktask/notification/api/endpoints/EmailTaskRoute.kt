/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.api.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.core.scheduler.service.task.TaskKey
import ktask.notification.model.message.request.EmailRequest
import ktask.notification.service.NotificationService

/**
 * Creates a new scheduled Email notification task.
 */
internal fun Route.emailTaskRoute() {
    /**
     * Create a new scheduled Email notification task.
     * @OpenAPITag Notification
     */
    post<EmailRequest>("push/email") { request ->
        val keys: List<TaskKey> = NotificationService.schedule(request = request)
        call.respond(status = HttpStatusCode.Created, message = keys)
    }
}

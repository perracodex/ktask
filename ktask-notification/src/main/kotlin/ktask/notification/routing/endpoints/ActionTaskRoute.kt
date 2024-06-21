/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.routing.endpoints

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.notification.entity.action.request.ActionRequest
import ktask.notification.service.ActionService

/**
 * Creates a new scheduled action task.
 */
fun Route.actionTaskRoute() {

    // Create a new scheduled action task.
    post<ActionRequest>("action") { request ->
        ActionService.schedule(request = request)

        call.respond(
            status = HttpStatusCode.Created,
            message = "New action scheduled. ID: ${request.id}"
        )
    }
}

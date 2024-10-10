/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.api.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.core.scheduler.service.task.TaskKey
import ktask.notification.model.action.request.ActionRequest
import ktask.notification.service.ActionService

/**
 * Creates a new scheduled action task.
 */
internal fun Route.actionTaskRoute() {
    /**
     * Create a new scheduled action task.
     * @OpenAPITag Notification
     */
    post<ActionRequest>("push/action") { request ->
        val key: TaskKey = ActionService.schedule(request = request)
        call.respond(status = HttpStatusCode.Created, message = key)
    }
}

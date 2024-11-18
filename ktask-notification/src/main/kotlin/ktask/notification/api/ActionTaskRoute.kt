/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.api

import io.github.perracodex.kopapi.dsl.operation.api
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.notification.model.action.request.ActionRequest
import ktask.notification.service.ActionService
import ktask.scheduler.service.task.TaskKey

/**
 * Creates a new scheduled action task.
 */
public fun Route.actionTaskRoute() {
    post<ActionRequest>("/push/action") { request ->
        val key: TaskKey = ActionService.schedule(request = request)
        call.respond(status = HttpStatusCode.Created, message = key)
    } api {
        tags = setOf("Notification")
        summary = "Create a new scheduled action task."
        description = "Create a new scheduled action task."
        operationId = "createActionTask"
        requestBody<ActionRequest>()
        response<TaskKey>(status = HttpStatusCode.Created) {
            description = "The key of the scheduled action task."
        }
    }
}

/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.api

import io.github.perracodex.kopapi.dsl.operation.api
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.notification.model.message.request.SlackRequest
import ktask.notification.service.NotificationService
import ktask.scheduler.service.task.TaskKey

/**
 * Creates a new scheduled Slack notification task.
 */
public fun Route.slackTaskRoute() {
    post<SlackRequest>("/push/slack") { request ->
        val keys: List<TaskKey> = NotificationService.schedule(request = request)
        call.respond(status = HttpStatusCode.Created, message = keys)
    } api {
        tags = setOf("Notification")
        summary = "Create a new scheduled Slack notification task."
        description = "Create a new scheduled Slack notification task."
        operationId = "createSlackTask"
        requestBody<SlackRequest>()
        response<List<TaskKey>>(status = HttpStatusCode.Created) {
            description = "The keys of the scheduled Slack notification tasks."
        }
    }
}

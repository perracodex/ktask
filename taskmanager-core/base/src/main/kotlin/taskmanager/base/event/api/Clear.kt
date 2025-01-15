/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package taskmanager.base.event.api

import io.github.perracodex.kopapi.dsl.operation.api
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import taskmanager.base.event.SseRouteApi
import taskmanager.base.event.SseService

/**
 * Clears all the system events.
 */
@SseRouteApi
internal fun Route.sseClearRoute() {
    post("/admin/events/clear") {
        SseService.reset()
        call.respond(message = HttpStatusCode.OK)
    } api {
        tags = setOf("Events")
        summary = "Clear the events."
        description = "Clear all the events."
        operationId = "clearEvents"
        response<Unit>(status = HttpStatusCode.OK) {
            description = "The events are cleared."
        }
    }
}

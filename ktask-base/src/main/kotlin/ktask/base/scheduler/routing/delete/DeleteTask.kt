/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.scheduler.routing.delete

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.base.scheduler.service.SchedulerService

/**
 * Deletes a concrete scheduler task.
 */
fun Route.deleteSchedulerTaskRoute() {
    route("{name}/{group}") {
        // Deletes a concrete scheduler task.
        delete {
            val name: String = call.parameters["name"]!!
            val group: String = call.parameters["group"]!!
            val success: Boolean = SchedulerService.deleteTask(name = name, group = group)

            call.respond(
                status = if (success) HttpStatusCode.OK else HttpStatusCode.NotFound,
                message = success
            )
        }
    }
}

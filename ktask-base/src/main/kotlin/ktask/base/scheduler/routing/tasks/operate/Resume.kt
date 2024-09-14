/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.scheduler.routing.tasks.operate

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.base.scheduler.model.task.TaskStateChange
import ktask.base.scheduler.service.core.SchedulerService

/**
 * Resume a concrete scheduler task.
 */
internal fun Route.resumeSchedulerTaskRoute() {
    // Resume a concrete scheduled task.
    post("scheduler/task/{name}/{group}/resume") {
        val name: String = call.parameters["name"]!!
        val group: String = call.parameters["group"]!!
        val state: TaskStateChange = SchedulerService.tasks.resume(name = name, group = group)
        call.respond(status = HttpStatusCode.OK, message = state)
    }
}

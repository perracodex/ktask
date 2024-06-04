/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.scheduler.routing.state

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.base.scheduler.entity.TaskStateChangeEntity
import ktask.base.scheduler.service.SchedulerService

/**
 * Pauses all the scheduler tasks.
 */
fun Route.pauseAllSchedulerTasksRoute() {
    // Pauses all the scheduler tasks.
    post("/pause") {
        val state: TaskStateChangeEntity = SchedulerService.pause()
        call.respond(status = HttpStatusCode.OK, message = state)
    }
}

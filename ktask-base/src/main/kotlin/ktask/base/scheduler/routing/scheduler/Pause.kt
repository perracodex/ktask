/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.scheduler.routing.scheduler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.base.scheduler.model.TaskStateChange
import ktask.base.scheduler.service.core.SchedulerService

/**
 * Pauses all the scheduler tasks.
 */
internal fun Route.pauseSchedulerRoute() {
    // Pauses all the scheduler tasks.
    post("scheduler/pause") {
        val state: TaskStateChange = SchedulerService.pause()
        call.respond(status = HttpStatusCode.OK, message = state)
    }
}

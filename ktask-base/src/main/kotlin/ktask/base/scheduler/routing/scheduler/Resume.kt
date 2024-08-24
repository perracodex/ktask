/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.scheduler.routing.scheduler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.base.scheduler.entity.TaskStateChangeEntity
import ktask.base.scheduler.service.core.SchedulerService

/**
 * Resume all the scheduler tasks.
 */
internal fun Route.resumeSchedulerRoute() {
    // Resume all the scheduler tasks.
    post("resume") {
        val state: TaskStateChangeEntity = SchedulerService.resume()
        call.respond(status = HttpStatusCode.OK, message = state)
    }
}

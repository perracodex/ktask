/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.scheduler.api.scheduler

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.base.scheduler.model.task.TaskStateChange
import ktask.base.scheduler.service.core.SchedulerService

/**
 * Resume all the scheduler tasks.
 */
internal fun Route.resumeSchedulerRoute() {
    /**
     * Resume all the scheduler tasks.
     * @OpenAPITag Scheduler - Maintenance
     */
    post("scheduler/resume") {
        val state: TaskStateChange = SchedulerService.resume()
        call.respond(status = HttpStatusCode.OK, message = state)
    }
}

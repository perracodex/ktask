/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.core.scheduler.api.scheduler.operate

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.core.scheduler.api.SchedulerRouteAPI
import ktask.core.scheduler.model.task.TaskStateChange
import ktask.core.scheduler.service.SchedulerService

/**
 * Pauses all the scheduler tasks.
 */
@SchedulerRouteAPI
internal fun Route.pauseSchedulerRoute() {
    /**
     * Pauses all the scheduler tasks.
     * @OpenAPITag Scheduler - Maintenance
     */
    post("scheduler/pause") {
        val state: TaskStateChange = SchedulerService.pause()
        call.respond(status = HttpStatusCode.OK, message = state)
    }
}

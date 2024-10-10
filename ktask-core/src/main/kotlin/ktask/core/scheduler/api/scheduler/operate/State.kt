/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.core.scheduler.api.scheduler.operate

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.core.scheduler.api.SchedulerRouteAPI
import ktask.core.scheduler.service.SchedulerService

/**
 * Returns the state of the task scheduler.
 */
@SchedulerRouteAPI
internal fun Route.schedulerStateRoute() {
    /**
     * Returns the state of the task scheduler.
     * @OpenAPITag Scheduler - Maintenance
     */
    get("scheduler/state") {
        val state: SchedulerService.TaskSchedulerState = SchedulerService.state()
        call.respond(status = HttpStatusCode.OK, message = state.name)
    }
}


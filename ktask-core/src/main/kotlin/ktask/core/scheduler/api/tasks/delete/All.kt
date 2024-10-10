/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.core.scheduler.api.tasks.delete

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.core.scheduler.api.SchedulerRouteAPI
import ktask.core.scheduler.service.SchedulerService

/**
 * Deletes all the scheduler tasks.
 */
@SchedulerRouteAPI
internal fun Route.deleteAllSchedulerTasksRoute() {
    /**
     * Deletes all scheduler tasks.
     * @OpenAPITag Scheduler
     */
    delete("scheduler/task") {
        val deletedCount: Int = SchedulerService.tasks.deleteAll()
        call.respond(status = HttpStatusCode.OK, message = deletedCount)
    }
}

/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.core.scheduler.api.tasks.fetch

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.core.persistence.utils.toUuidOrNull
import ktask.core.scheduler.api.SchedulerRouteAPI
import ktask.core.scheduler.model.task.TaskSchedule
import ktask.core.scheduler.service.SchedulerService
import kotlin.uuid.Uuid

/**
 * Gets all scheduler tasks.
 */
@SchedulerRouteAPI
internal fun Route.getSchedulerTasksRoute() {
    /**
     * Gets all scheduler tasks.
     * @OpenAPITag Scheduler
     */
    get("scheduler/task") {
        val groupId: Uuid? = call.parameters["group"]?.toUuidOrNull()
        val tasks: List<TaskSchedule> = SchedulerService.tasks.all(groupId = groupId)
        call.respond(status = HttpStatusCode.OK, message = tasks)
    }
}

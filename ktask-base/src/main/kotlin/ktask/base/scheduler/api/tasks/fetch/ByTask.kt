/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.scheduler.api.tasks.fetch

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.base.persistence.utils.toUuidOrNull
import ktask.base.scheduler.api.SchedulerRouteAPI
import ktask.base.scheduler.model.task.TaskSchedule
import ktask.base.scheduler.service.core.SchedulerService
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

/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.scheduler.routing.tasks.get

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.base.persistence.utils.toUUIDOrNull
import ktask.base.scheduler.entity.TaskScheduleEntity
import ktask.base.scheduler.service.core.SchedulerService
import java.util.*

/**
 * Gets all scheduler tasks.
 */
fun Route.getSchedulerTasksRoute() {
    // Gets all scheduler tasks.
    get {
        val groupId: UUID? = call.parameters["group"]?.toUUIDOrNull()
        val tasks: List<TaskScheduleEntity> = SchedulerService.tasks.all(groupId = groupId)
        call.respond(status = HttpStatusCode.OK, message = tasks)
    }
}

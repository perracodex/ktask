/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.scheduler.routing.tasks.get

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.base.persistence.utils.toUuidOrNull
import ktask.base.scheduler.entity.TaskScheduleEntity
import ktask.base.scheduler.service.core.SchedulerService
import kotlin.uuid.Uuid

/**
 * Gets all scheduler tasks.
 */
internal fun Route.getSchedulerTasksRoute() {
    // Gets all scheduler tasks.
    get {
        val groupId: Uuid? = call.parameters["group"]?.toUuidOrNull()
        val tasks: List<TaskScheduleEntity> = SchedulerService.tasks.all(groupId = groupId)
        call.respond(status = HttpStatusCode.OK, message = tasks)
    }
}

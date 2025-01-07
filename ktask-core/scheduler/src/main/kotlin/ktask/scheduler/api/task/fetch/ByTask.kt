/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.scheduler.api.task.fetch

import io.github.perracodex.kopapi.dsl.operation.api
import io.github.perracodex.kopapi.dsl.parameter.queryParameter
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.base.util.toUuidOrNull
import ktask.scheduler.model.task.TaskSchedule
import ktask.scheduler.service.SchedulerService
import kotlin.uuid.Uuid

/**
 * Gets all scheduler tasks.
 */
internal fun Route.getSchedulerTasksRoute() {
    get("/admin/scheduler/task") {
        val groupId: Uuid? = call.queryParameters["groupId"].toUuidOrNull()
        val tasks: List<TaskSchedule> = SchedulerService.tasks.all(groupId = groupId)
        call.respond(status = HttpStatusCode.OK, message = tasks)
    } api {
        tags = setOf("Scheduler")
        summary = "Get all scheduler tasks."
        description = "Get all the scheduler tasks."
        operationId = "getSchedulerTasks"
        queryParameter<Uuid>(name = "groupId") {
            description = "The group ID of the tasks."
            required = false
        }
        response<List<TaskSchedule>>(status = HttpStatusCode.OK) {
            description = "The list of tasks."
        }
    }
}

/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.core.scheduler.api.task.fetch

import io.github.perracodex.kopapi.dsl.operation.api
import io.github.perracodex.kopapi.dsl.parameter.queryParameter
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.core.scheduler.api.SchedulerRouteApi
import ktask.core.scheduler.model.task.TaskSchedule
import ktask.core.scheduler.service.SchedulerService
import ktask.core.util.trimOrNull

/**
 * Gets all scheduler tasks.
 */
@SchedulerRouteApi
internal fun Route.getSchedulerTasksRoute() {
    get("/admin/scheduler/task") {
        val groupId: String? = call.queryParameters["groupId"].trimOrNull()
        val tasks: List<TaskSchedule> = SchedulerService.tasks.all(groupId = groupId)
        call.respond(status = HttpStatusCode.OK, message = tasks)
    } api {
        tags = setOf("Scheduler")
        summary = "Get all scheduler tasks."
        description = "Get all the scheduler tasks."
        operationId = "getSchedulerTasks"
        queryParameter<String>(name = "groupId") {
            description = "The group ID of the tasks."
            required = false
        }
        response<List<TaskSchedule>>(status = HttpStatusCode.OK) {
            description = "The list of tasks."
        }
    }
}

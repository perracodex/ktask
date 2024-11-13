/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.core.scheduler.api.scheduler.audit

import io.github.perracodex.kopapi.dsl.operation.api
import io.github.perracodex.kopapi.dsl.parameter.pathParameter
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import ktask.core.scheduler.api.SchedulerRouteApi
import ktask.core.scheduler.audit.AuditService
import ktask.core.scheduler.model.audit.AuditLog

/**
 * Returns the audit log for a specific task.
 */
@SchedulerRouteApi
internal fun Route.schedulerAuditByTaskRoute() {
    get("/admin/scheduler/audit/{name}/{group}") {
        val taskName: String = call.parameters.getOrFail(name = "name")
        val taskGroup: String = call.parameters.getOrFail(name = "group")
        val audit: List<AuditLog> = AuditService.find(taskName = taskName, taskGroup = taskGroup)

        if (audit.isEmpty()) {
            call.respond(status = HttpStatusCode.NotFound, message = "No audit logs found for the task.")
        } else {
            call.respond(status = HttpStatusCode.OK, message = audit)
        }
    } api {
        tags = setOf("Scheduler Admin")
        summary = "Get audit logs for a specific task."
        description = "Get all existing audit logs for a specific task."
        operationId = "getAuditLogsByTask"
        pathParameter<String>(name = "name") {
            description = "The name of the task."
        }
        pathParameter<String>(name = "group") {
            description = "The group of the task."
        }
        response<List<AuditLog>>(status = HttpStatusCode.OK) {
            description = "All audit logs for the task."
        }
        response<String>(status = HttpStatusCode.NotFound) {
            description = "No audit logs found for the task."
        }
    }
}

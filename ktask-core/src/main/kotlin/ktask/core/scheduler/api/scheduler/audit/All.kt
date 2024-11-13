/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.core.scheduler.api.scheduler.audit

import io.github.perracodex.kopapi.dsl.operation.api
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ktask.core.scheduler.api.SchedulerRouteApi
import ktask.core.scheduler.audit.AuditService
import ktask.core.scheduler.model.audit.AuditLog

/**
 * Returns all existing audit logs for the scheduler.
 */
@SchedulerRouteApi
internal fun Route.schedulerAllAuditRoute() {
    get("/admin/scheduler/audit") {
        val audit: List<AuditLog> = AuditService.findAll()
        call.respond(status = HttpStatusCode.OK, message = audit)
    } api {
        tags = setOf("Scheduler Admin")
        summary = "Get all scheduler audit logs."
        description = "Get all existing audit logs for the scheduler."
        operationId = "getAllSchedulerAuditLogs"
        response<List<AuditLog>>(status = HttpStatusCode.OK) {
            description = "All scheduler audit logs."
        }
    }
}

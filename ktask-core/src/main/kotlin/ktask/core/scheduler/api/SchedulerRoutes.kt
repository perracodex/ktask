/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.core.scheduler.api

import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import ktask.core.scheduler.api.scheduler.audit.schedulerAllAuditRoute
import ktask.core.scheduler.api.scheduler.audit.schedulerAuditByTaskRoute
import ktask.core.scheduler.api.scheduler.operate.pauseSchedulerRoute
import ktask.core.scheduler.api.scheduler.operate.restartSchedulerRoute
import ktask.core.scheduler.api.scheduler.operate.resumeSchedulerRoute
import ktask.core.scheduler.api.scheduler.operate.schedulerStateRoute
import ktask.core.scheduler.api.tasks.delete.deleteAllSchedulerTasksRoute
import ktask.core.scheduler.api.tasks.delete.deleteSchedulerTaskRoute
import ktask.core.scheduler.api.tasks.fetch.getSchedulerTaskGroupsRoute
import ktask.core.scheduler.api.tasks.fetch.getSchedulerTasksRoute
import ktask.core.scheduler.api.tasks.operate.pauseSchedulerTaskRoute
import ktask.core.scheduler.api.tasks.operate.resendSchedulerTaskRoute
import ktask.core.scheduler.api.tasks.operate.resumeSchedulerTaskRoute
import ktask.core.scheduler.api.view.schedulerDashboardRoute

/**
 * Annotation for controlled access to the Scheduler Routes API.
 */
@RequiresOptIn(level = RequiresOptIn.Level.ERROR, message = "Only to be used within the Scheduler Routes API.")
@Retention(AnnotationRetention.BINARY)
internal annotation class SchedulerRouteAPI

/**
 * Route administers all scheduled tasks, allowing to list and delete them.
 */
@OptIn(SchedulerRouteAPI::class)
public fun Route.schedulerRoutes() {

    // Sets up the routing to serve resources as static content for the scheduler.
    staticResources(remotePath = "/templates/scheduler", basePackage = "/templates/scheduler")

    // Maintenance related routes.
    schedulerDashboardRoute()
    schedulerStateRoute()
    pauseSchedulerRoute()
    resumeSchedulerRoute()
    restartSchedulerRoute()
    schedulerAllAuditRoute()
    schedulerAuditByTaskRoute()

    // Task related routes.
    getSchedulerTasksRoute()
    getSchedulerTaskGroupsRoute()
    deleteSchedulerTaskRoute()
    deleteAllSchedulerTasksRoute()
    pauseSchedulerTaskRoute()
    resumeSchedulerTaskRoute()
    resendSchedulerTaskRoute()
}

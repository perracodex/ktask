/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.scheduler.api

import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import ktask.scheduler.api.scheduler.audit.schedulerAllAuditRoute
import ktask.scheduler.api.scheduler.audit.schedulerAuditByTaskRoute
import ktask.scheduler.api.scheduler.operate.pauseSchedulerRoute
import ktask.scheduler.api.scheduler.operate.restartSchedulerRoute
import ktask.scheduler.api.scheduler.operate.resumeSchedulerRoute
import ktask.scheduler.api.scheduler.operate.schedulerStateRoute
import ktask.scheduler.api.task.delete.deleteAllSchedulerTasksRoute
import ktask.scheduler.api.task.delete.deleteSchedulerGroupRoute
import ktask.scheduler.api.task.delete.deleteSchedulerTaskRoute
import ktask.scheduler.api.task.fetch.getSchedulerAllGroupsRoute
import ktask.scheduler.api.task.fetch.getSchedulerTasksRoute
import ktask.scheduler.api.task.operate.pauseSchedulerTaskRoute
import ktask.scheduler.api.task.operate.resendSchedulerTaskRoute
import ktask.scheduler.api.task.operate.resumeSchedulerTaskRoute
import ktask.scheduler.api.view.schedulerDashboardRoute

/**
 * Route administers all scheduled tasks, allowing to list and delete them.
 */
public fun Route.schedulerRoutes() {

    // Sets up the routing to serve resources as static content for the scheduler.
    staticResources(remotePath = "/scheduler", basePackage = "/scheduler")

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
    getSchedulerAllGroupsRoute()
    deleteSchedulerTaskRoute()
    deleteSchedulerGroupRoute()
    deleteAllSchedulerTasksRoute()
    pauseSchedulerTaskRoute()
    resumeSchedulerTaskRoute()
    resendSchedulerTaskRoute()
}

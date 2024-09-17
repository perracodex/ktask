/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.scheduler.api

import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import ktask.base.scheduler.api.scheduler.audit.schedulerAllAuditRoute
import ktask.base.scheduler.api.scheduler.audit.schedulerAuditByTaskRoute
import ktask.base.scheduler.api.scheduler.operate.pauseSchedulerRoute
import ktask.base.scheduler.api.scheduler.operate.restartSchedulerRoute
import ktask.base.scheduler.api.scheduler.operate.resumeSchedulerRoute
import ktask.base.scheduler.api.scheduler.operate.schedulerStateRoute
import ktask.base.scheduler.api.tasks.delete.deleteAllSchedulerTasksRoute
import ktask.base.scheduler.api.tasks.delete.deleteSchedulerTaskRoute
import ktask.base.scheduler.api.tasks.get.getSchedulerTaskGroupsRoute
import ktask.base.scheduler.api.tasks.get.getSchedulerTasksRoute
import ktask.base.scheduler.api.tasks.operate.pauseSchedulerTaskRoute
import ktask.base.scheduler.api.tasks.operate.resendSchedulerTaskRoute
import ktask.base.scheduler.api.tasks.operate.resumeSchedulerTaskRoute
import ktask.base.scheduler.api.view.schedulerDashboardRoute

/**
 * Route administers all scheduled tasks, allowing to list and delete them.
 */
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

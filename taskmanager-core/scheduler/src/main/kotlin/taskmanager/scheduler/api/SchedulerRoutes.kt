/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package taskmanager.scheduler.api

import io.ktor.server.routing.*
import taskmanager.scheduler.api.scheduler.audit.schedulerAllAuditRoute
import taskmanager.scheduler.api.scheduler.audit.schedulerAuditByTaskRoute
import taskmanager.scheduler.api.scheduler.operate.pauseSchedulerRoute
import taskmanager.scheduler.api.scheduler.operate.restartSchedulerRoute
import taskmanager.scheduler.api.scheduler.operate.resumeSchedulerRoute
import taskmanager.scheduler.api.scheduler.operate.schedulerStateRoute
import taskmanager.scheduler.api.task.delete.deleteAllSchedulerTasksRoute
import taskmanager.scheduler.api.task.delete.deleteSchedulerGroupRoute
import taskmanager.scheduler.api.task.delete.deleteSchedulerTaskRoute
import taskmanager.scheduler.api.task.fetch.getSchedulerAllGroupsRoute
import taskmanager.scheduler.api.task.fetch.getSchedulerTasksRoute
import taskmanager.scheduler.api.task.operate.pauseSchedulerTaskRoute
import taskmanager.scheduler.api.task.operate.resendSchedulerTaskRoute
import taskmanager.scheduler.api.task.operate.resumeSchedulerTaskRoute
import taskmanager.scheduler.api.view.schedulerDashboardRoute

/**
 * Route administers all scheduled tasks, allowing to list and delete them.
 */
public fun Route.schedulerRoutes() {

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

/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.scheduler.routing

import io.ktor.server.routing.*
import ktask.base.scheduler.routing.delete.deleteAllSchedulerTasksRoute
import ktask.base.scheduler.routing.delete.deleteSchedulerTaskRoute
import ktask.base.scheduler.routing.get.getSchedulerTaskGroupsRoute
import ktask.base.scheduler.routing.get.getSchedulerTasksRoute
import ktask.base.scheduler.routing.state.pauseAllSchedulerTasksRoute
import ktask.base.scheduler.routing.state.pauseSchedulerTaskRoute
import ktask.base.scheduler.routing.state.resumeAllSchedulerTasksRoute
import ktask.base.scheduler.routing.state.resumeSchedulerTaskRoute
import ktask.base.scheduler.routing.view.schedulerDashboardRoute

/**
 * Route administers all scheduled tasks, allowing to list and delete them.
 */
fun Route.schedulerRoutes() {

    route("scheduler/tasks") {
        schedulerDashboardRoute()

        getSchedulerTasksRoute()
        getSchedulerTaskGroupsRoute()

        deleteSchedulerTaskRoute()
        deleteAllSchedulerTasksRoute()

        pauseAllSchedulerTasksRoute()
        pauseSchedulerTaskRoute()

        resumeAllSchedulerTasksRoute()
        resumeSchedulerTaskRoute()
    }
}

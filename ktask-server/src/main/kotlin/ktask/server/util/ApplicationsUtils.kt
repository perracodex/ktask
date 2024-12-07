/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.util

import io.ktor.server.application.*
import ktask.core.env.Tracer
import ktask.core.settings.AppSettings
import ktask.core.snowflake.SnowflakeFactory
import ktask.core.util.NetworkUtils
import ktask.scheduler.service.SchedulerService

/**
 * Utility functions for the application server.
 */
internal object ApplicationsUtils {
    private val tracer: Tracer = Tracer<ApplicationsUtils>()

    /**
     * Perform any additional server configuration that is required for the application to run.
     *
     * @param application The Ktor application instance.
     */
    fun completeServerConfiguration(application: Application) {
        // Add a startup hook to start the scheduler when the server starts.
        application.monitor.subscribe(definition = ApplicationStarted) {
            SchedulerService.start()
        }

        // Add a shutdown hook to stop the scheduler when the server stops.
        application.monitor.subscribe(ApplicationStopping) {
            SchedulerService.release()
        }

        // Watch the server for readiness.
        application.monitor.subscribe(definition = ServerReady) {
            outputState(application = application)
        }
    }

    /**
     * Output the server state to the console, including main endpoints and configuration.
     */
    private fun outputState(application: Application) {
        // Dumps the server's endpoints to the console for easy access and testing.
        // This does not include the actual API routes endpoints.
        NetworkUtils.logEndpoints(reason = "Scheduler", endpoints = listOf("admin/scheduler/dashboard"))
        NetworkUtils.logEndpoints(reason = "Healthcheck", endpoints = listOf("admin/health"))
        NetworkUtils.logEndpoints(reason = "Snowflake", endpoints = listOf("admin/snowflake/${SnowflakeFactory.nextId()}"))
        NetworkUtils.logEndpoints(reason = "Micrometer Metrics", endpoints = listOf("admin/metrics"))

        // Log the server readiness.
        tracer.withSeverity("Development Mode Enabled: ${application.developmentMode}")
        tracer.info("Server configured. Environment: ${AppSettings.runtime.environment}")
    }
}

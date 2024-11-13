/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.util

import io.ktor.server.application.*
import ktask.core.env.Tracer
import ktask.core.settings.AppSettings
import ktask.core.snowflake.SnowflakeFactory
import ktask.core.util.NetworkUtils

/**
 * Utility functions for the application server.
 */
internal object ApplicationsUtils {
    private val tracer = Tracer<ApplicationsUtils>()

    /**
     * Watches the server for readiness and logs the server's endpoints to the console.
     */
    fun watchServer(application: Application) {
        application.monitor.subscribe(definition = ServerReady) {

            // Dumps the server's endpoints to the console for easy access and testing.
            // This does not include the actual API routes endpoints.
            NetworkUtils.logEndpoints(reason = "Demo", endpoints = listOf("demo"))
            NetworkUtils.logEndpoints(reason = "Healthcheck", endpoints = listOf("admin/health"))
            NetworkUtils.logEndpoints(reason = "Snowflake", endpoints = listOf("admin/snowflake/${SnowflakeFactory.nextId()}"))
            NetworkUtils.logEndpoints(reason = "Scheduler", endpoints = listOf("admin/scheduler/dashboard"))
            NetworkUtils.logEndpoints(reason = "Micrometer Metrics", endpoints = listOf("admin/metrics"))

            if (AppSettings.apiSchema.environments.contains(AppSettings.runtime.environment)) {
                NetworkUtils.logEndpoints(
                    reason = "Swagger, Redoc, OpenApi",
                    endpoints = listOf(
                        AppSettings.apiSchema.swaggerEndpoint,
                        AppSettings.apiSchema.redocEndpoint,
                        AppSettings.apiSchema.openApiEndpoint,
                    )
                )
            }

            // Log the server readiness.
            tracer.withSeverity("Development Mode Enabled: ${application.developmentMode}.")
            tracer.info("Server configured. Environment: ${AppSettings.runtime.environment}.")
        }
    }
}

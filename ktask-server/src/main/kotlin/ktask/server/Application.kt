/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server

import io.ktor.server.application.*
import io.ktor.server.netty.*
import ktask.base.plugins.*
import ktask.base.settings.AppSettings
import ktask.server.plugins.configureRoutes
import ktask.server.utils.ApplicationsUtils

/**
 * Application main entry point.
 * Launches the Ktor server using Netty as the application engine.
 *
 * See: [Choosing an engine](https://ktor.io/docs/server-engines.html)
 *
 * See: [Configure an engine](https://ktor.io/docs/server-engines.html#configure-engine)
 *
 * See: [Application Monitoring With Server Events](https://ktor.io/docs/server-events.html)
 *
 * @param args Command line arguments passed to the application.
 */
public fun main(args: Array<String>) {
    EngineMain.main(args)
}

/**
 * Application configuration module, responsible for setting up the server with various plugins.
 * Execution order is important as some configurations depend on the prior initialization of others.
 *
 * See: [Modules](https://ktor.io/docs/server-modules.html)
 *
 * See: [Plugins](https://ktor.io/docs/server-plugins.html)
 */
internal fun Application.ktaskModule() {

    AppSettings.load(applicationConfig = environment.config)

    configureDatabase()

    configureCors()

    configureSecureConnection()

    configureBasicAuthentication()

    configureHeaders()

    configureHttp()

    configureCallLogging()

    configureSerialization()

    configureRateLimit()

    configureRoutes()

    configuredApiSchema()

    configureMicroMeterMetrics()

    configureStatusPages()

    configureDoubleReceive()

    configureTaskScheduler()

    configureThymeleaf()

    ApplicationsUtils.watchServer(environment = this.environment)
}

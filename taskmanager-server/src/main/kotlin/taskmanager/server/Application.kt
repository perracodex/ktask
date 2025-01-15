/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package taskmanager.server

import io.ktor.server.application.*
import io.ktor.server.netty.*
import taskmanager.base.plugins.*
import taskmanager.base.settings.AppSettings
import taskmanager.database.plugins.configureDatabase
import taskmanager.server.plugins.configureRoutes
import taskmanager.server.util.ApplicationsUtils

/**
 * Application main entry point.
 * Launches the Ktor server using Netty as the application engine.
 *
 * #### Continuous Compilation
 * - Command: `./gradlew -t build -x test -i`
 * - [Auto-Reload](https://ktor.io/docs/server-auto-reload.html)
 *
 * #### References
 * - [Choosing an engine](https://ktor.io/docs/server-engines.html)
 * - [Configure an engine](https://ktor.io/docs/server-engines.html#configure-engine)
 * - [Application Monitoring With Server Events](https://ktor.io/docs/server-events.html)
 *
 * @param args Command line arguments passed to the application.
 */
public fun main(args: Array<String>) {
    EngineMain.main(args)
}

/**
 * Application configuration module, responsible for setting up the server with various plugins.
 *
 * #### Important
 * The order of execution is vital, as certain configurations depend on the initialization
 * of previous plugins. Incorrect ordering can lead to runtime errors or configuration issues.
 *
 * #### References
 * - [Modules](https://ktor.io/docs/server-modules.html)
 * - [Plugins](https://ktor.io/docs/server-plugins.html)
 */
internal fun Application.taskManagerModule() {

    AppSettings.load(applicationConfig = environment.config)

    configureDatabase()

    configureCors()

    configureSecureConnection()

    configureBasicAuthentication()

    configureHeaders()

    configureHttp()

    configureSse()

    configureCallLogging()

    configureSerialization()

    configureRateLimit()

    configureRoutes()

    configureApiSchema()

    configureMicroMeterMetrics()

    configureStatusPages()

    configureDoubleReceive()

    configureThymeleaf()

    ApplicationsUtils.completeServerConfiguration(application = this)
}

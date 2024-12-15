/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.database.plugins

import io.ktor.server.application.*
import ktask.base.env.Telemetry
import ktask.database.schema.scheduler.SchedulerAuditTable

/**
 * Configures the custom [DbPlugin].
 *
 * This will set up and configure database, including the connection pool, and register
 * the database schema tables so that the ORM can interact with them.
 *
 * @see [DbPlugin]
 */
public fun Application.configureDatabase() {

    install(plugin = DbPlugin) {
        telemetryRegistry = Telemetry.registry

        tables.add(SchedulerAuditTable)
    }
}

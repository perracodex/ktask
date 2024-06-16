/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.plugins

import io.ktor.server.application.*
import ktask.base.database.plugin.DbPlugin
import ktask.base.database.schema.SchedulerAuditTable

/**
 * Configures the custom [DbPlugin].
 *
 * This will set up and configure database, including the connection pool, and register
 * the database schema tables so that the ORM can interact with them.
 *
 * @see DbPlugin
 */
fun Application.configureDatabase() {

    install(plugin = DbPlugin) {
        micrometerRegistry = appMicrometerRegistry

        tables.add(SchedulerAuditTable)
    }
}

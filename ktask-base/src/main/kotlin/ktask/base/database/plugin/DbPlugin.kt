/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.database.plugin

import io.ktor.server.application.*
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import ktask.base.database.service.DatabaseService
import ktask.base.settings.AppSettings
import org.jetbrains.exposed.sql.Table

/**
 * Configuration for the [DbPlugin].
 */
class DbPluginConfig {
    /** Optional [PrometheusMeterRegistry] instance for micro-metrics monitoring. */
    var micrometerRegistry: PrometheusMeterRegistry? = null

    /** List of tables to be registered with the database. */
    val tables: MutableList<Table> = mutableListOf()
}

/**
 * Custom Ktor plugin to configure the database.
 */
val DbPlugin: ApplicationPlugin<DbPluginConfig> = createApplicationPlugin(
    name = "DbPlugin",
    createConfiguration = ::DbPluginConfig
) {
    DatabaseService.init(
        settings = AppSettings.database,
        micrometerRegistry = pluginConfig.micrometerRegistry
    ) {
        pluginConfig.tables.forEach { table ->
            addTable(table)
        }
    }
}

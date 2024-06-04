/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.database.plugin

import io.ktor.server.application.*
import ktask.base.database.service.DatabaseService
import ktask.base.settings.AppSettings
import org.jetbrains.exposed.sql.Table

/**
 * Configuration for the [DbPlugin].
 */
class DbPluginConfig {
    /** List of tables to be registered with the database. */
    val tables: MutableList<Table> = mutableListOf()
}

/**
 * Custom Ktor plugin to configure the database.
 */
val DbPlugin: ApplicationPlugin<DbPluginConfig> = createApplicationPlugin(
    name = "DatabasePlugin",
    createConfiguration = ::DbPluginConfig
) {
    DatabaseService.init(
        settings = AppSettings.database,
    ) {
        pluginConfig.tables.forEach { table ->
            addTable(table)
        }
    }
}

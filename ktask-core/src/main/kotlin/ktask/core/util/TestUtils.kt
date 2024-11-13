/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.core.util

import io.ktor.server.config.*
import ktask.core.database.service.DatabaseService
import ktask.core.settings.AppSettings
import java.io.File

/**
 * Common utilities for unit testing.
 */
public object TestUtils {

    /**
     * Loads the application settings for testing.
     */
    public fun loadSettings() {
        val testConfig = ApplicationConfig(configPath = "application.conf")

        AppSettings.load(applicationConfig = testConfig)
    }

    /**
     * Sets up the database for testing.
     */
    public fun setupDatabase() {
        DatabaseService.init(settings = AppSettings.database)
    }

    /**
     * Tears down the testing environment.
     */
    public fun tearDown() {
        DatabaseService.close()

        val tempRuntime = File(AppSettings.runtime.workingDir)
        if (tempRuntime.exists()) {
            tempRuntime.deleteRecursively()
        }
    }
}

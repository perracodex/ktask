/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.utils

import io.ktor.server.config.*
import kotlinx.coroutines.runBlocking
import ktask.base.database.service.DatabaseService
import ktask.base.settings.AppSettings
import java.io.File

/**
 * Common utilities for unit testing.
 */
object TestUtils {

    /**
     * Loads the application settings for testing.
     */
    fun loadSettings() {
        val testConfig = ApplicationConfig(configPath = "config/config.conf")

        runBlocking {
            AppSettings.load(applicationConfig = testConfig)
        }
    }

    /**
     * Sets up the database for testing.
     */
    fun setupDatabase() {
        DatabaseService.init(settings = AppSettings.database)
    }

    /**
     * Tears down the testing environment.
     */
    fun tearDown() {
        DatabaseService.close()

        val tempRuntime = File(AppSettings.runtime.workingDir)
        if (tempRuntime.exists()) {
            tempRuntime.deleteRecursively()
        }
    }
}

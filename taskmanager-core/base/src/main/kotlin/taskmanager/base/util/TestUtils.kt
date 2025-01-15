/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package taskmanager.base.util

import io.ktor.server.config.*
import taskmanager.base.settings.AppSettings
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
     * Tears down the testing environment.
     */
    public fun tearDown() {
        val tempRuntime = File(AppSettings.runtime.workingDir)
        if (tempRuntime.exists()) {
            tempRuntime.deleteRecursively()
        }
    }
}

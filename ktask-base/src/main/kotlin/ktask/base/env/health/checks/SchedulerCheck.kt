/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.env.health.checks

import kotlinx.serialization.Serializable
import ktask.base.env.health.annotation.HealthCheckAPI
import ktask.base.scheduler.service.core.SchedulerService
import ktask.base.settings.AppSettings

/**
 * Used to check the health of the scheduler.
 *
 * @property errors The list of errors that occurred during the check.
 * @property isStarted Whether the scheduler is started.
 * @property isPaused Whether the scheduler is paused.
 * @property totalTasks The total number of tasks in the scheduler.
 * @property email The email specification for the scheduler.
 *
 */
@HealthCheckAPI
@Serializable
data class SchedulerCheck(
    val errors: MutableList<String>,
    val isStarted: Boolean,
    val isPaused: Boolean,
    val totalTasks: Int,
    val email: EmailSpec,
    val templatesPath: String
) {
    constructor() : this(
        errors = mutableListOf(),
        isStarted = SchedulerService.isStarted(),
        isPaused = SchedulerService.isPaused(),
        totalTasks = SchedulerService.totalTasks(),
        email = EmailSpec(),
        templatesPath = AppSettings.scheduler.templatesPath
    ) {
        if (!isStarted) {
            errors.add("${this::class.simpleName}. Scheduler is not started.")
        }

        if (email.hostName.isBlank()) {
            errors.add("${this::class.simpleName}. Email host name is empty.")
        }

        if (email.smtpPort <= 0) {
            errors.add("${this::class.simpleName}. Email SMTP port is invalid.")
        }

        if (templatesPath.isBlank()) {
            errors.add("${this::class.simpleName}. Templates path is empty.")
        }
    }

    /**
     * Contains settings related to email communication.
     *
     * @property hostName The hostname of the outgoing mail server.
     * @property smtpPort The non-SSL port number of the outgoing mail server.
     * @property isSSLOnConnect Whether SSL/TLS encryption should be enabled for the SMTP transport upon connection (SMTP/POP).
     */
    @Serializable
    data class EmailSpec(
        val hostName: String = AppSettings.scheduler.emailSpec.hostName,
        val smtpPort: Int = AppSettings.scheduler.emailSpec.smtpPort,
        val isSSLOnConnect: Boolean = AppSettings.scheduler.emailSpec.isSSLOnConnect,
    )
}

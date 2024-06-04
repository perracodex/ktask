/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.env.health.checks

import io.ktor.server.application.*
import kotlinx.serialization.Serializable
import ktask.base.env.EnvironmentType
import ktask.base.env.health.annotation.HealthCheckAPI
import ktask.base.settings.AppSettings
import ktask.base.utils.DateTimeUtils
import ktask.base.utils.KLocalDateTime

/**
 * Used to check the runtime configuration of the application.
 *
 * @property errors List of errors found during the health check.
 * @property machineId The unique identifier of the machine running the application.
 * @property environment The [EnvironmentType] the application is running in.
 * @property developmentModeEnabled Flag indicating if development mode is enabled.
 * @property utc The current UTC timestamp.
 * @property local The current local timestamp.
 */
@HealthCheckAPI
@Serializable
data class RuntimeCheck(
    val errors: MutableList<String>,
    val machineId: Int,
    val environment: EnvironmentType,
    val developmentModeEnabled: Boolean,
    val utc: KLocalDateTime,
    val local: KLocalDateTime,
) {
    constructor(call: ApplicationCall?) : this(
        errors = mutableListOf(),
        machineId = AppSettings.runtime.machineId,
        environment = AppSettings.runtime.environment,
        developmentModeEnabled = call?.application?.developmentMode ?: false,
        utc = timestamp,
        local = DateTimeUtils.utcToLocal(utc = timestamp),
    )

    init {
        val className: String? = this::class.simpleName

        if (machineId == 0) {
            errors.add("$className. Machine ID is not set.")
        }

        if (environment == EnvironmentType.PROD && developmentModeEnabled) {
            errors.add("$className. Development mode flag enabled in ${environment}.")
        }

        val utcToLocal: KLocalDateTime = DateTimeUtils.utcToLocal(utc = utc)
        if (utcToLocal != local) {
            errors.add("$className. Runtime UTC and Local mismatch. UTC: $utc, Local: $local, UTC to Local: $utcToLocal.")
        }
    }

    companion object {
        /** The current UTC timestamp. */
        private val timestamp: KLocalDateTime = DateTimeUtils.currentUTCDateTime()
    }
}

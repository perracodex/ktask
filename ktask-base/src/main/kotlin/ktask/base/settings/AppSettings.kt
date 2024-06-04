/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.settings

import io.ktor.server.config.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import ktask.base.env.Tracer
import ktask.base.settings.annotation.ConfigurationAPI
import ktask.base.settings.config.ConfigurationCatalog
import ktask.base.settings.config.parser.ConfigClassMap
import ktask.base.settings.config.parser.ConfigurationParser
import ktask.base.settings.config.parser.IConfigSection
import ktask.base.settings.config.sections.*
import ktask.base.settings.config.sections.security.SecuritySettings
import kotlin.system.measureTimeMillis

/**
 * Singleton providing the configuration settings throughout the application.
 *
 * This class serves as the central point for accessing all configuration settings in a type-safe manner.
 */
object AppSettings {
    @Volatile
    private lateinit var configuration: ConfigurationCatalog

    /** The API schema settings. */
    val apiSchema: ApiSchemaSettings get() = configuration.apiSchema

    /** The CORS settings. */
    val cors: CorsSettings get() = configuration.cors

    /** The database settings. */
    val database: DatabaseSettings get() = configuration.database

    /** The email settings. */
    val email: EmailSettings get() = configuration.email

    /** The deployment settings. */
    val deployment: DeploymentSettings get() = configuration.deployment

    /** The runtime settings. */
    val runtime: RuntimeSettings get() = configuration.runtime

    /** The application security settings. */
    val security: SecuritySettings get() = configuration.security

    /** The Slack settings. */
    val slack: SlackSettings get() = configuration.slack

    /**
     * Loads the application settings from the provided [ApplicationConfig].
     * Will only load the settings once.
     *
     * @param applicationConfig The [ApplicationConfig] to load the settings from.
     */
    @OptIn(ConfigurationAPI::class)
    fun load(applicationConfig: ApplicationConfig) {
        if (AppSettings::configuration.isInitialized)
            return

        val tracer = Tracer(ref = ::load)
        tracer.info("Loading application settings.")

        val timeTaken = measureTimeMillis {
            // Map connecting configuration paths.
            // Where the first argument is the path to the configuration section,
            // the second argument is the name of the constructor argument in the
            // ConfigurationCatalog class, and the third argument is the data class
            // that will be instantiated with the configuration values.
            val configMappings: List<ConfigClassMap<out IConfigSection>> = listOf(
                ConfigClassMap(mappingName = "apiSchema", path = "apiSchema", kClass = ApiSchemaSettings::class),
                ConfigClassMap(mappingName = "cors", path = "cors", kClass = CorsSettings::class),
                ConfigClassMap(mappingName = "database", path = "database", kClass = DatabaseSettings::class),
                ConfigClassMap(mappingName = "email", path = "email", kClass = EmailSettings::class),
                ConfigClassMap(mappingName = "deployment", path = "ktor.deployment", kClass = DeploymentSettings::class),
                ConfigClassMap(mappingName = "runtime", path = "runtime", kClass = RuntimeSettings::class),
                ConfigClassMap(mappingName = "security", path = "security", kClass = SecuritySettings::class),
                ConfigClassMap(mappingName = "slack", path = "slack", kClass = SlackSettings::class)
            )

            runBlocking {
                configuration = ConfigurationParser.parse(
                    configuration = applicationConfig,
                    configMappings = configMappings
                )
            }
        }

        tracer.info("Application settings loaded. Time taken: $timeTaken ms.")
    }

    /**
     * Serializes the current settings configuration to a JSON string.
     * The [load] method must be called before this method.
     *
     * @return The JSON string representation of the current settings.
     */
    fun serialize(): String {
        return Json.encodeToString(
            serializer = ConfigurationCatalog.serializer(),
            value = configuration
        )
    }

    /**
     * Deserializes the provided JSON string into the current settings.
     *
     * @param jsonString The JSON string to deserialize.
     */
    fun deserialize(jsonString: String) {
        configuration = Json.decodeFromString(
            deserializer = ConfigurationCatalog.serializer(),
            string = jsonString
        )
    }
}

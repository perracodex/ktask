/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.settings.config.sections

import kotlinx.serialization.Serializable
import ktask.base.env.EnvironmentType
import ktask.base.settings.config.parser.IConfigSection

/**
 * Contains settings related to Swagger and OpenAPI.
 *
 * @property environments The list of environments under which the documentation is enabled.
 * @property schemaRoot The root endpoint path for API schema documentation.
 * @property schemaResourceFile The OpenAPI resource documentation file.
 * @property openApiEndpoint The endpoint to the OpenAPI specification.
 * @property swaggerEndpoint The endpoint to the Swagger UI.
 * @property redocEndpoint The endpoint path to the Redoc UI.
 */
@Serializable
public data class ApiSchemaSettings(
    val environments: List<EnvironmentType>,
    val schemaRoot: String,
    val schemaResourceFile: String,
    val openApiEndpoint: String,
    val swaggerEndpoint: String,
    val redocEndpoint: String
) : IConfigSection

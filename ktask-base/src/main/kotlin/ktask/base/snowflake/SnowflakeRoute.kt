/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.snowflake

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Defines the snowflake route, which is used to parse snowflake IDs.
 */
public fun Route.snowflakeRoute() {

    // Snowflake parser to read back the components of a snowflake ID.
    get("/snowflake/{id}") {
        val data: SnowflakeData = SnowflakeFactory.parse(id = call.parameters["id"]!!)
        call.respond(status = HttpStatusCode.OK, message = data)
    }
}

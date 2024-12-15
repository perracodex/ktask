/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import ktask.base.env.Tracer
import ktask.base.error.AppException
import ktask.base.error.CompositeAppException
import ktask.base.error.ErrorUtils
import ktask.base.error.respondError
import org.jetbrains.exposed.exceptions.ExposedSQLException

/**
 * Install the [StatusPages] feature for handling HTTP status codes.
 *
 * The [StatusPages] plugin allows Ktor applications to respond appropriately
 * to any failure state based on a thrown exception or status code.
 *
 * #### References
 * - [StatusPages Plugin](https://ktor.io/docs/server-status-pages.html)
 */
public fun Application.configureStatusPages() {
    val tracer = Tracer<Application>()

    install(plugin = StatusPages) {
        // Custom application exceptions.
        exception<AppException> { call: ApplicationCall, cause ->
            tracer.error(message = cause.messageDetail(), cause = cause)
            call.respondError(cause = cause)
        }
        exception<CompositeAppException> { call, cause ->
            tracer.error(message = cause.messageDetail(), cause = cause)
            call.respondError(cause = cause)
        }

        // Security exception handling.
        status(HttpStatusCode.MethodNotAllowed) { call: ApplicationCall, status: HttpStatusCode ->
            call.respond(status = HttpStatusCode.MethodNotAllowed, message = "$status")
        }

        // Handle database exceptions.
        exception<ExposedSQLException> { call: ApplicationCall, cause: ExposedSQLException ->
            tracer.error(message = cause.message, cause = cause)
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = "Unexpected database error. Code: ${cause.errorCode}"
            )
        }

        // Bad request exception handling.
        exception<BadRequestException> { call: ApplicationCall, cause: Throwable ->
            tracer.error(message = cause.message, cause = cause)
            val message: String = ErrorUtils.summarizeCause(cause = cause)
            call.respond(status = HttpStatusCode.BadRequest, message = message)
        }

        // Additional exception handling.
        exception<IllegalArgumentException> { call: ApplicationCall, cause: Throwable ->
            tracer.error(message = cause.message, cause = cause)
            val message: String = ErrorUtils.summarizeCause(cause = cause)
            call.respond(status = HttpStatusCode.BadRequest, message = message)
        }
        exception<NotFoundException> { call: ApplicationCall, cause: Throwable ->
            tracer.error(message = cause.message, cause = cause)
            val message: String = ErrorUtils.summarizeCause(cause = cause)
            call.respond(status = HttpStatusCode.NotFound, message = message)
        }
        exception<Throwable> { call: ApplicationCall, cause: Throwable ->
            tracer.error(message = cause.message, cause = cause)
            call.respond(status = HttpStatusCode.InternalServerError, message = HttpStatusCode.InternalServerError.description)
        }
    }
}

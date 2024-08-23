/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.errors

import io.ktor.http.*
import java.util.*

/**
 * Concrete system errors.
 *
 * @property status The [HttpStatusCode] associated with this error.
 * @property code A unique code identifying the type of error.
 * @property description A human-readable description of the error.
 * @property reason An optional human-readable reason for the exception, providing more context.
 * @property cause The underlying cause of the exception, if any.
 */
sealed class SystemError(
    status: HttpStatusCode,
    code: String,
    description: String,
    reason: String? = null,
    cause: Throwable? = null
) : AppException(status = status, code = code, description = description, reason = reason, cause = cause) {

    /**
     * Error for when an email has an invalid format.
     *
     * @param id The affected source id.
     * @param email The email that is already registered.
     * @param reason An optional human-readable reason for the exception, providing more context.
     * @param cause The underlying cause of the exception, if any.
     */
    class InvalidEmailFormat(id: UUID?, email: String, reason: String? = null, cause: Throwable? = null) : SystemError(
        status = HttpStatusCode.BadRequest,
        code = "${TAG}IEF",
        description = "Invalid email format: '$email'. Id: $id",
        reason = reason,
        cause = cause
    )

    /**
     * Error for when a phone has an invalid format.
     *
     * @param id The affected source id.
     * @param phone The phone value with the invalid format.
     * @param reason An optional human-readable reason for the exception, providing more context.
     * @param cause The underlying cause of the exception, if any.
     */
    class InvalidPhoneFormat(id: UUID?, phone: String, reason: String? = null, cause: Throwable? = null) : SystemError(
        status = HttpStatusCode.BadRequest,
        code = "${TAG}IPF",
        description = "Invalid phone format: '$phone'. Id: $id",
        reason = reason,
        cause = cause
    )

    companion object {
        private const val TAG: String = "SYS."

        init {
            ErrorCodeRegistry.registerTag(tag = TAG)
        }
    }
}

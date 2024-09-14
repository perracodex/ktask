/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.base.errors

import io.ktor.http.*
import kotlin.uuid.Uuid

/**
 * Concrete system errors.
 *
 * @property statusCode The [HttpStatusCode] associated with this error.
 * @property errorCode A unique code identifying the type of error.
 * @property description A human-readable description of the error.
 * @property reason An optional human-readable reason for the exception, providing more context.
 * @property cause The underlying cause of the exception, if any.
 */
public sealed class SystemError(
    statusCode: HttpStatusCode,
    errorCode: String,
    description: String,
    reason: String? = null,
    cause: Throwable? = null
) : AppException(
    statusCode = statusCode,
    errorCode = errorCode,
    context = "SYSTEM",
    description = description,
    reason = reason,
    cause = cause
) {
    /**
     * Error for when an email has an invalid format.
     *
     * @param id The affected source id.
     * @param email The email that is already registered.
     */
    public class InvalidEmailFormat(id: Uuid?, email: String, reason: String? = null, cause: Throwable? = null) : SystemError(
        statusCode = STATUS_CODE,
        errorCode = ERROR_CODE,
        description = "Invalid email format: '$email'. Id: $id",
        reason = reason,
        cause = cause
    ) {
        public companion object {
            /** The [HttpStatusCode] associated with this error. */
            public val STATUS_CODE: HttpStatusCode = HttpStatusCode.BadRequest

            /** A unique code identifying the type of error. */
            public const val ERROR_CODE: String = "INVALID_EMAIL_FORMAT"
        }
    }

    /**
     * Error for when a phone has an invalid format.
     *
     * @param id The affected source id.
     * @param phone The phone value with the invalid format.
     */
    public class InvalidPhoneFormat(id: Uuid?, phone: String, reason: String? = null, cause: Throwable? = null) : SystemError(
        statusCode = STATUS_CODE,
        errorCode = ERROR_CODE,
        description = "Invalid phone format: '$phone'. Id: $id",
        reason = reason,
        cause = cause
    ) {
        public companion object {
            /** The [HttpStatusCode] associated with this error. */
            public val STATUS_CODE: HttpStatusCode = HttpStatusCode.BadRequest

            /** A unique code identifying the type of error. */
            public const val ERROR_CODE: String = "INVALID_PHONE_FORMAT"
        }
    }
}

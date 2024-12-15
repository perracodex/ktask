/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.error

import io.ktor.http.*
import ktask.base.error.AppException
import kotlin.uuid.Uuid

/**
 * Concrete errors for the notifications domain.
 *
 * @param statusCode The [HttpStatusCode] associated with this error.
 * @param errorCode A unique code identifying the type of error.
 * @param description A human-readable description of the error.
 * @param field Optional field identifier, typically the input field that caused the error.
 * @param reason Optional human-readable reason for the exception, providing more context.
 * @param cause Optional underlying cause of the exception, if any.
 */
internal sealed class NotificationError(
    statusCode: HttpStatusCode,
    errorCode: String,
    description: String,
    field: String? = null,
    reason: String? = null,
    cause: Throwable? = null
) : AppException(
    statusCode = statusCode,
    errorCode = errorCode,
    context = "NOTIFICATION",
    description = description,
    field = field,
    reason = reason,
    cause = cause
) {
    /**
     * Error for when an email invalid.
     *
     * @param groupId The affected source group id.
     * @param email The invalid email.
     * @param field Optional field identifier, typically the input field that caused the error.
     * @param reason Optional human-readable reason for the exception, providing more context.
     * @param cause Optional underlying cause of the exception, if any.
     */
    class InvalidEmail(
        groupId: Uuid?,
        email: String,
        field: String? = null,
        reason: String? = null,
        cause: Throwable? = null
    ) : NotificationError(
        statusCode = STATUS_CODE,
        errorCode = ERROR_CODE,
        description = "Invalid email: '$email'. Group Id: $groupId",
        field = field,
        reason = reason,
        cause = cause
    ) {
        companion object {
            val STATUS_CODE: HttpStatusCode = HttpStatusCode.BadRequest
            const val ERROR_CODE: String = "INVALID_EMAIL"
        }
    }

    /**
     * Error for when a phone number is invalid, typically due to an incorrect format.
     *
     * @param groupId The affected source group id.
     * @param phone The invalid phone number.
     * @param field Optional field identifier, typically the input field that caused the error.
     * @param reason Optional human-readable reason for the exception, providing more context.
     * @param cause Optional underlying cause of the exception, if any.
     */
    class InvalidPhoneNumber(
        groupId: Uuid?,
        phone: String,
        field: String? = null,
        reason: String? = null,
        cause: Throwable? = null
    ) : NotificationError(
        statusCode = STATUS_CODE,
        errorCode = ERROR_CODE,
        description = "Invalid phone number: '$phone'. Id: $groupId",
        field = field,
        reason = reason,
        cause = cause
    ) {
        companion object {
            val STATUS_CODE: HttpStatusCode = HttpStatusCode.BadRequest
            const val ERROR_CODE: String = "INVALID_PHONE_NUMBER"
        }
    }
}

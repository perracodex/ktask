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
 */
sealed class SystemError(
    status: HttpStatusCode,
    code: String,
    description: String
) : BaseError(status = status, code = code, description = description) {

    /**
     * Error for when an email has an invalid format.
     *
     * @property id The affected source id.
     * @property email The email that is already registered.
     */
    data class InvalidEmailFormat(val id: UUID?, val email: String) : SystemError(
        status = HttpStatusCode.BadRequest,
        code = "${TAG}IEF",
        description = "Invalid email format: '$email'. Id: $id"
    )

    /**
     * Error for when a phone has an invalid format.
     *
     * @property id The affected source id.
     * @property phone The phone value with the invalid format.
     */
    data class InvalidPhoneFormat(val id: UUID?, val phone: String) : SystemError(
        status = HttpStatusCode.BadRequest,
        code = "${TAG}IPF",
        description = "Invalid phone format: '$phone'. Id: $id"
    )

    companion object {
        private const val TAG: String = "SYS."

        init {
            ErrorCodeRegistry.registerTag(tag = TAG)
        }
    }
}

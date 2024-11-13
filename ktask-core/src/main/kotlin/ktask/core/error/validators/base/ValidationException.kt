/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.core.error.validators.base

import ktask.core.error.validators.EmailValidator
import ktask.core.error.validators.PhoneValidator

/**
 * Custom exception class to represent validation errors.
 * This exception should be thrown when validation of a value fails.
 *
 * @property code A unique code identifying the type of validation error.
 * @param message The detail message describing the validation failure.
 * @param cause Optional underlying reason for this [ValidationException].
 *
 * @see [IValidator]
 * @see [EmailValidator]
 * @see [PhoneValidator]
 */
public class ValidationException(
    public val code: String,
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.core.settings.catalog.section.security.node

import kotlinx.serialization.Serializable

/**
 * Security constraints settings.
 *
 * @property privateApi Rate limit specification for the Private API endpoints.
 */
public data class ConstraintsSettings(
    val privateApi: LimitSpec,
) {

    /**
     * Rate limit specification.
     * For example, a limit of 10 requests per second would be represented as:
     * ```
     * LimitSpec(limit = 10, refillMs = 1000)
     * ```
     *
     * @property limit The maximum number of requests allowed within the refill period. Must be > 0.
     * @property refillMs The time period in milliseconds after which the limit is reset. Must be > 0.
     */
    @Serializable
    public data class LimitSpec(
        val limit: Int,
        val refillMs: Long
    ) {
        init {
            require(limit > 0) { "Invalid rate limit. Must be > 0." }
            require(refillMs > 0L) { "Invalid rate refill. Must be > 0." }
        }
    }
}

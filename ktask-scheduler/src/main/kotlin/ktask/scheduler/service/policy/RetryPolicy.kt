/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.scheduler.service.policy

import kotlin.time.Duration.Companion.seconds

/**
 * Data class representing the retry policy for a task.
 *
 * @property maxRetries The maximum number of retries allowed for the task.
 * @property backoffStrategy The backoff strategy to use for retrying the task.
 */
public data class RetryPolicy(
    val maxRetries: Int = 3,
    val backoffStrategy: BackoffStrategy = BackoffStrategy.Exponential(
        initialDelay = 10.seconds,
        multiplier = 2.0
    )
) {
    internal companion object {
        const val COUNT_KEY: String = "#RETRY_COUNT_KEY#"
        const val MAX_RETRIES_KEY: String = "#RETRY_MAX_RETRIES_KEY#"
        const val BACKOFF_TYPE_KEY: String = "#RETRY_BACKOFF_TYPE_KEY#"
        const val DELAY_MILLIS_KEY: String = "#RETRY_DELAY_MILLIS_KEY#"
        const val INITIAL_DELAY_MILLIS_KEY: String = "#RETRY_INITIAL_DELAY_MILLIS_KEY#"
        const val MULTIPLIER_KEY: String = "#RETRY_MULTIPLIER_KEY#"
    }
}

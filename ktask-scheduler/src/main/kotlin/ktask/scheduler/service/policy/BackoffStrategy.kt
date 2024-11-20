/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.scheduler.service.policy

import kotlin.time.Duration

/**
 * Sealed class representing the different backoff strategies for retrying a task.
 */
public sealed class BackoffStrategy {
    /**
     * Data class representing a fixed delay for retrying the task.
     *
     * @property delay The fixed delay for retrying the task.
     */
    public data class Fixed(val delay: Duration) : BackoffStrategy()

    /**
     * Data class representing an exponential backoff strategy for retrying the task.
     *
     * @property initialDelay The initial delay for retrying the task.
     * @property multiplier The multiplier for the exponential backoff.
     */
    public data class Exponential(val initialDelay: Duration, val multiplier: Double) : BackoffStrategy()

    internal companion object {
        const val FIXED_KEY: String = "#BACKOFF_STRATEGY_FIXED_KEY#"
        const val EXPONENTIAL_KEY: String = "#BACKOFF_STRATEGY_EXPONENTIAL_KEY#"
    }
}

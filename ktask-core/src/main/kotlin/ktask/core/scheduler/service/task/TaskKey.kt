/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.core.scheduler.service.task

import kotlinx.serialization.Serializable
import ktask.core.scheduler.service.annotation.SchedulerApi
import org.quartz.JobKey

/**
 * Represents a key that uniquely identifies a task in the scheduler.
 *
 * @property groupId The group to which the task belongs.
 * @property taskId The unique identifier of the task.
 */
@Serializable
public data class TaskKey(
    val groupId: String,
    val taskId: String,
) {
    internal companion object {
        /**
         * Creates a [TaskKey] from a Quartz [JobKey].
         *
         * @param jobKey The Quartz [JobKey].
         * @return The [TaskKey] instance.
         */
        @SchedulerApi
        fun fromJobKey(jobKey: JobKey): TaskKey {
            return TaskKey(groupId = jobKey.group, taskId = jobKey.name)
        }
    }
}

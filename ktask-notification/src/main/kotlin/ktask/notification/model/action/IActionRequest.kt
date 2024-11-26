/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.model.action

import ktask.core.serializer.NoBlankString
import ktask.core.serializer.Uuid
import ktask.notification.consumer.action.AbsActionConsumer
import ktask.scheduler.service.schedule.Schedule

/**
 * Base interface for all action based requests.
 *
 * @property groupId The group ID of the task.
 * @property description The description of the task.
 * @property replace Whether to replace the task if it already exists.
 * @property schedule Optional [Schedule] for the task.
 */
public interface IActionRequest {
    public val groupId: Uuid
    public val description: NoBlankString
    public val replace: Boolean
    public val schedule: Schedule?

    /**
     * Verifies the integrity of the task request.
     */
    public fun verify() {
        require(description.isNotBlank()) { "Description must not be blank." }
    }

    /**
     * Converts the action request into a map of parameters suitable for task processing.
     *
     * Subclasses should override this method to include additional type-specific parameters.
     */
    public fun toMap(taskId: String): MutableMap<String, Any?> {
        return mutableMapOf(
            AbsActionConsumer.Property.GROUP_ID.key to groupId,
            AbsActionConsumer.Property.TASK_ID.key to taskId,
            AbsActionConsumer.Property.DESCRIPTION.key to description,
        )
    }
}

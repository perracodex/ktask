/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.model.action

import ktask.core.persistence.serializers.Uuid
import ktask.core.scheduler.service.schedule.Schedule
import ktask.notification.consumer.action.AbsActionConsumer

/**
 * Base interface for all action based requests.
 *
 * @property id The unique identifier of the task request.
 * @property description Optional description of the task.
 * @property schedule Optional [Schedule] for the task.
 */
public interface IActionRequest {
    public val id: Uuid
    public val description: String?
    public val schedule: Schedule?

    /**
     * Converts the action request into a map of parameters suitable for task processing.
     *
     * Subclasses should override this method to include additional type-specific parameters.
     */
    public fun toMap(taskName: String): MutableMap<String, Any?> {
        return mutableMapOf(
            AbsActionConsumer.Property.TASK_GROUP_ID.key to id,
            AbsActionConsumer.Property.TASK_NAME.key to taskName,
            AbsActionConsumer.Property.DESCRIPTION.key to description,
        )
    }
}

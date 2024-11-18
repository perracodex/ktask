/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.model.action.request

import kotlinx.serialization.Serializable
import ktask.core.persistence.serializer.NoBlankString
import ktask.core.persistence.serializer.Uuid
import ktask.core.scheduler.service.schedule.Schedule
import ktask.notification.consumer.action.task.ActionConsumer
import ktask.notification.model.action.IActionRequest

/**
 * Represents a custom action task request.
 *
 * @property groupId The group ID of the task.
 * @property description The description of the task.
 * @property replace Whether to replace the task if it already exists.
 * @property schedule Optional [Schedule] for the task.
 * @property data Some custom data to be used in the action.
 */
@Serializable
public data class ActionRequest(
    override val groupId: Uuid,
    override val description: String,
    override val replace: Boolean,
    override val schedule: Schedule? = null,
    val data: NoBlankString,
) : IActionRequest {

    init {
        verify()
    }

    override fun toMap(taskId: String): MutableMap<String, Any?> {
        return super.toMap(taskId = taskId).apply {
            this[ActionConsumer.Property.DATA.key] = data
        }
    }
}

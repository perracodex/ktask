/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.model.action.request

import kotlinx.serialization.Serializable
import ktask.base.serializer.NoBlankString
import ktask.notification.consumer.action.task.ActionConsumer
import ktask.notification.model.action.IActionRequest
import ktask.scheduler.scheduling.ScheduleType
import kotlin.uuid.Uuid

/**
 * Represents a custom action task request.
 *
 * @property groupId The group ID of the task.
 * @property description The description of the task.
 * @property replace Whether to replace the task if it already exists.
 * @property scheduleType Optional [ScheduleType] for the task.
 * @property data Some custom data to be used in the action.
 */
@Serializable
public data class ActionRequest internal constructor(
    override val groupId: Uuid,
    override val description: String,
    override val replace: Boolean,
    override val scheduleType: ScheduleType? = null,
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

/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.entity.action.request

import kotlinx.serialization.Serializable
import ktask.base.persistence.serializers.SUUID
import ktask.base.scheduler.service.schedule.Schedule
import ktask.server.consumer.action.task.ActionConsumer
import ktask.server.entity.action.IActionRequest

/**
 * Represents a custom action task request.
 *
 * @property id The unique identifier of the task request.
 * @property description Optional description of the task.
 * @property schedule Optional [Schedule] for the task.
 * @property data Some custom data to be used in the action.
 */
@Serializable
data class ActionRequest(
    override val id: SUUID,
    override val description: String? = null,
    override val schedule: Schedule? = null,
    val data: String,
) : IActionRequest {

    override fun toMap(): MutableMap<String, Any?> {
        return super.toMap().apply {
            this[ActionConsumer.Property.DATA.key] = data
        }
    }
}

/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.entity.action.request

import kotlinx.serialization.Serializable
import ktask.base.persistence.serializers.SUUID
import ktask.base.scheduler.service.schedule.Schedule
import ktask.server.consumer.action.task.ActionConsumer
import ktask.server.domain.entity.action.IActionRequest

/**
 * Represents a custom action task request.
 *
 * @property id The unique identifier of the task request.
 * @property schedule Optional [Schedule] for the task.
 * @property data Some custom data to be used in the action.
 */
@Serializable
data class ActionRequest(
    override val id: SUUID,
    override val schedule: Schedule? = null,
    val data: String,
) : IActionRequest {

    override fun toTaskParameters(): MutableMap<String, Any> {
        return super.toTaskParameters().also { parameter ->
            parameter[ActionConsumer.Property.DATA.key] = data
        }
    }
}

/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.model.action.request

import kotlinx.serialization.Serializable
import ktask.base.persistence.serializers.UuidS
import ktask.base.scheduler.service.schedule.Schedule
import ktask.notification.consumer.action.task.ActionConsumer
import ktask.notification.model.action.IActionRequest

/**
 * Represents a custom action task request.
 *
 * @property id The unique identifier of the task request.
 * @property description Optional description of the task.
 * @property schedule Optional [Schedule] for the task.
 * @property data Some custom data to be used in the action.
 */
@Serializable
public data class ActionRequest(
    override val id: UuidS,
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

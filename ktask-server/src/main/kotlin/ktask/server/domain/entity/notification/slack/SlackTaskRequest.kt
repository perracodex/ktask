/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.entity.notification.slack

import kotlinx.serialization.Serializable
import ktask.base.persistence.serializers.SUUID
import ktask.base.scheduler.service.schedule.Schedule
import ktask.server.domain.entity.ITaskRequest
import ktask.server.domain.entity.Recipient
import ktask.server.domain.service.consumer.AbsTaskConsumer

/**
 * Represents a request to send a Slack notification task.
 *
 * @property id The unique identifier of the task request.
 * @property schedule Optional [Schedule] for the task.
 * @property recipients List of target recipients.
 * @property params The [SlackParams] for the notification.
 */
@Serializable
data class SlackTaskRequest(
    override val id: SUUID,
    override val schedule: Schedule? = null,
    override val recipients: List<Recipient>,
    val params: SlackParams
) : ITaskRequest {
    init {
        require(recipients.isNotEmpty()) { "At least one recipient must be specified." }
    }

    override fun toTaskParameters(recipient: Recipient): MutableMap<String, Any> {
        return super.toTaskParameters(recipient = recipient).also { parameter ->
            parameter[AbsTaskConsumer.PARAMETERS_KEY] = params.serialize()
        }
    }
}

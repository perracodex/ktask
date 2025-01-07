/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.model.message.request

import kotlinx.serialization.Serializable
import ktask.base.serializer.NoBlankString
import ktask.notification.consumer.message.task.SlackConsumer
import ktask.notification.model.message.IMessageRequest
import ktask.notification.model.message.Recipient
import ktask.scheduler.scheduling.ScheduleType
import kotlin.uuid.Uuid

/**
 * Represents a request to send a Slack notification task.
 *
 * @property groupId The group ID of the task.
 * @property description The description of the task.
 * @property replace Whether to replace the task if it already exists.
 * @property scheduleType Optional [ScheduleType] for the task.
 * @property recipients List of [Recipient] targets.
 * @property template The template to be used for the notification.
 * @property fields Optional fields to be included in the template.
 * @property channel The channel to send the notification to.
 */
@Serializable
public data class SlackRequest internal constructor(
    override val groupId: Uuid,
    override val description: NoBlankString,
    override val replace: Boolean,
    override val scheduleType: ScheduleType? = null,
    override val recipients: List<Recipient>,
    override val template: NoBlankString,
    override val fields: Map<NoBlankString, NoBlankString>? = null,
    val channel: NoBlankString,
) : IMessageRequest {

    init {
        verify()
    }

    override fun toMap(taskId: String, recipient: Recipient): MutableMap<String, Any?> {
        return super.toMap(taskId = taskId, recipient = recipient).apply {
            this[SlackConsumer.Property.CHANNEL.key] = channel
        }
    }
}

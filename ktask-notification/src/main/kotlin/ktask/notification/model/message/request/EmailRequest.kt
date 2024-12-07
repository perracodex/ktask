/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.model.message.request

import kotlinx.serialization.Serializable
import ktask.core.error.validator.EmailValidator
import ktask.core.serializer.NoBlankString
import ktask.core.serializer.Uuid
import ktask.notification.consumer.message.task.EmailConsumer
import ktask.notification.error.NotificationError
import ktask.notification.model.message.IMessageRequest
import ktask.notification.model.message.Recipient
import ktask.scheduler.task.schedule.Schedule

/**
 * Represents a request to send an Email notification task.
 *
 * @property groupId The group ID of the task.
 * @property replace Whether to replace the task if it already exists.
 * @property description The description of the task.
 * @property schedule Optional [Schedule] for the task.
 * @property recipients List of target recipients.
 * @property template The template to be used for the notification.
 * @property fields Optional fields to be included in the template.
 * @property cc List of recipients to be copied on the notification.
 * @property subject The subject or title of the notification.
 */
@Serializable
public data class EmailRequest internal constructor(
    override val groupId: Uuid,
    override val description: NoBlankString,
    override val replace: Boolean,
    override val schedule: Schedule? = null,
    override val recipients: List<Recipient>,
    override val template: NoBlankString,
    override val fields: Map<NoBlankString, NoBlankString>? = null,
    val cc: List<NoBlankString> = emptyList(),
    val subject: NoBlankString,
) : IMessageRequest {

    init {
        verify()
    }

    override fun toMap(taskId: String, recipient: Recipient): MutableMap<String, Any?> {
        return super.toMap(taskId = taskId, recipient = recipient).apply {
            this[EmailConsumer.Property.CC.key] = cc
            this[EmailConsumer.Property.SUBJECT.key] = subject
        }
    }

    internal companion object {
        /**
         * Verifies the recipients of the request.
         *
         * @param request The [EmailRequest] instance to be verified.
         */
        fun verifyRecipients(request: EmailRequest) {
            // This verification of recipients must be done within the scope of a route endpoint,
            // and not in the request dataclass init block, which is actually called before
            // the dataclass reaches the route endpoint scope.
            // This way we can raise a custom error response, as opposed to a generic
            // 400 Bad Request, which would be raised if the validation was done in the init block.
            request.recipients.forEach { recipient ->
                EmailValidator.check(value = recipient.target).onFailure { error ->
                    throw NotificationError.InvalidEmail(groupId = request.groupId, email = recipient.target, cause = error)
                }
            }
        }
    }
}

/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.model.message.request

import kotlinx.serialization.Serializable
import ktask.base.errors.SystemError
import ktask.base.persistence.serializers.SUuid
import ktask.base.persistence.validators.IValidator
import ktask.base.persistence.validators.impl.EmailValidator
import ktask.base.scheduler.service.schedule.Schedule
import ktask.notification.consumer.message.task.EmailConsumer
import ktask.notification.model.message.IMessageRequest
import ktask.notification.model.message.Recipient

/**
 * Represents a request to send an Email notification task.
 *
 * @property id The unique identifier of the task request.
 * @property description Optional description of the task.
 * @property schedule Optional [Schedule] for the task.
 * @property recipients List of target recipients.
 * @property template The template to be used for the notification.
 * @property fields Optional fields to be included in the template.
 * @property cc List of recipients to be copied on the notification.
 * @property subject The subject or title of the notification.
 */
@Serializable
public data class EmailRequest(
    override val id: SUuid,
    override val description: String? = null,
    override val schedule: Schedule? = null,
    override val recipients: List<Recipient>,
    override val template: String,
    override val fields: Map<String, String>? = null,
    val cc: List<String> = emptyList(),
    val subject: String,
) : IMessageRequest {

    init {
        verify()
    }

    override fun toMap(recipient: Recipient): MutableMap<String, Any?> {
        return super.toMap(recipient = recipient).apply {
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
                val result: IValidator.Result = EmailValidator.validate(value = recipient.target)
                if (result is IValidator.Result.Failure) {
                    throw SystemError.InvalidEmailFormat(id = request.id, email = recipient.target)
                }
            }
        }
    }
}

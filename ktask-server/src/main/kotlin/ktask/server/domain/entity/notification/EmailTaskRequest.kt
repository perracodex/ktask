/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.entity.notification

import kotlinx.serialization.Serializable
import ktask.base.errors.SystemError
import ktask.base.persistence.serializers.SUUID
import ktask.base.persistence.validators.IValidator
import ktask.base.persistence.validators.impl.EmailValidator
import ktask.base.scheduler.service.schedule.Schedule
import ktask.server.consumer.notification.EmailTaskConsumer
import ktask.server.domain.entity.INotificationTaskRequest
import ktask.server.domain.entity.Recipient

/**
 * Represents a request to send an Email notification task.
 *
 * @property id The unique identifier of the task request.
 * @property schedule Optional [Schedule] for the task.
 * @property recipients List of target recipients.
 * @property template The template to be used for the notification.
 * @property fields Optional fields to be included in the template.
 * @property attachments Optional list of file paths to be attached to the notification.
 * @property cc List of recipients to be copied on the notification.
 * @property subject The subject or title of the notification.
 */
@Serializable
data class EmailTaskRequest(
    override val id: SUUID,
    override val schedule: Schedule? = null,
    override val recipients: List<Recipient>,
    override val template: String,
    override val fields: Map<String, String>? = null,
    override val attachments: List<String>? = null,
    val cc: List<String> = emptyList(),
    val subject: String,
) : INotificationTaskRequest {

    override fun toTaskParameters(recipient: Recipient): MutableMap<String, Any> {
        return super.toTaskParameters(recipient = recipient).also { parameter ->
            parameter[EmailTaskConsumer.Property.CC.key] = cc
            parameter[EmailTaskConsumer.Property.SUBJECT.key] = subject
        }
    }

    companion object {
        /**
         * Verifies the recipients of the request.
         *
         * @param request The [EmailTaskRequest] instance to be verified.
         */
        fun verifyRecipients(request: EmailTaskRequest) {
            // This verification of recipients must be done within the scope of a route endpoint,
            // and not in the request dataclass init block, which is actually called before
            // the dataclass reaches the route endpoint scope.
            // This way we can raise a custom error response, as opposed to a generic
            // 400 Bad Request, which would be raised if the validation was done in the init block.
            request.recipients.forEach { recipient ->
                val result: IValidator.Result = EmailValidator.validate(value = recipient.target)
                if (result is IValidator.Result.Failure) {
                    SystemError.InvalidEmailFormat(id = request.id, email = recipient.target).raise()
                }
            }
        }
    }
}

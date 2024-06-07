/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.entity.notification

import kotlinx.serialization.Serializable
import ktask.base.errors.SystemError
import ktask.base.persistence.serializers.SUUID
import ktask.base.persistence.validators.IValidator
import ktask.base.persistence.validators.impl.EmailValidator
import ktask.base.utils.KLocalDateTime
import ktask.server.domain.entity.ITaskRequest
import ktask.server.domain.service.consumer.notifications.EmailTaskConsumer

/**
 * Represents a request to send an Email notification task.
 *
 * @property id The unique identifier of the task request.
 * @property schedule Optional date/time when the task must be sent. Null to send immediately.
 * @property interval Optional interval in seconds at which the task should repeat. In minutes.
 * @property recipients List of target recipients.
 * @property cc List of recipients to be copied on the email notification.
 * @property subject The subject or title of the email notification.
 * @property message The message or information contained in the email notification.
 * @property asHtml Whether the message is in HTML format, or plain text.
 */
@Serializable
data class EmailTaskRequest(
    override val id: SUUID,
    override val schedule: KLocalDateTime? = null,
    override val interval: UInt? = null,
    override val recipients: List<String>,
    val cc: List<String> = emptyList(),
    val subject: String,
    val message: String,
    val asHtml: Boolean
) : ITaskRequest {
    init {
        require(subject.isNotBlank()) { "Subject cannot be blank." }
        require(message.isNotBlank()) { "Message cannot be blank." }
        require(recipients.isNotEmpty()) { "At least one recipient must be specified." }
    }

    override fun toTaskParameters(recipient: String): MutableMap<String, Any> {
        return super.toTaskParameters(recipient).also {
            it[EmailTaskConsumer.SUBJECT_KEY] = subject
            it[EmailTaskConsumer.MESSAGE_KEY] = message
            it[EmailTaskConsumer.AS_HTML_KEY] = asHtml
            it[EmailTaskConsumer.RECIPIENT_COPY_KEY] = cc
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
            request.recipients.forEach {
                val result: IValidator.Result = EmailValidator.validate(value = it)
                if (result is IValidator.Result.Failure) {
                    SystemError.InvalidEmailFormat(id = request.id, email = it).raise()
                }
            }
        }
    }
}

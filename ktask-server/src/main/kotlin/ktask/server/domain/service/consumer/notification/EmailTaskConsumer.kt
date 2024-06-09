/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.service.consumer.notification

import ktask.base.env.Tracer
import ktask.base.settings.AppSettings
import ktask.base.settings.config.sections.SchedulerSettings
import ktask.server.domain.entity.notification.email.EmailParamsRequest
import ktask.server.domain.service.consumer.AbsTaskConsumer
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.EmailAttachment
import org.apache.commons.mail.HtmlEmail

/**
 * Represents a scheduled task that processes emails.
 */
internal class EmailTaskConsumer : AbsTaskConsumer() {
    private val tracer = Tracer<EmailTaskConsumer>()

    override fun consume(payload: TaskPayload) {
        tracer.debug("Processing email task notification. ID: ${payload.taskId}")

        val parameters: EmailParamsRequest = EmailParamsRequest.deserialize(
            string = payload.additionalParams[PARAMETERS_KEY] as String
        )

        // Build the message.

        val email: HtmlEmail = HtmlEmail().apply {
            val message: String = buildMessage(
                type = TemplateType.EMAIL,
                recipient = payload.recipient,
                template = parameters.template,
                fields = parameters.fields
            )

            setHtmlMsg(message)
            setTextMsg("Your email client does not support HTML messages.")
        }

        // Add recipients to be copied on the email notification.

        if (parameters.cc.isNotEmpty()) {
            email.addCc(*parameters.cc.toTypedArray())
        }

        // Add attachments to the email.

        parameters.attachments?.forEach { attachmentPath ->
            val attachment: EmailAttachment = EmailAttachment().apply {
                path = attachmentPath
                disposition = EmailAttachment.ATTACHMENT
            }

            email.attach(attachment)
        }

        // Configure email settings.

        val emailSpec: SchedulerSettings.EmailSpec = AppSettings.scheduler.emailSpec
        email.hostName = emailSpec.hostName
        email.setSmtpPort(emailSpec.smtpPort)
        email.authenticator = DefaultAuthenticator(emailSpec.username, emailSpec.password)
        email.isSSLOnConnect = emailSpec.isSSLOnConnect
        email.setFrom(emailSpec.username)
        email.addTo(payload.recipient.target)
        email.subject = parameters.subject

        // Send the email.
        val result: String = email.send()
        tracer.debug("Email notification sent to ${payload.recipient.target}. Task ID: ${payload.taskId}. Result: $result")
    }
}

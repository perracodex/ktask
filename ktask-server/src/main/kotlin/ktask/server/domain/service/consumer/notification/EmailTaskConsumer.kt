/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.service.consumer.notification

import ktask.base.env.Tracer
import ktask.base.settings.AppSettings
import ktask.base.settings.config.sections.SchedulerSettings
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

        val cc: List<String> = parameterToStringList(parameter = payload.additionalParams[CC_KEY])
        val subject: String = payload.additionalParams[SUBJECT_KEY] as String

        // Build the message.

        val email: HtmlEmail = HtmlEmail().apply {
            val message: String = buildMessage(
                type = TemplateType.EMAIL,
                payload = payload
            )

            setHtmlMsg(message)
            setTextMsg("Your email client does not support HTML messages.")
        }

        // Add recipients to be copied on the email notification.

        if (cc.isNotEmpty()) {
            email.addCc(*cc.toTypedArray())
        }

        // Add attachments to the email.

        payload.attachments.forEach { attachmentPath ->
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
        email.subject = subject

        // Send the email.
        val result: String = email.send()
        tracer.debug("Email notification sent to ${payload.recipient.target}. Task ID: ${payload.taskId}. Result: $result")
    }

    companion object {
        const val CC_KEY: String = "CC"
        const val SUBJECT_KEY: String = "SUBJECT"
    }
}

/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.consumer.notification

import ktask.base.env.Tracer
import ktask.base.settings.AppSettings
import ktask.base.settings.config.sections.SchedulerSettings
import ktask.base.utils.CastUtils
import ktask.server.consumer.AbsNotificationTaskConsumer
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.EmailAttachment
import org.apache.commons.mail.HtmlEmail

/**
 * Represents a scheduled task that processes emails.
 */
internal class EmailTaskConsumer : AbsNotificationTaskConsumer() {
    private val tracer = Tracer<EmailTaskConsumer>()

    /**
     * Represents the concrete properties for the email task.
     */
    enum class Property(val key: String) {
        CC(key = "CC"),
        SUBJECT(key = "SUBJECT")
    }

    override fun consume(payload: TaskPayload) {
        tracer.debug("Processing email task notification. ID: ${payload.taskId}")

        val cc: List<String> = CastUtils.toStringList(list = payload.additionalParameters[Property.CC.key])
        val subject: String = payload.additionalParameters[Property.SUBJECT.key] as String

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
}

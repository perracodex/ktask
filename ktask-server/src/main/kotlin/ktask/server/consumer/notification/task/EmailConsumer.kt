/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.consumer.notification.task

import ktask.base.env.Tracer
import ktask.base.settings.AppSettings
import ktask.base.settings.config.sections.SchedulerSettings
import ktask.base.utils.CastUtils
import ktask.server.consumer.notification.AbsNotificationConsumer
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.EmailAttachment
import org.apache.commons.mail.HtmlEmail

/**
 * Represents a scheduled task that processes emails.
 */
internal class EmailConsumer : AbsNotificationConsumer() {
    private val tracer = Tracer<EmailConsumer>()

    /**
     * Represents the concrete properties for the email task.
     */
    enum class Property(val key: String) {
        CC(key = "CC"),
        SUBJECT(key = "SUBJECT")
    }

    override fun consume(payload: TaskPayload) {
        tracer.debug("Processing email notification. ID: ${payload.taskId}")

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
        val cc: List<String>? = CastUtils.toStringList(list = payload.additionalParameters[Property.CC.key])
        if (!cc.isNullOrEmpty()) {
            email.addCc(*cc.toTypedArray())
        }

        // Add attachments to the email.
        payload.attachments?.forEach { attachmentPath ->
            val attachment: EmailAttachment = EmailAttachment().apply {
                path = attachmentPath
                disposition = EmailAttachment.ATTACHMENT
            }

            email.attach(attachment)
        }

        // Configure email settings.
        val emailSpec: SchedulerSettings.EmailSpec = AppSettings.scheduler.emailSpec
        email.apply {
            hostName = emailSpec.hostName
            setSmtpPort(emailSpec.smtpPort)
            authenticator = DefaultAuthenticator(emailSpec.username, emailSpec.password)
            isSSLOnConnect = emailSpec.isSSLOnConnect
            setFrom(emailSpec.username)
            addTo(payload.recipient.target)
            subject = payload.additionalParameters[Property.SUBJECT.key] as String
        }

        // Send the email.
        email.send().also { messageId ->
            tracer.debug("Email notification sent: ${payload.recipient.target}. Task ID: ${payload.taskId}. Message ID: $messageId")
        }
    }
}

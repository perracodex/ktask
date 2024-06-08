/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.service.consumer.notifications

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import ktask.base.env.Tracer
import ktask.base.settings.AppSettings
import ktask.base.settings.config.sections.SchedulerSettings
import ktask.server.domain.service.consumer.AbsTaskConsumer
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.Email
import org.apache.commons.mail.HtmlEmail
import org.apache.commons.mail.SimpleEmail

/**
 * Represents a scheduled task that processes emails.
 */
internal class EmailTaskConsumer : AbsTaskConsumer() {
    private val tracer = Tracer<EmailTaskConsumer>()

    override fun consume(payload: TaskPayload) {
        tracer.debug("Processing email task notification. ID: ${payload.taskId}")

        // Build the email message.

        val subject: String = payload.additionalParams[SUBJECT_KEY] as String
        val message: String = payload.additionalParams[MESSAGE_KEY] as String
        val asHtml: Boolean = payload.additionalParams[AS_HTML_KEY] as Boolean

        val email: Email = if (asHtml) {
            HtmlEmail().apply {
                val htmlMessage: String = buildHtmlEmailContent(
                    subject = subject,
                    message = message,
                    recipient = payload.recipient
                )
                setHtmlMsg(htmlMessage)

                // Set also the plain text message as fallback when the email client does not support HTML.
                setTextMsg(message)
            }
        } else {
            SimpleEmail().apply {
                setMsg(message)
            }
        }

        // Add recipients to be copied on the email notification.

        val cc: List<String> = (payload.additionalParams[RECIPIENT_COPY_KEY] as? List<*>)
            ?.filterIsInstance<String>() ?: emptyList()
        if (cc.isNotEmpty()) {
            email.addCc(*cc.toTypedArray())
        }

        // Configure email settings.

        val emailSpec: SchedulerSettings.EmailSpec = AppSettings.scheduler.emailSpec
        email.hostName = emailSpec.hostName
        email.setSmtpPort(emailSpec.smtpPort)
        email.authenticator = DefaultAuthenticator(emailSpec.username, emailSpec.password)
        email.isSSLOnConnect = emailSpec.isSSLOnConnect
        email.setFrom(emailSpec.username)
        email.addTo(payload.recipient)
        email.subject = subject

        // Send the email notification.
        val result: String = email.send()
        tracer.debug("Email notification sent to ${payload.recipient}. Result: $result")
    }

    /**
     * Builds the HTML content for the email message using HTML DSL.
     * Other libraries like Thymeleaf or FreeMarker can be used for more complex email templates.
     *
     * @param subject The subject or title of the email notification.
     * @param message The message or information contained in the email notification.
     * @param recipient The intended recipient of the email notification.
     * @return The HTML content of the email message.
     */
    private fun buildHtmlEmailContent(subject: String, message: String, recipient: String): String {
        return buildString {
            appendHTML().html {
                head {
                    title { +subject }
                }
                body {
                    h1 { +"Hello, $recipient!" }
                    p { +message }
                    p {
                        +"Best regards,"
                        br
                        +"Your Company"
                    }
                }
            }
        }
    }

    companion object {
        /** The key for the email subject in the task parameters. */
        const val SUBJECT_KEY: String = "SUBJECT"

        /** The key for the email message in the task parameters. */
        const val MESSAGE_KEY: String = "MESSAGE"

        /** The key to indicate whether the email message is in HTML format, or plain text. */
        const val AS_HTML_KEY: String = "AS_HTML"

        /** The key for the email recipients to be copied on the email notification. */
        const val RECIPIENT_COPY_KEY: String = "CC"
    }
}

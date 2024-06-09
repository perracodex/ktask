/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.service.consumer.notification

import ktask.base.env.Tracer
import ktask.base.settings.AppSettings
import ktask.base.settings.config.sections.SchedulerSettings
import ktask.server.domain.entity.notification.email.EmailParams
import ktask.server.domain.service.consumer.AbsTaskConsumer
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.Email
import org.apache.commons.mail.HtmlEmail
import org.apache.commons.mail.SimpleEmail
import org.thymeleaf.context.Context

/**
 * Represents a scheduled task that processes emails.
 */
internal class EmailTaskConsumer : AbsTaskConsumer() {
    private val tracer = Tracer<EmailTaskConsumer>()

    override fun consume(payload: TaskPayload) {
        tracer.debug("Processing email task notification. ID: ${payload.taskId}")

        // Build the email message.

        val parameters: EmailParams = payload.additionalParams[PARAMETERS_KEY] as EmailParams

        val email: Email = if (parameters.template.isNullOrBlank()) {
            SimpleEmail().apply {
                setMsg(parameters.message!!)
            }
        } else {
            HtmlEmail().apply {
                // Set the variables to be used in the template.
                val context: Context = Context().apply {
                    setVariable("language", payload.recipient.language ?: "en")
                    setVariable("recipient", payload.recipient.target)
                    setVariable("subject", parameters.subject)
                    setVariable("name", payload.recipient.name)
                    setVariable("message", parameters.message)
                    setVariable("sender", parameters.sender)
                }

                // Generate the message from the template.
                val htmlMessage: String = loadTemplate(
                    type = TemplateType.EMAIL,
                    recipient = payload.recipient,
                    template = parameters.template,
                    context = context
                )

                setHtmlMsg(htmlMessage)
                setTextMsg("Your email client does not support HTML messages.")
            }
        }

        // Add recipients to be copied on the email notification.

        if (parameters.cc.isNotEmpty()) {
            email.addCc(*parameters.cc.toTypedArray())
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

        // Send the email notification.
        val result: String = email.send()
        tracer.debug("Email notification sent to ${payload.recipient.target}. Task ID: ${payload.taskId}. Result: $result")
    }
}

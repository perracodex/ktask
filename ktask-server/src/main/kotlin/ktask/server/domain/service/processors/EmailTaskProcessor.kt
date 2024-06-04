/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.service.processors

import ktask.base.env.Tracer
import ktask.base.settings.AppSettings
import ktask.base.settings.config.sections.EmailSettings
import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail

/**
 * Represents a scheduled task that processes emails.
 */
internal class EmailTaskProcessor : AbsTaskProcessor() {
    private val tracer = Tracer<EmailTaskProcessor>()

    override fun runTask(payload: TaskPayload) {
        tracer.debug("Processing email task notification. ID: ${payload.taskId}")

        runCatching {
            val emailSettings: EmailSettings = AppSettings.email
            val subject: String = payload.additionalParams[SUBJECT_KEY] as String

            val email = SimpleEmail()
            email.hostName = emailSettings.hostName
            email.setSmtpPort(emailSettings.setSmtpPort)
            email.authenticator = DefaultAuthenticator(emailSettings.username, emailSettings.password)
            email.isSSLOnConnect = emailSettings.isSSLOnConnect
            email.setFrom(emailSettings.username)
            email.subject = subject
            email.setMsg(payload.message)
            email.addTo(payload.recipient)

            email.send()
        }.onFailure { error ->
            tracer.error("Failed to send email notification: $error")
        }
    }

    companion object {
        /** The key for the email subject in the task parameters. */
        const val SUBJECT_KEY: String = "subject"
    }
}

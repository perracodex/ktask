/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.consumer.notification.task

import com.slack.api.Slack
import com.slack.api.methods.response.chat.ChatPostMessageResponse
import ktask.base.env.Tracer
import ktask.base.settings.AppSettings
import ktask.base.settings.config.sections.SchedulerSettings
import ktask.server.consumer.notification.AbsNotificationConsumer


/**
 * Represents a scheduled task that processes Slack messages.
 *
 * See: [Slack SDK](https://github.com/slackapi/java-slack-sdk)
 */
internal class SlackConsumer : AbsNotificationConsumer() {
    private val tracer = Tracer<SlackConsumer>()

    /**
     * Represents the concrete properties for the Slack task.
     */
    enum class Property(val key: String) {
        CHANNEL(key = "CHANNEL")
    }

    override fun consume(payload: TaskPayload) {
        tracer.debug("Processing Slack notification. ID: ${payload.taskId}")

        val channel: String = payload.additionalParameters[Property.CHANNEL.key] as String

        // Build the message.

        val message: String = buildMessage(
            type = TemplateType.SLACK,
            payload = payload,
        )

        // Append attachment links to the message.

        val attachmentLinks: String = payload.attachments.joinToString("\n") { attachmentUrl ->
            "Attachment: <${attachmentUrl}|Download>"
        }

        val finalMessage: String = if (attachmentLinks.isBlank()) {
            message
        } else {
            "$message\n$attachmentLinks"
        }

        // Send the Slack notification.

        val slackSpec: SchedulerSettings.SlackSpec = AppSettings.scheduler.slackSpec
        val slack: Slack = Slack.getInstance()
        val response: ChatPostMessageResponse = slack.methods(slackSpec.token).chatPostMessage { request ->
            request.channel(channel)
            request.username(payload.recipient.name)
            request.text(finalMessage)
        }

        // Handle the response.

        if (response.isOk) {
            tracer.debug("Slack notification sent to ${payload.recipient.target}. ID: ${payload.taskId}. Result: $response")
        } else {
            tracer.error("Failed to send Slack notification. ID: ${payload.taskId}. Response: $response")
        }
    }
}

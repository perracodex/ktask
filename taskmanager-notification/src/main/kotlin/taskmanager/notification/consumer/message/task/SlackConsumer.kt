/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package taskmanager.notification.consumer.message.task

import com.slack.api.Slack
import taskmanager.base.env.Tracer
import taskmanager.base.settings.AppSettings
import taskmanager.base.settings.catalog.section.CommunicationSettings
import taskmanager.notification.consumer.message.AbsNotificationConsumer

/**
 * Represents a scheduled task that processes Slack messages.
 *
 * #### References
 * - [Slack SDK](https://github.com/slackapi/java-slack-sdk)
 */
internal class SlackConsumer : AbsNotificationConsumer() {
    private val tracer: Tracer = Tracer<SlackConsumer>()

    /**
     * Represents the concrete properties for the Slack task.
     */
    enum class Property(val key: String) {
        CHANNEL(key = "CHANNEL")
    }

    override fun consume(payload: ConsumerPayload) {
        tracer.debug("Processing Slack notification. Group Id: ${payload.groupId} | Task Id: ${payload.taskId}")

        val slackSpec: CommunicationSettings.SlackSpec = AppSettings.communication.slackSpec
        verifySettings(spec = slackSpec)

        // Build the message.
        val message: String = buildMessage(
            type = TemplateType.SLACK,
            payload = payload,
        )

        // Append attachment links to the message.
        val attachmentLinks: String? = payload.attachments?.joinToString("\n") { attachmentUrl ->
            "Attachment: <$attachmentUrl|Download>"
        }

        val finalMessage: String = if (attachmentLinks.isNullOrBlank()) {
            message
        } else {
            "$message\n$attachmentLinks"
        }

        // Send the Slack notification.

        val channel: String = payload.additionalParameters[Property.CHANNEL.key] as? String
            ?: throw IllegalArgumentException("CHANNEL is missing or invalid.")

        Slack.getInstance().methods(slackSpec.token).chatPostMessage { request ->
            request.channel(channel)
            request.username(payload.recipient.name)
            request.text(finalMessage)
        }.also { response ->
            if (response.isOk) {
                tracer.debug(
                    "Slack notification sent to ${payload.recipient.target}. " +
                            "| Group Id: ${payload.groupId} " +
                            "| Task Id: ${payload.taskId} " +
                            "| Response: $response"
                )
            } else {
                throw IllegalStateException(
                    "Failed to send Slack notification. " +
                            "| Group Id: ${payload.groupId} " +
                            "| Task Id: ${payload.taskId} " +
                            "| Response: $response"
                )
            }
        }
    }

    /**
     * Verifies that the email settings values are valid.
     */
    private fun verifySettings(spec: CommunicationSettings.SlackSpec) {
        require(spec.token.isNotBlank()) { "Slack token is missing." }
    }
}

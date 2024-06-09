/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.service.consumer.notification

import com.slack.api.Slack
import com.slack.api.methods.response.chat.ChatPostMessageResponse
import ktask.base.env.Tracer
import ktask.base.settings.AppSettings
import ktask.base.settings.config.sections.SchedulerSettings
import ktask.server.domain.entity.notification.slack.SlackParams
import ktask.server.domain.service.consumer.AbsTaskConsumer


/**
 * Represents a scheduled task that processes Slack messages.
 *
 * See: [Slack SDK](https://github.com/slackapi/java-slack-sdk)
 */
internal class SlackTaskConsumer : AbsTaskConsumer() {
    private val tracer = Tracer<SlackTaskConsumer>()

    override fun consume(payload: TaskPayload) {
        tracer.debug("Processing Slack task notification. ID: ${payload.taskId}")

        val parameters: SlackParams = SlackParams.deserialize(
            string = payload.additionalParams[PARAMETERS_KEY] as String
        )

        // Build the message.

        val message: String = buildMessage(
            type = TemplateType.SLACK,
            recipient = payload.recipient,
            template = parameters.template,
            fields = parameters.fields
        )

        // Append attachment links to the message.

        val attachmentLinks: String? = parameters.attachments?.joinToString("\n") { attachmentUrl ->
            "Attachment: <${attachmentUrl}|Download>"
        }

        val finalMessage: String = if (attachmentLinks != null) {
            "$message\n\n$attachmentLinks"
        } else {
            message
        }

        // Send the Slack notification.

        val slackSpec: SchedulerSettings.SlackSpec = AppSettings.scheduler.slackSpec
        val slack: Slack = Slack.getInstance()
        val response: ChatPostMessageResponse = slack.methods(slackSpec.token).chatPostMessage { request ->
            request.channel(parameters.channel)
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

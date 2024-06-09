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
import org.thymeleaf.context.Context


/**
 * Represents a scheduled task that processes Slack messages.
 *
 * See: [Slack SDK](https://github.com/slackapi/java-slack-sdk)
 */
internal class SlackTaskConsumer : AbsTaskConsumer() {
    private val tracer = Tracer<SlackTaskConsumer>()

    override fun consume(payload: TaskPayload) {
        tracer.debug("Processing Slack task notification. ID: ${payload.taskId}")

        val slackSpec: SchedulerSettings.SlackSpec = AppSettings.scheduler.slackSpec
        val parameters: SlackParams = payload.additionalParams[PARAMETERS_KEY] as SlackParams

        val message: String = if (parameters.template.isNullOrBlank()) {
            parameters.message!!
        } else {
            // Set the variables to be used in the template.
            val context: Context = Context().apply {
                setVariable("recipient", payload.recipient.target)
                setVariable("name", payload.recipient.name)
                setVariable("message", parameters.message)
                setVariable("sender", parameters.sender)
            }

            // Generate the message from the template.
            loadTemplate(
                type = TemplateType.SLACK,
                recipient = payload.recipient,
                template = parameters.template,
                context = context
            )
        }

        val slack: Slack = Slack.getInstance()
        val response: ChatPostMessageResponse = slack.methods(slackSpec.token).chatPostMessage { request ->
            request.channel(parameters.channel)
            request.username(payload.recipient.name)
            request.text(message)
        }

        if (response.isOk) {
            tracer.debug("Slack notification sent to ${payload.recipient.target}. ID: ${payload.taskId}. Result: $response")
        } else {
            tracer.error("Failed to send Slack notification. ID: ${payload.taskId}. Response: $response")
        }
    }
}

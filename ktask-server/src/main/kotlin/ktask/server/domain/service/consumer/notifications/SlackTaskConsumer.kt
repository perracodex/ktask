/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.service.consumer.notifications

import com.slack.api.Slack
import com.slack.api.methods.response.chat.ChatPostMessageResponse
import ktask.base.env.Tracer
import ktask.base.settings.AppSettings
import ktask.base.settings.config.sections.SchedulerSettings
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

        val slackSpec: SchedulerSettings.SlackSpec = AppSettings.scheduler.slackSpec
        val channel: String = payload.additionalParams[CHANNEL_KEY] as String
        val message: String = payload.additionalParams[MESSAGE_KEY] as String

        val slack: Slack = Slack.getInstance()
        val response: ChatPostMessageResponse = slack.methods(slackSpec.token).chatPostMessage { request ->
            request.channel(channel)
            request.username(payload.recipient)
            request.text(message)
        }

        if (response.isOk) {
            tracer.debug("Successfully sent Slack notification. ID: ${payload.taskId}")
        } else {
            tracer.error("Failed to send Slack notification. ID: ${payload.taskId}. Response: $response")
        }
    }

    companion object {
        /** The key for the Slack channel in the task parameters. */
        const val CHANNEL_KEY: String = "CHANNEL"

        /** The key for the Slack message in the task parameters. */
        const val MESSAGE_KEY: String = "MESSAGE"
    }
}

/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.service.processors

import com.slack.api.Slack
import com.slack.api.methods.response.chat.ChatPostMessageResponse
import ktask.base.env.Tracer
import ktask.base.settings.AppSettings
import ktask.base.settings.config.sections.SlackSettings

/**
 * Represents a scheduled task that processes Slack messages.
 *
 * See: [Slack SDK](https://github.com/slackapi/java-slack-sdk)
 */
internal class SlackTaskProcessor : AbsTaskProcessor() {
    private val tracer = Tracer<SlackTaskProcessor>()

    override fun runTask(payload: TaskPayload) {
        tracer.debug("Processing slack task notification. ID: ${payload.taskId}")

        runCatching {
            val slackSettings: SlackSettings = AppSettings.slack
            val channel: String = payload.additionalParams[CHANNEL_KEY] as String

            val slack: Slack = Slack.getInstance()
            val response: ChatPostMessageResponse = slack.methods(slackSettings.token).chatPostMessage { request ->
                request.channel(channel)
                request.username(payload.recipient)
                request.text(payload.message)
            }

            if (response.isOk) {
                tracer.debug("Successfully sent Slack notification. ID: ${payload.taskId}")
            } else {
                tracer.error("Failed to send Slack notification. ID: ${payload.taskId}. Response: $response")
            }
        }.onFailure { error ->
            tracer.error("Failed to send Slack notification: $error")
        }
    }

    companion object {
        const val CHANNEL_KEY: String = "CHANNEL"
    }
}

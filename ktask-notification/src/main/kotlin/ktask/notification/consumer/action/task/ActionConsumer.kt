/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.notification.consumer.action.task

import ktask.base.env.Tracer
import ktask.notification.consumer.action.AbsActionConsumer

/**
 * Represents a scheduled task that processes an action.
 */
internal class ActionConsumer : AbsActionConsumer() {
    private val tracer: Tracer = Tracer<ActionConsumer>()

    /**
     * Represents the concrete properties for the action task.
     */
    enum class Property(val key: String) {
        DATA(key = "DATA"),
    }

    override fun consume(payload: ConsumerPayload) {
        tracer.debug("Processing action. Group Id: ${payload.groupId} | Task Id: ${payload.taskId}")

        val data: String = payload.additionalParameters[Property.DATA.key] as? String
            ?: throw IllegalArgumentException("DATA is missing or invalid.")

        tracer.debug("Action data: $data")
    }
}

/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.domain.service.consumer

import ktask.base.persistence.serializers.SUUID
import ktask.base.scheduler.service.task.SchedulerTask
import ktask.server.domain.entity.Recipient
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.FileTemplateResolver

/**
 * Abstract base class for processing scheduled tasks, implying the handling of task
 * execution by abstracting common task-related data extraction and pre-processing tasks.
 */
internal abstract class AbsTaskConsumer : SchedulerTask() {

    /**
     * Represents the type of template to load.
     *
     * @param mode The mode of the template (HTML or text).
     * @param suffix The suffix of the template file.
     * @param location The location of the template file.
     */
    protected enum class TemplateType(val mode: TemplateMode, val suffix: String, val location: String) {
        EMAIL(mode = TemplateMode.HTML, suffix = ".html", location = "email"),
        SLACK(mode = TemplateMode.TEXT, suffix = ".txt", location = "slack")
    }

    /**
     * Represents the data necessary for task processing, encapsulating task-specific parameters.
     *
     * @param taskId The unique identifier of the task.
     * @param recipient The target recipient of the task.
     * @param additionalParams A map of additional parameters required for the task.
     */
    data class TaskPayload(
        val taskId: SUUID,
        val recipient: Recipient,
        val additionalParams: Map<String, Any> = emptyMap()
    ) {
        companion object {
            fun map(properties: Map<String, Any>): TaskPayload {
                return TaskPayload(
                    taskId = properties[TASK_ID_KEY] as SUUID,
                    recipient = properties[RECIPIENT_KEY] as Recipient,
                    additionalParams = properties.filterKeys { key ->
                        key !in setOf(TASK_ID_KEY, RECIPIENT_KEY)
                    }
                )
            }
        }
    }

    override fun start(properties: Map<String, Any>) {
        val payload: TaskPayload = properties.let { TaskPayload.map(properties = it) }
        consume(payload = payload)
    }

    /**
     * Processes the task with the provided data.
     *
     * @param payload The [TaskPayload] containing the data required to process the task.
     */
    protected abstract fun consume(payload: TaskPayload)

    /**
     * Loads a template file and processes it with the provided context.
     * If the recipient has a language set, the template file will be resolved.
     * If the template file is not found, the task will fail.
     *
     * @param type The type of template to load.
     * @param recipient The recipient of the message.
     * @param template The name of the template file to load.
     * @param context The context to use for processing the template.
     * @return The processed template as a string.
     */
    protected fun loadTemplate(
        type: TemplateType,
        recipient: Recipient,
        template: String,
        context: Context
    ): String {
        // Resolve the template name based on the recipient's language.
        // Example: "simple-message" -> "simple-message-en"
        val targetTemplate: String = if (recipient.language.isNullOrBlank()) {
            context.setVariable("language", "en")
            template
        } else {
            context.setVariable("language", recipient.language)
            "$template-${recipient.language}"
        }

        // The task runs in a different class loader, so the template engine
        // doesn't have any resolvers set by default, even if the application
        // has them configured. We need to set the resolvers manually.
        val templateEngine: TemplateEngine = TemplateEngine().apply {
            addTemplateResolver(FileTemplateResolver().apply {
                prefix = "$PUBLIC_TEMPLATES/${type.location}/"
                suffix = type.suffix
                characterEncoding = "utf-8"
                templateMode = type.mode
            })
        }

        // Process the template with the context variables.
        // Note that if the template is not found, the task will fail.
        return templateEngine.process(targetTemplate, context)
    }

    companion object {
        /** The location of the public templates. */
        private const val PUBLIC_TEMPLATES = "public_templates"

        /** The key for the task ID in the payload. */
        const val TASK_ID_KEY: String = "TASK_ID"

        /** The key for the recipient in the payload. */
        const val RECIPIENT_KEY: String = "RECIPIENT"

        /** The key for the additional parameters in the payload. */
        const val PARAMETERS_KEY: String = "PARAMETERS"
    }
}

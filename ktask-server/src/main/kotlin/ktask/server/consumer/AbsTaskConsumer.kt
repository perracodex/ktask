/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.consumer

import ktask.base.persistence.serializers.SUUID
import ktask.base.scheduler.service.task.SchedulerTask
import ktask.base.settings.AppSettings
import ktask.base.utils.CastUtils
import ktask.base.utils.DateTimeUtils
import ktask.server.domain.entity.Recipient
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.FileTemplateResolver

/**
 * Abstract base class for processing scheduled tasks, providing common steps for task execution,
 * handling the extraction, pre-processing, and consumption of task-related data, including
 * the loading and processing of template files. Extending classes must implement the [consume]
 * method to define task-specific behavior.
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
        val template: String,
        val fields: Map<String, String> = emptyMap(),
        val attachments: List<String> = emptyList(),
        val additionalParams: Map<String, Any> = emptyMap()
    ) {
        companion object {
            fun map(properties: Map<String, Any>): TaskPayload {

                val taskId: SUUID = properties[TASK_ID_KEY] as SUUID
                val template: String = properties[TEMPLATE_KEY] as String
                val fields: Map<String, String> = CastUtils.toStringMap(map = properties[FIELDS_KEY])
                val attachments: List<String> = CastUtils.toStringList(list = properties[ATTACHMENTS_KEY])

                val recipient = Recipient(
                    target = properties[RECIPIENT_TARGET_KEY] as String,
                    name = properties[RECIPIENT_NAME_KEY] as String,
                    locale = properties[RECIPIENT_LOCALE_KEY] as String
                )

                // The consumer-specific parameters.
                val additionalParams = properties.filterKeys { key ->
                    key !in setOf(
                        ATTACHMENTS_KEY, FIELDS_KEY, TASK_ID_KEY, TEMPLATE_KEY,
                        RECIPIENT_TARGET_KEY, RECIPIENT_NAME_KEY, RECIPIENT_LOCALE_KEY
                    )
                }

                return TaskPayload(
                    taskId = taskId,
                    recipient = recipient,
                    template = template,
                    fields = fields,
                    attachments = attachments,
                    additionalParams = additionalParams
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
     *
     * @param type The type of template to load.
     * @param payload The [TaskPayload] containing the data to be used in the template.
     * @return The processed template as a string.
     */
    protected fun buildMessage(type: TemplateType, payload: TaskPayload): String {

        val locale: String = payload.recipient.locale.lowercase()

        // Set the variables to be used in the template.
        val context: Context = Context().apply {
            setVariable(LOCALE_PLACEHOLDER, locale)
            setVariable(RECIPIENT_PLACEHOLDER, payload.recipient.target)
            setVariable(NAME_PLACEHOLDER, payload.recipient.name)

            // Add the formatted/localized date to the context.
            val formattedDate: String = DateTimeUtils.localizedCurrentDate(language = locale)
            setVariable(DATE_PLACEHOLDER, formattedDate)

            // Set the additional fields in the context.
            // These fields are not bound to the consumer's payload,
            // but that may exist in the template.
            payload.fields.forEach { (key, value) -> setVariable(key.lowercase(), value) }
        }

        // Resolve the template name based on the recipient's language.
        val targetTemplate = "${payload.template}-${locale}"

        // The task runs in a different class loader, so the template engine
        // doesn't have any resolvers set by default, even if the application
        // has them configured. We need to set the resolvers manually.
        val templateEngine: TemplateEngine = TemplateEngine().apply {
            addTemplateResolver(FileTemplateResolver().apply {
                prefix = "${AppSettings.scheduler.templatesPath}/${type.location}/"
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
        const val ATTACHMENTS_KEY: String = "ATTACHMENTS"
        const val FIELDS_KEY: String = "FIELDS"
        const val RECIPIENT_LOCALE_KEY: String = "RECIPIENT_LOCALE"
        const val RECIPIENT_NAME_KEY: String = "RECIPIENT_NAME"
        const val RECIPIENT_TARGET_KEY: String = "RECIPIENT_TARGET"
        const val TASK_ID_KEY: String = "TASK_ID"
        const val TEMPLATE_KEY: String = "TEMPLATE"

        private const val LOCALE_PLACEHOLDER: String = "locale"
        private const val RECIPIENT_PLACEHOLDER: String = "recipient"
        private const val NAME_PLACEHOLDER: String = "name"
        private const val DATE_PLACEHOLDER: String = "date"
    }
}

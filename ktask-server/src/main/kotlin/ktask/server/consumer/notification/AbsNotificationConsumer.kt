/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.server.consumer.notification

import ktask.base.persistence.serializers.SUUID
import ktask.base.scheduler.service.task.SchedulerTask
import ktask.base.settings.AppSettings
import ktask.base.utils.CastUtils
import ktask.base.utils.DateTimeUtils
import ktask.server.domain.entity.notification.Recipient
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
internal abstract class AbsNotificationConsumer : SchedulerTask() {

    /**
     * Represents the properties used in the task payload.
     * These are the common properties shared by all task consumers.
     */
    enum class Property(val key: String) {
        ATTACHMENTS(key = "ATTACHMENTS"),
        FIELDS(key = "FIELDS"),
        RECIPIENT_LOCALE(key = "RECIPIENT_LOCALE"),
        RECIPIENT_NAME(key = "RECIPIENT_NAME"),
        RECIPIENT_TARGET(key = "RECIPIENT_TARGET"),
        TASK_ID(key = "TASK_ID"),
        TEMPLATE(key = "TEMPLATE")
    }

    /**
     * Represents the placeholders used in the template files.
     * These are tags in the template files that are replaced with the actual values.
     *
     * Keys must be in lowercase to match the templates context variables.
     */
    private enum class Placeholder(val key: String) {
        LOCALE(key = "locale"),
        RECIPIENT(key = "recipient"),
        NAME(key = "name"),
        DATE(key = "date")
    }

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
     * @param template The template to be used for the notification.
     * @param fields Optional fields to be included in the template.
     * @param attachments Optional list of file paths to be attached to the notification.
     * @param additionalParameters A map of additional parameters required for the task.
     */
    data class TaskPayload(
        val taskId: SUUID,
        val recipient: Recipient,
        val template: String,
        val fields: Map<String, String> = emptyMap(),
        val attachments: List<String> = emptyList(),
        val additionalParameters: Map<String, Any> = emptyMap()
    ) {
        companion object {
            fun build(properties: Map<String, Any>): TaskPayload {

                val taskId: SUUID = properties[Property.TASK_ID.key] as SUUID
                val template: String = properties[Property.TEMPLATE.key] as String
                val fields: Map<String, String> = CastUtils.toStringMap(map = properties[Property.FIELDS.key])
                val attachments: List<String> = CastUtils.toStringList(list = properties[Property.ATTACHMENTS.key])

                val recipient = Recipient(
                    target = properties[Property.RECIPIENT_TARGET.key] as String,
                    name = properties[Property.RECIPIENT_NAME.key] as String,
                    locale = properties[Property.RECIPIENT_LOCALE.key] as String
                )

                // Get the consumer-specific properties, which are not part of the common payload.
                val additionalParameters: Map<String, Any> = properties.filterKeys { key ->
                    key !in Property.entries.map { it.key }
                }

                return TaskPayload(
                    taskId = taskId,
                    recipient = recipient,
                    template = template,
                    fields = fields,
                    attachments = attachments,
                    additionalParameters = additionalParameters
                )
            }
        }
    }

    override fun start(properties: Map<String, Any>) {
        val payload: TaskPayload = properties.let { TaskPayload.build(properties = it) }
        consume(payload = payload)
    }

    /**
     * Processes the task with the provided payload.
     * Extending classes must implement this method to define
     * the task-specific consumption behavior.
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
            setVariable(Placeholder.LOCALE.key, locale)
            setVariable(Placeholder.RECIPIENT.key, payload.recipient.target)
            setVariable(Placeholder.NAME.key, payload.recipient.name)

            // Add the formatted/localized date to the context.
            val formattedDate: String = DateTimeUtils.localizedCurrentDate(language = locale)
            setVariable(Placeholder.DATE.key, formattedDate)

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
}

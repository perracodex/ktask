# [KTask](https://github.com/perracodex/KTask)

A [Quartz](https://github.com/quartz-scheduler) task manager and scheduler based notification system using the [Ktor](https://ktor.io/) framework.

---

## Preface

[KTask](https://github.com/perracodex/KTask) serves as a comprehensive example of a scheduler-based notification system.
It showcases dispatching of tasks at scheduled times, for example sending emails, Slack messages, or any other custom action.

The system allows for scheduling tasks to be sent either immediately or at a specified future time to multiple recipients.
If a task is sent with a past date, it will be dispatched either immediately or at the next available time.

For functionality, and concretely for the supplied notification samples, the necessary credentials must be configured
either for Email and/or Slack services. These credentials should be specified in the project's `application.conf`.

---

## Features

* Scheduling: Configure notifications for immediate delivery or scheduled for future deployment.
* Extendable: Dispatch notifications either via email or Slack. Easily extendable to other types of notifications or custom actions.
* Templates: Send notifications using dynamic templates for both HTML emails or rich Slack messages
* Multi-Language: Support for sending notifications in different languages, as long as the templates are provided.
* Administration: View, pause, resume, and delete scheduled tasks through dedicated REST endpoints.
* Dashboard: A dashboard sample is available to view and manage scheduled tasks.
* Audit: Keep track of all tasks execution history.
* Micrometer-Metrics: Ready for integration with monitoring tools like Prometheus or Grafana.

---
For convenience, it is included a *[Postman Collection](./.postman/ktask.postman_collection.json)* with all the available REST endpoints.

For credentials `Basic Authentication` is used. Default: `admin` / `admin`.
See: [security.conf](./ktask-core/base/src/main/resources/config/config_security.conf)

---

### Wiki

* ### [Workflow](./.wiki/01.workflow.md)
* ### [Dashboard](./.wiki/02.dashboard.md)
* ### [Language Support](./.wiki/03.language.md)
* ### [Message Template Fields](./.wiki/04.message_template_fields.md)
* ### [Cron Expressions](./.wiki/05.cron_expressions.md)
* ### [Email Notification Endpoints](./.wiki/06.email_notification_endpoints.md)
* ### [Slack Notification Endpoints](./.wiki/07.slack_notification_endpoints.md)
* ### [Administration Endpoints](./.wiki/08.administration_endpoints.md)

---

### License

This project is licensed under an MIT License - see the [LICENSE](LICENSE) file for details.

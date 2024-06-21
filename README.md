# [KTask](https://github.com/perracodex/KTask)

A [Quartz](https://github.com/quartz-scheduler) scheduler based notification system using the [Ktor](https://ktor.io/) framework.

---

## Preface

[KTask](https://github.com/perracodex/KTask) serves as a comprehensive example of a scheduler-based notification system.
It showcases dispatching of tasks at scheduled times, for example sending emails, Slack messages, or any other custom action.

The system allows for scheduling tasks to be sent either immediately or at a specified future time to multiple recipients.
If a task is sent with a past date, it will be dispatched either immediately or at the next available time.

For functionality, and concretely for the supplied notification samples, the necessary credentials must be configured
either for Email and/or Slack services. These credentials should be specified in the project's `application.conf`.

---

### Wiki

* ### [Features](./.wiki/01.features.md)
* ### [Workflow](./.wiki/02.workflow.md)
* ### [Dashboard](./.wiki/03.dashboard.md)
* ### [Language Support](./.wiki/04.language.md)
* ### [Message Template Fields](./.wiki/05.message_template_fields.md)
* ### [Cron Expressions](./.wiki/06.cron_expressions.md)
* ### [Email Notification Endpoints](./.wiki/07.email_notification_endpoints.md)
* ### [Slack Notification Endpoints](./.wiki/08.slack_notification_endpoints.md)
* ### [Administration Endpoints](./.wiki/09.administration_endpoints.md)

# [KTask](https://github.com/perracodex/KTask)

A [Quartz](https://github.com/quartz-scheduler) scheduler based notification system using the [Ktor](https://ktor.io/) framework.

---

### Preface

[KTask](https://github.com/perracodex/KTask) serves as a comprehensive example of a scheduler-based notification system.
It showcases dispatching of tasks at scheduled times, for example sending emails, Slack messages, or any other custom action.

The system allows for scheduling tasks to be sent either immediately or at a specified future time to multiple recipients.
If a task is sent with a past date, it will be dispatched either immediately or at the next available time.

For functionality, and concretely for the supplied notification samples, the necessary credentials must be configured
either for Email and/or Slack services. These credentials should be specified in the project's `application.conf` and `env` files.

---

### Features

* Scheduling: Configure notifications for immediate delivery or scheduled for future deployment.
* Extendable: Dispatch notifications either via email or Slack. Easily extendable to other types of notifications or custom actions.
* Administration: View, pause, resume, and delete scheduled tasks through dedicated REST endpoints.
* Dashboard: A dashboard sample is available to view and manage scheduled tasks.

---

For convenience, it is included a *[Postman Collection](./.postman/ktask.postman_collection.json)* with all the available REST endpoints.

---

### Workflow

<img src=".screenshots/workflow.jpg" alt="workflow">

---

### Dashboard

A dashboard to view and manage scheduled tasks is available at the endpoint: `/scheduler/tasks/dashboard`

<img src=".screenshots/dashboard.jpg" alt="dashboard">

---
### Notification Endpoints

#### Email

- **Endpoint**: `POST /push/email`
- **Description**: Send or schedule an email notification.
- **Request Body**:
  ```json
  {
    "id": "UUID",
    "schedule": "optional date-time",
    "recipients": ["email-addresses"],
    "subject": "string",
    "message": "string",
    "asHtml": "boolean",
    "cc": ["optional email-addresses"]
  }
  ```

#### Slack

- **Endpoint**: `POST /push/slack`
- **Description**: Send or schedule a Slack notification.
- **Request Body**:
  ```json
  {
    "id": "UUID",
    "schedule": "optional date-time",
    "recipients": ["string"],
    "channel": "string",
    "message": "string"
  }
  ```

---
### Administration Endpoints

#### List Scheduled Tasks

- **Endpoint**: `GET /scheduler/tasks`
- **Description**: Retrieve a list of all scheduled tasks.
- **Sample Output**:
  ```json
    [
      {
        "name": "task-1iq2xnjwmccg0-125193306528400",
        "group": "38befbfb-20a3-4bcd-91e1-a2c7240adfa0",
        "className": "EmailTaskProcessor",
        "nextFireTime": "2025-05-01T15:42:50",
        "state": "NORMAL",
        "isDurable": false,
        "shouldRecover": false,
        "dataMap": "[(RECIPIENT, someone@gmail.com), (MESSAGE, Hello World!), (TASK_ID, 38befbfb-20a3-4bcd-91e1-a2c7240adfa0), (SUBJECT, Something)]"
      }
    ]
  ```

#### List Task Groups

- **Endpoint**: `GET /scheduler/tasks/groups`
- **Description**: Retrieve a list of task groups.
- **Sample Output**:
  ```json
  [
    "38befbfb-20a3-4bcd-91e1-a2c7240adfa0"
  ]
  ```

#### Pause/Resume Scheduled Tasks

- **Endpoint**: `POST /scheduler/tasks/pause`
- **Endpoint**: `POST /scheduler/tasks/{taskId}/{taskGroup}/pause`
- **Endpoint**: `POST /scheduler/tasks/resume`
- **Endpoint**: `POST /scheduler/tasks/{taskId}/{taskGroup}/resume`
- **Description**: Pause or resume a scheduled task.
- **Sample Output**:
  ```json
  {
    "totalAffected": 6,
    "alreadyInState": 0,
    "totalTasks": 6
  }
  ```

#### Delete Scheduled Task

- **Endpoint**: `DELETE /scheduler/tasks/{taskId}/{taskGroup}`
- **Output**: Boolean indicating if the task was deleted.

#### Delete All Scheduled Tasks

- **Endpoint**: `DELETE /scheduler/tasks`
- **Output**: Total number of tasks deleted.

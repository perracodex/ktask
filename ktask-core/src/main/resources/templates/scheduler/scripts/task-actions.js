/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

function openFullAudit() {
    window.open('/admin/scheduler/audit', '_blank');
}

function openTaskAudit(button) {
    const groupId = encodeURIComponent(button.getAttribute('data-group-id'));
    const taskId = encodeURIComponent(button.getAttribute('data-task-id'));
    window.open(`/admin/scheduler/audit?groupId=${groupId}&taskId=${taskId}`, '_blank');
}

function refreshPage() {
    location.reload();
}

function deleteAll() {
    fetch(`/admin/scheduler`, {method: 'DELETE'})
        .then(response => {
            if (response.ok) {
                console.log('All tasks deleted successfully');
                window.location.reload();
            } else {
                console.error('Failed to delete all tasks');
                alert("Failed to delete all tasks.");
            }
        })
        .catch(error => console.error('Error:', error));
}

function deleteTask(button) {
    const groupId = encodeURIComponent(button.getAttribute('data-group-id'));
    const taskId = encodeURIComponent(button.getAttribute('data-task-id'));

    fetch(`/admin/scheduler/task?groupId=${groupId}&taskId=${taskId}`, {method: 'DELETE'})
        .then(response => {
            if (response.ok) {
                console.log('Task deleted successfully');
                window.location.reload();
            } else {
                console.error('Failed to delete task');
                alert("Failed to delete task.");
            }
        })
        .catch(error => console.error('Error:', error));
}

function openLog(element) {
    const log = element.getAttribute('data-log');
    const newWindow = window.open();
    newWindow.document.write(`
        <html lang="en">
            <head>
                <title>Log Details</title>
                <style>
                    body { font-family: Arial, sans-serif; font-size: 1.25em; background-color: #1e1e1e; color: #c7c7c7; }
                    pre { white-space: pre-wrap; word-wrap: break-word; }
                </style>
            </head>
            <body>
                <pre>${log}</pre>
            </body>
        </html>
    `);
    newWindow.document.close();
}

function toggleTaskPauseResume(button) {
    const groupId = encodeURIComponent(button.getAttribute('data-group-id'));
    const taskId = encodeURIComponent(button.getAttribute('data-task-id'));
    const state = button.getAttribute('data-state');
    const action = state === 'PAUSED' ? 'resume' : 'pause';

    fetch(`/admin/scheduler/task/${action}?groupId=${groupId}&taskId=${taskId}`, {method: 'POST'})
        .then(response => {
            if (response.ok) {
                console.log('Task paused/resumed successfully');
                window.location.reload();
            } else {
                console.error('Failed to pause/resume task');
                alert("Failed to pause/resume task.");
            }
        })
        .catch(error => console.error('Error:', error));
}

function resendTask(button) {
    const groupId = encodeURIComponent(button.getAttribute('data-group-id'));
    const taskId = encodeURIComponent(button.getAttribute('data-task-id'));

    fetch(`/admin/scheduler/task/resend?groupId=${groupId}&taskId=${taskId}`, {method: 'POST'})
        .then(response => {
            if (response.ok) {
                console.log('Task resent successfully');
                window.location.reload();
            } else {
                console.error('Failed to resend task');
                alert("Failed to pause/resume task.");
            }
        })
        .catch(error => console.error('Error:', error));
}

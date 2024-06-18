/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

const SCHEDULER_STATE = {
    RUNNING: 'RUNNING',
    PAUSED: 'PAUSED',
    STOPPED: 'STOPPED'
}

function setCookie(name, value, days) {
    let expires = "";
    if (days) {
        const date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "") + expires + "; path=/";
}

function getCookie(name) {
    const nameEQ = `${name}=`;
    const cookies = document.cookie.split(';');
    for (let i = 0; i < cookies.length; i++) {
        let cookie = cookies[i].trim();
        if (cookie.indexOf(nameEQ) === 0) {
            return cookie.substring(nameEQ.length, cookie.length);
        }
    }
    return null;
}

// Place this script as early as possible in your HTML document, ideally in the <head> section or right before </body>

document.addEventListener('DOMContentLoaded', () => {
    updateSchedulerPauseResumeButton();

    const pageIdentifier = window.location.pathname;

    // Restore expanded states from cookies
    document.querySelectorAll('.table-container').forEach(container => {
        const key = `${pageIdentifier}_${container.getAttribute('data-id')}`;
        const isExpandedInitially = getCookie(key) === 'true';
        const subRow = container.querySelector('.table-sub-row');

        // Set initial display based on cookie value
        subRow.style.display = isExpandedInitially ? 'block' : 'none';

        // Set initial inner HTML of the expand trigger based on initial display state
        const trigger = container.querySelector('.expand-trigger');
        trigger.innerHTML = isExpandedInitially ? '-' : '+';

        // Add click event listener to toggle expand/collapse
        trigger.addEventListener('click', function () {
            const subRow = container.querySelector('.table-sub-row');
            const isExpanded = getComputedStyle(subRow).display === 'block';
            subRow.style.display = isExpanded ? 'none' : 'block'; // Toggle display

            // Update cookie with new state
            setCookie(key, (!isExpanded).toString(), 1);

            // Update inner HTML of trigger based on new state
            trigger.innerHTML = isExpanded ? '+' : '-';
        });
    });
});

const eventsSource = new EventSource("/events");
eventsSource.onmessage = function (event) {
    const eventsContainer = document.getElementById("events");
    const eventsDiv = document.createElement("div");
    eventsDiv.textContent = event.data;
    eventsContainer.appendChild(eventsDiv);
    eventsContainer.scrollTop = eventsContainer.scrollHeight; // Auto scroll to bottom.
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

function openFullAudit() {
    window.open('/scheduler/audit', '_blank');
}

function openTaskAudit(button) {
    const itemName = encodeURIComponent(button.getAttribute('data-name'));
    const itemGroup = encodeURIComponent(button.getAttribute('data-group'));
    window.open(`/scheduler/audit/${itemName}/${itemGroup}`, '_blank');
}

function refreshPage() {
    location.reload();
}

function deleteAll() {
    fetch(`/scheduler/task`, {method: 'DELETE'})
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

function toggleTaskPauseResume(button) {
    const itemName = encodeURIComponent(button.getAttribute('data-name'));
    const itemGroup = encodeURIComponent(button.getAttribute('data-group'));
    const state = button.getAttribute('data-state');
    const action = state === 'PAUSED' ? 'resume' : 'pause';

    fetch(`/scheduler/task/${itemName}/${itemGroup}/${action}`, {method: 'POST'})
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

function deleteTask(button) {
    const itemName = encodeURIComponent(button.getAttribute('data-name'));
    const itemGroup = encodeURIComponent(button.getAttribute('data-group'));

    fetch(`/scheduler/task/${itemName}/${itemGroup}`, {method: 'DELETE'})
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

function toggleScheduler() {
    checkSchedulerState(function (state) {
        if (state.trim() === SCHEDULER_STATE.RUNNING) {
            pauseScheduler(confirmAndRefreshState);
        } else if (state.trim() === SCHEDULER_STATE.PAUSED) {
            resumeScheduler(confirmAndRefreshState);
        }
    });
}

function checkSchedulerState(callback) {
    console.log("Fetching scheduler state...");
    fetch('/scheduler/state', {
        method: 'GET',
        headers: {
            'Cache-Control': 'no-cache, no-store, must-revalidate',
            'Pragma': 'no-cache',
            'Expires': '0'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text();
        })
        .then(state => {
            console.log('Scheduler state:', state);
            callback(state);
        })
        .catch(error => {
            console.error('Error fetching scheduler state:', error);
            alert('Error fetching scheduler state. Check console for details.');
        });
}

function pauseScheduler(callback) {
    fetch('/scheduler/pause', {method: 'POST'})
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to pause scheduler');
            }
            console.log('Paused scheduler');
            callback();
        })
        .catch(error => {
            console.error('Error pausing scheduler:', error);
            alert('Error pausing scheduler. Check console for details.');
        });
}

function resumeScheduler(callback) {
    fetch('/scheduler/resume', {method: 'POST'})
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to resume scheduler');
            }
            console.log('Resumed scheduler');
            callback();
        })
        .catch(error => {
            console.error('Error resuming scheduler:', error);
            alert('Error resuming scheduler. Check console for details.');
        });
}

function confirmAndRefreshState() {
    checkSchedulerState(function (state) {
        setSchedulerButtonState(state);
        window.location.reload(); // Reload the page after state has been committed
    });
}

function setSchedulerButtonState(state) {
    const button = document.getElementById('toggleScheduler');
    console.log('Setting button state for:', state); // Debug log

    if (state.trim() === SCHEDULER_STATE.RUNNING) {
        button.textContent = 'Pause Scheduler';
        button.classList.add('pause');
        button.classList.remove('resume');
    } else if (state.trim() === SCHEDULER_STATE.PAUSED) {
        button.textContent = 'Resume Scheduler';
        button.classList.add('resume');
        button.classList.remove('pause');
    } else if (state.trim() === SCHEDULER_STATE.STOPPED) {
        button.textContent = 'Scheduler Stopped';
        button.disabled = true;
    }
}

function updateSchedulerPauseResumeButton() {
    checkSchedulerState(function (state) {
        setSchedulerButtonState(state);
    });
}

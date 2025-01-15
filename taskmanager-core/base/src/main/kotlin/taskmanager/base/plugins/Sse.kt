/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package taskmanager.base.plugins

import io.ktor.server.application.*
import io.ktor.server.sse.*

/**
 * Configures SSE (Server-Sent Events) plugin allowing to server to continuously
 * push events to a client over an HTTP connection.
 *
 * Useful in cases where the server needs to send event-based updates without
 * requiring the client to repeatedly poll the server.
 *
 * #### References
 * - [SSE Plugin](https://ktor.io/docs/server-server-sent-events.html)
 */
public fun Application.configureSse() {
    install(plugin = SSE)
}

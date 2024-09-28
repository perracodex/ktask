/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

package ktask.core.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import ktask.core.settings.AppSettings
import ktask.core.settings.catalog.sections.security.sections.BasicAuthSettings

/**
 * Configures the Basic authentication.
 *
 * The Basic authentication scheme is a part of the HTTP framework used for access control and authentication.
 * In this scheme, actor credentials are transmitted as username/password pairs encoded using Base64.
 *
 * #### References
 * - [Basic Authentication](https://ktor.io/docs/server-basic-auth.html)
 */
public fun Application.configureBasicAuthentication() {

    authentication {
        basic(name = AppSettings.security.basicAuth.providerName) {
            realm = AppSettings.security.basicAuth.realm

            validate { credential ->
                val basicAuth: BasicAuthSettings = AppSettings.security.basicAuth
                val isVerified: Boolean = (credential.name == basicAuth.username) && (credential.password == basicAuth.password)
                if (isVerified) UserIdPrincipal(name = credential.name) else null
            }
        }
    }
}

/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

group = "taskmanager.notification"
version = "1.0.0"

dependencies {
    implementation(project(":taskmanager-core:base"))
    implementation(project(":taskmanager-core:scheduler"))

    detektPlugins(libs.detekt.formatting)

    implementation(libs.kopapi)

    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization)

    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.html.builder)
    implementation(libs.ktor.server.rateLimit)
    implementation(libs.ktor.server.thymeleaf)

    implementation(libs.quartz.scheduler)

    implementation(libs.shared.commons.codec)
    implementation(libs.shared.commons.email)

    implementation(libs.slack.api.client)

    testImplementation(libs.test.kotlin.junit)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.mockito.kotlin)
}

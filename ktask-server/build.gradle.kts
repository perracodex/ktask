/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

group = "ktask.server"
version = "1.0.0"

dependencies {

    implementation(project(":ktask-base"))

    implementation(libs.kotlinx.datetime)

    implementation(libs.dotenv)

    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.html.builder)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.rateLimit)
    implementation(libs.ktor.server.tests)

    implementation(libs.quartz.scheduler)

    implementation(libs.slack.api.client)

    implementation(libs.shared.commons.codec)
    implementation(libs.shared.commons.email)
    implementation(libs.shared.gson)

    testImplementation(libs.test.kotlin.junit)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.mockito.kotlin)
    testImplementation(libs.exposed.core)
    testImplementation(libs.exposed.kotlin.datetime)
}

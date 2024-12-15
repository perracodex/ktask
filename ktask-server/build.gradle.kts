/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

group = "ktask.server"
version = "1.0.0"

dependencies {
    implementation(project(":ktask-core"))
    implementation(project(":ktask-notification"))

    detektPlugins(libs.detekt.formatting)

    implementation(libs.exposed.core)

    implementation(libs.hikariCP)

    implementation(libs.kopapi)

    implementation(libs.kotlinx.atomicfu)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization)

    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.rateLimit)

    implementation(libs.ktor.config)

    testImplementation(libs.test.kotlin.junit)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.mockito.kotlin)
}

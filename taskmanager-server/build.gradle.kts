/*
 * Copyright (c) 2024-Present Perracodex. Use of this source code is governed by an MIT license.
 */

group = "taskmanager.server"
version = "1.0.0"

dependencies {
    implementation(project(":taskmanager-core"))
    implementation(project(":taskmanager-notification"))

    detektPlugins(libs.detekt.formatting)

    implementation(libs.kopapi)

    implementation(libs.kotlinx.atomicfu)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization)

    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.rateLimit)

    testImplementation(libs.test.kotlin.junit)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.mockito.kotlin)
}

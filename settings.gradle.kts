pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    // https://github.com/gradle/foojay-toolchains
    // https://github.com/gradle/foojay-toolchains/tags
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "TaskManager"

include("taskmanager-core:base")
include("taskmanager-core:database")
include("taskmanager-core:scheduler")
include("taskmanager-core")
include("taskmanager-notification")
include("taskmanager-server")
